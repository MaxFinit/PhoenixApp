package com.maxfin.phoenixapp;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

public class XMPPServerConnection implements ConnectionListener {
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


    @Override
    public void connected(XMPPConnection connection) {

    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {

    }

    @Override
    public void connectionClosed() {

    }

    @Override
    public void connectionClosedOnError(Exception e) {

    }
}
