package com.maxfin.phoenixapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
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
import com.maxfin.phoenixapp.managers.SipServerManager;
import com.maxfin.phoenixapp.managers.StateManager;

import java.net.URI;

public class OutgoingCallActivity extends AppCompatActivity {
    private static final String TAG = "OutgoingCallActivity";

    private static final String NAME_KEY = "name_key";
    private static final String NUMBER_KEY = "number_key";
    private static final String PHOTO_KEY = "photo_key";

    private SipServerManager mSipConnectionManager;
    private ImageView mPhotoImageView;
    private TextView mStateTextView;
    private TextView mNameTextView;
    private Chronometer mChronometer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);

        mStateTextView = findViewById(R.id.state_text);
        mNameTextView = findViewById(R.id.name_output_call_text);
        mPhotoImageView = findViewById(R.id.outgoing_call_image);

        Intent intent = getIntent();
        String name = intent.getStringExtra(NAME_KEY);
        String number = intent.getStringExtra(NUMBER_KEY);
        String photo = intent.getStringExtra(PHOTO_KEY);


        if (number == null) {
            number = "+380713222303";
        }

        Log.d(TAG,number);


//        mPhotoImageView.setImageURI(Uri.parse(photo));
//        mNameTextView.setText(name);


//        Resources resources = getResources();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mPhotoImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_avatar));
//        } else {
//            mPhotoImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_contact_circle_api2));
//        }

        mSipConnectionManager = SipServerManager.getSipServerManager(getApplicationContext());
        if (!mSipConnectionManager.isInCall())
            mSipConnectionManager.initiateCall(number);


        FloatingActionButton endCallButton = findViewById(R.id.end_call_button);
        endCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "CALL TRY CLOSING" + mSipConnectionManager.isInCall());
                if (mSipConnectionManager.isInCall()) {
                    Log.d(TAG, "CALL CLOSED");
                    mChronometer.stop();
                    mSipConnectionManager.endCall();
                    onBackPressed();
                }
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
        mSipConnectionManager.endCall();
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
