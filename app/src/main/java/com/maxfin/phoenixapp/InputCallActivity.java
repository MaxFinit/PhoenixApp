package com.maxfin.phoenixapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class InputCallActivity extends AppCompatActivity {
    SipConnectionManager mSipConnectionManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_call);

        mSipConnectionManager = SipConnectionManager.getSipConnectionManager(getApplicationContext());
        mSipConnectionManager.initCall("+380713222303@172.16.13.223");





    }
}
