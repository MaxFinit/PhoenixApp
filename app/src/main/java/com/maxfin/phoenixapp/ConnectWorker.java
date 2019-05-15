package com.maxfin.phoenixapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.maxfin.phoenixapp.managers.StateManager;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ConnectWorker extends Worker {
    private static final String TAG = "ConnectWorker";
    private Context mContext;
    private StateManager mStateManager;

    public ConnectWorker(@NonNull Context context,
                         @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mStateManager = StateManager.getStateManager();
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d(TAG, "START");
        if (mStateManager.getConnectionXMPPState() == XMPPServerConnection.ConnectionXMPPState.DISCONNECTED) {
            Intent intent = new Intent(mContext, XMPPConnectionService.class);
            mContext.startService(intent);
        }

        return Result.success();
    }
}
