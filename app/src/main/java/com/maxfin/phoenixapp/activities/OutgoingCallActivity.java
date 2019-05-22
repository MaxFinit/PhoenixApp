package com.maxfin.phoenixapp.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maxfin.phoenixapp.OnStateCallback;
import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.managers.SipServerManager;
import com.maxfin.phoenixapp.managers.StateManager;

public class OutgoingCallActivity extends AppCompatActivity {
    private static final String TAG = "OutgoingCallActivity";
    private FloatingActionButton mEndCallButton;
    private SipServerManager mSipConnectionManager;
    private ImageView mPhotoImageView;
    private TextView mStateTextView;
    private TextView mNameTextView;
    private StateManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_call);
        mEndCallButton = findViewById(R.id.end_call_button);
        mStateTextView = findViewById(R.id.state_text);
        mNameTextView = findViewById(R.id.name_output_call_text);
        mPhotoImageView = findViewById(R.id.outgoing_call_image);


        Resources resources = getResources();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPhotoImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_avatar));
        } else {
            mPhotoImageView.setImageDrawable(resources.getDrawable(R.drawable.ic_contact_circle_api2));
        }


        manager = StateManager.getStateManager();
        mSipConnectionManager = SipServerManager.getSipServerManager(getApplicationContext());
        if (!mSipConnectionManager.isInCall())
            mSipConnectionManager.initiateCall();


        mEndCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSipConnectionManager.isInCall()) {
                    mSipConnectionManager.endCall();
                    onBackPressed();
                }
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


}
