package com.maxfin.phoenixapp;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

public class XMPPServerConnection implements ConnectionListener,ReconnectionListener {
    private static final String TAG = "XMPPServerConnection";
    private final Context mApplicationContext;
    private final String mUsername;
    private final String mPassword;
    private final String mServiceName;
    private XMPPTCPConnection mConnection;



    public static enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED;
    }

    public static enum LoggedInState {
        LOGGED_IN, LOGGED_OUT;
    }


    public XMPPServerConnection(Context context) {
        Log.d(TAG, "XMPPServerConnection constructor вызван");
        mApplicationContext = context.getApplicationContext();
        String jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_jid", null);
        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext)
                .getString("xmpp_password", null);

        if (jid != null) {
            mUsername = jid.split("@")[0];
            mServiceName = jid.split("@")[1];
        } else {
            mUsername = "";
            mServiceName = "";
        }


    }


    public void connect() throws IOException, XMPPException, SmackException {
        Log.d(TAG, "Connection to server");
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        builder.setXmppDomain(mServiceName);
        builder.setUsernameAndPassword(mUsername,mPassword);
        //builder.setHostAddress()
        builder.setResource("Resource");
        builder.setKeystorePath("");

        // setupUiThreadBroadCastMessageReceiver();

        mConnection = new XMPPTCPConnection(builder.build());
        mConnection.addConnectionListener(this);
        try {
            Log.d(TAG,"Попытка подключения");
            mConnection.connect();
            mConnection.login();
            Log.d(TAG,"Залогинились, ура");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(mConnection);
        reconnectionManager.setEnabledPerDefault(true);
        reconnectionManager.enableAutomaticReconnection();

    }


    public void disconnect(){
        Log.d(TAG,"Disconnecting from serser "+ mServiceName);
        if (mConnection != null)
        {
            mConnection.disconnect();
        }

        mConnection = null;
    }


    @Override
    public void connected(XMPPConnection connection) {
        XMPPConnectionService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Connected Successfully");

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        XMPPConnectionService.sConnectionState=ConnectionState.CONNECTED;
        Log.d(TAG,"Authenticated Successfully");


    }

    @Override
    public void connectionClosed() {
        XMPPConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"Connectionclosed()");

    }

    @Override
    public void connectionClosedOnError(Exception e) {
        XMPPConnectionService.sConnectionState=ConnectionState.DISCONNECTED;
        Log.d(TAG,"ConnectionClosedOnError, error "+ e.toString());

    }


    @Override
    public void reconnectingIn(int seconds) {
        XMPPConnectionService.sConnectionState = ConnectionState.CONNECTING;
        Log.d(TAG,"ReconnectingIn() ");

    }



    @Override
    public void reconnectionFailed(Exception e) {
        XMPPConnectionService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.d(TAG,"ReconnectionFailed()");

    }


}
