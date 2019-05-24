package com.maxfin.phoenixapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.maxfin.phoenixapp.OnStateCallback;
import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.Utils;
import com.maxfin.phoenixapp.managers.SipServerManager;
import com.maxfin.phoenixapp.managers.StateManager;

public class OutgoingCallActivity extends AppCompatActivity {
    private static final String TAG = "OutgoingCallActivity";


    private SipServerManager mSipConnectionManager;
    private TextView mStateTextView;
    private Chronometer mChronometer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);
        mStateTextView = findViewById(R.id.state_text);

        Intent intent = getIntent();
        String name = intent.getStringExtra(Utils.NAME_KEY);
        String number = intent.getStringExtra(Utils.NUMBER_KEY);
        String photo = intent.getStringExtra(Utils.PHOTO_KEY);

//////////
        if (number == null) {
            number = "071  322 23 03";
        }
//////////


        number = number.replaceAll("\\s", "");
        Log.d(TAG, number);

        if (name != null || photo != null) {
            ImageView photoImageView = findViewById(R.id.outgoing_call_image);
            photoImageView.setImageURI(Uri.parse(photo));
            TextView nameTextView = findViewById(R.id.name_output_call_text);
            nameTextView.setText(name);
        }


        mSipConnectionManager = SipServerManager.getSipServerManager(getApplicationContext());
        mSipConnectionManager.initiateCall(number);


        FloatingActionButton endCallButton = findViewById(R.id.end_call_button);
        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CALL CLOSED");
                mChronometer.stop();
                mSipConnectionManager.endCall();
                onBackPressed();
            }
        });

        mChronometer = findViewById(R.id.call_time_chronometer);


        mSipConnectionManager.onSipStateCallChanged(new OnStateCallback() {
            @Override
            public void onStateChanged() {

                switch (StateManager.getStateManager().getCallSIPState()) {

                    case BUSE:
                        updateTextView("Занято");
                        break;

                    case ESTABLISHED:
                        startClock();
                        updateTextView("Звонок");
                        break;

                    case HELD:
                        updateTextView("На удержании");
                        break;

                    case CALLING:
                        updateTextView("Звоним");
                        break;

                    case ENDED:
                        mChronometer.stop();
                        updateTextView("Звонок закончен");
                        Intent intent = new Intent(OutgoingCallActivity.this, CallActivity.class);
                        startActivity(intent);
                        break;

                    case ERROR:
                        updateTextView("Ошибка");
                        Intent intent2 = new Intent(OutgoingCallActivity.this, CallActivity.class);
                        startActivity(intent2);
                        break;
                }
            }

        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //mSipConnectionManager.endCall();
    }

    private void updateTextView(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStateTextView.setText(string);
            }
        });
    }


    private void startClock() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mChronometer.setBase(SystemClock.elapsedRealtime());
                mChronometer.start();
            }
        });

    }


}
