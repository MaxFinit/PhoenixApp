package com.maxfin.phoenixapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.maxfin.phoenixapp.managers.StateManager;

public class OutgoingCallActivity extends AppCompatActivity {
    private static final String TAG = "OutgoingCallActivity";
    private FloatingActionButton mEndCallButton;
    private SipServerManager mSipConnectionManager;
    private TextView mStateTextView;
    private TextView mNameTextView;
    private StateManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_call);
        mEndCallButton = findViewById(R.id.end_call_button);
        mStateTextView = findViewById(R.id.state_text);
        mNameTextView = findViewById(R.id.name_output_call_text);

        manager = StateManager.getStateManager();
        mSipConnectionManager = SipServerManager.getSipServerManager(getApplicationContext());
        mSipConnectionManager.initiateCall();


        mEndCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSipConnectionManager.endCall();
                onBackPressed();
            }
        });


        mSipConnectionManager.onSipStateCallChanged(new OnStateCallback() {
            @Override
            public void onStateChanged() {


                switch (manager.getCallSIPState()) {

                    case BUSE:
                        updateTextView("");
                        break;

                    case ESTABLISHED:
                        updateTextView("Звонок");


                        break;

                    case HELD:
                        updateTextView("На удержании");
                        break;

                    case CALLING:
                        updateTextView("Звоним");
                        break;


                    case ENDED:
                        updateTextView("Звонок закончен");
                        Intent intent = new Intent(OutgoingCallActivity.this, CallActivity.class);
                        startActivity(intent);
                        break;


                    case ERROR:
                        updateTextView("Ошибка");
                        break;

                }


            }

        });

    }


    private void updateTextView(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStateTextView.setText(string);
            }
        });
    }


}
