package com.maxfin.phoenixapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class XMPPConnectionService extends Service {
    private static final String TAG = "XMPPConnectionService";

    public static final String SEND_MESSAGE = "com.maxfin.phoenixapp.sendmessage";
    public static final String RESEIVED_MESSAGE = "com.maxfin.phoenixapp.reseivedmessage";
    public static final String BUNDLE_MESSAGE_BODY = "b_body";
    public static final String BUNDLE_TO = "b_to";

    public static final String NEW_MESSAGE = "com.maxfin.phoenixapp.newmessage";
//    public static final String BUNDLE_FROM_JID = "b_from";


    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;
    private XMPPServerConnection mConnection;

    public XMPPConnectionService() {
    }

//    public static XMPPServerConnection.ConnectionXMPPState getConnectionXMPPState() {
//        if (sConnectionXMPPState == null) {
//            return XMPPServerConnection.ConnectionXMPPState.DISCONNECTED;
//        }
//        return sConnectionXMPPState;
//    }
//
//    public static XMPPServerConnection.LoggedInXMPPState getLoggedInXMPPState() {
//        if (sLoggedInXMPPState == null) {
//            return XMPPServerConnection.LoggedInXMPPState.LOGGED_OUT;
//        }
//        return sLoggedInXMPPState;
//    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
    }

    private void initConnection() {

        Log.d(TAG, "Init connection");

        if (mConnection == null) {
            mConnection = XMPPServerConnection.getXMPPServerConnection(this);
        }

        try {
            mConnection.connect();
        } catch (IOException | SmackException | XMPPException e) {
            Log.d(TAG, "Something went wrong while connecting ,make sure the credentials are right and try again");
            e.printStackTrace();
            //Stop the service all together.
            stopSelf();
        }

    }


    public void start() {
        Log.d(TAG, " Service Start() функция вызвана.");
        if (!mActive) {
            mActive = true;
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        Looper.loop();
                    }
                });
                mThread.start();
            }
        }
    }

    public void stop() {
        Log.d(TAG, "STOP()!");
        mActive = false;
        mTHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConnection != null) {
                    mConnection.disconnect();
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "OnStartCommand()");
        start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        stop();

    }
}
