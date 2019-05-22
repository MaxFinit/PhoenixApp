package com.maxfin.phoenixapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;


public class StateConnectBroadcast extends BroadcastReceiver {
    private static final String TAG = "StateConnectBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Start receiver");


        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

         android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isConnected()) {

            Log.d(TAG, "Restart service");
            Intent i1 = new Intent(context, XMPPConnectionService.class);
            context.startService(i1);


        }


    }
}
