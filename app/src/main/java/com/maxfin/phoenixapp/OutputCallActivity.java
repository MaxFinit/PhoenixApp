package com.maxfin.phoenixapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class OutputCallActivity extends AppCompatActivity {
    private static final String TAG = "OutputCallActivity";
    public static SipConnectionManager.CallingState sCallingState;
    private SipConnectionManager mSipConnectionManager;
    private FloatingActionButton mEndCallButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_call);
        mEndCallButton = findViewById(R.id.end_call_button);







//        mSipConnectionManager = SipConnectionManager.getSipConnectionManager(getApplicationContext());
//        mSipConnectionManager.initCall("+380713222303@172.16.13.223");


//        mEndCallButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSipConnectionManager.endCall();
//                onBackPressed();
//            }
//        });
//
//        mSipConnectionManager.setCustomEventListener(new SipConnectionManager.onCustomEventListener() {
//            @Override
//            public void onEvent() {
//                mSipConnectionManager.endCall();
//                onBackPressed();
//            }
//        });
//
    }
}
