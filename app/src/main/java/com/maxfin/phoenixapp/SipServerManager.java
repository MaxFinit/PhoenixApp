package com.maxfin.phoenixapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.net.sip.SipSession;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;

public class SipServerManager {
    private static final String TAG = "SipServerManager";

    private SipManager mManager = null;
    private SipProfile mLocalProfile = null;
    private SipAudioCall mCall = null;
    private SipSession mSipSession = null;
    public static SipServerManager sSipServerManager;
    private Context mContext;
    private static ConnectionState sConnectionState;
    private static LoggedInState sLoggedInState;
    private static CallState sCallState;


    private OnStateCallback mListener;

    public void onStateChanged(OnStateCallback eventListener) {
        OutputCallActivity.sCallingState = sCallState;
        this.mListener = eventListener;
        this.mListener.onStateChanged();
    }


    public enum ConnectionState {
        CONNECTED, CONNECTION, FAILED
    }

    public enum LoggedInState {
        AUTHENTICATION, DONE, FAILED
    }

    public enum CallState {
        BUSE, ENDED, ERROR, ESTABLISHED, CALLING, HELD, RINGING, RINGINGBACK, CHANGED
    }

    public SipServerManager(Context context) {
        mContext = context.getApplicationContext();
        initializeManager(context);
    }

    public static SipServerManager getSipServerManager(Context context) {
        if (sSipServerManager == null) {
            sSipServerManager = new SipServerManager(context);
        }
        return sSipServerManager;
    }

    public SipManager getManager() {
        return mManager;
    }

    private void initializeManager(Context context) {
        if (mManager == null) {
            mManager = SipManager.newInstance(context);
        }

        initializeLocalProfile();

    }

    private void initializeLocalProfile() {
        if (mManager == null) {
            return;
        }

        if (mLocalProfile != null) {
            closeLocalProfile();
        }


        try {
            SipProfile.Builder builder = new SipProfile.Builder("76920", "172.16.13.223");
            builder.setPassword("238219325823bd838c23d9db90ee32cd");
            builder.setAutoRegistration(false);
            mLocalProfile = builder.build();

            SipRegistrationListener listener = new SipRegistrationListener() {
                @Override
                public void onRegistering(String localProfileUri) {
                    sLoggedInState = LoggedInState.AUTHENTICATION;
                    logger("AUTHENTICATION..." + localProfileUri);
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    sLoggedInState = LoggedInState.DONE;
                    logger("AUTHENTICATION DONE " + expiryTime);

                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    sLoggedInState = LoggedInState.FAILED;
                    logger("AUTHENTICATION FAILED " + errorCode + errorMessage);
                }
            };


            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, Intent.FILL_IN_DATA);


            mManager.open(mLocalProfile, pi, null);

            mManager.register(mLocalProfile, 30, listener);


        } catch (SipException e) {
            e.printStackTrace();
            logger("CONNECTION ERROR" + e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            logger("CONNECTION ERROR" + e.getMessage());
        }


    }

    private void closeLocalProfile() {
        if (mManager == null) {
            return;
        }

        try {
            mManager.close(mLocalProfile.getUriString());
        } catch (SipException e) {
            e.printStackTrace();
            logger("FAILED TO CLOSE LOCAL PROFILE" + e.getMessage());
        }


    }

    public void initiateCall() {

        sCallState = CallState.CALLING;
        logger("INIT CALL");

        if (mCall != null && mCall.isInCall()) {
            Toast.makeText(mContext, "You're currently busy...", Toast.LENGTH_SHORT);
            return;
        }

        SipAudioCall.Listener listener = new SipAudioCall.Listener() {

            @Override
            public void onCallEstablished(SipAudioCall call) {
                call.startAudio();
                call.setSpeakerMode(true);
                //          call.toggleMute();
                sCallState = CallState.ESTABLISHED;
                logger("CALL ESTABLISHED");
                onStateChanged(mListener);
            }

            @Override
            public void onCalling(SipAudioCall call) {
                super.onCalling(call);
                sCallState = CallState.CALLING;
                onStateChanged(mListener);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                logger("onCallEnded");
                sCallState = CallState.ENDED;
                onStateChanged(mListener);
                super.onCallEnded(call);
                endCall();
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                super.onError(call, errorCode, errorMessage);
                call.close();
                sCallState = CallState.ERROR;
                logger("CALL ERROR" + errorCode + errorMessage);
                onStateChanged(mListener);

            }

            @Override
            public void onReadyToCall(SipAudioCall call) {
                logger("onReadyToCall");
            }

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                logger("onRinging");
            }

            @Override
            public void onRingingBack(SipAudioCall call) {
                logger("onRingingBack");
            }

            @Override
            public void onCallBusy(SipAudioCall call) {
                logger("onCallBusy");
            }

            @Override
            public void onCallHeld(SipAudioCall call) {
                logger("onCallHeld");
            }

            @Override
            public void onChanged(SipAudioCall call) {
                logger("onChanged");
            }
        };

        try {
            SipProfile.Builder builder = new SipProfile.Builder("+380713222303", "172.16.13.223");
            SipProfile profile = builder.build();

            mCall = mManager.makeAudioCall(mLocalProfile,
                    profile, listener, 20);


        } catch (SipException e) {
            logger("ERROR WHEN TRYING CALL");
            sCallState = CallState.ERROR;
            onStateChanged(mListener);
            e.printStackTrace();
            if (mLocalProfile != null)
                closeLocalProfile();
            if (mCall != null) {
                mCall.close();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void endCall() {

        try {
            mCall.endCall();
        } catch (SipException e) {
            e.printStackTrace();
        }
        mCall.close();
    }


    private void logger(String logMessage) {
        Log.d(TAG, logMessage);
    }

}
