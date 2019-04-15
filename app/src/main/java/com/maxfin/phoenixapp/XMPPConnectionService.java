package com.maxfin.phoenixapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

public class XMPPConnectionService extends Service {
    private static final String TAG = "XMPPConnectionService";
    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;

    public XMPPConnectionService() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
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
                        //initConnection()
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
                //Код для дисконекта от сервера
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"OnStartCommand()");
        start();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
        stop();
    }
}
