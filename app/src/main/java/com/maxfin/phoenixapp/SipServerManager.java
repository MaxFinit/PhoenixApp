package com.maxfin.phoenixapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;

public class SipServerManager {
    private static final String TAG = "SipServerManager";

    public SipManager mManager = null;
    public SipProfile mLocalProfile = null;
    public SipAudioCall mCall = null;
    private Context mContext;
    private static connectionState sConnectionState;
    private static loggedInState sLoggedInState;
    private static callState sCallState;

    public static connectionState getConnectionState() {
        return sConnectionState;
    }

    public static loggedInState getLoggedInState() {
        return sLoggedInState;
    }

    public static callState getCallState() {
        return sCallState;
    }

    public enum connectionState {
        CONNECTED, CONNECTION, FAILED
    }

    public enum loggedInState {
        AUTHENTICATION, DONE, FAILED
    }

    public enum callState {
        BUSE, ENDED, ERROR, ESTABLISHED, CALLING, HELD, RINGING, RINGINGBACK, CHANGED
    }

    public SipServerManager(Context context) {
        mContext = context;
        initializeManager(context);
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
                    sLoggedInState = loggedInState.AUTHENTICATION;
                    logger("AUTHENTICATION..." + localProfileUri);
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    sLoggedInState = loggedInState.DONE;
                    logger("AUTHENTICATION DONE " + expiryTime);

                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    sLoggedInState = loggedInState.FAILED;
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
                call.toggleMute();
                sCallState = callState.ESTABLISHED;
                logger("CALL ESTABLISHED");
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                super.onError(call, errorCode, errorMessage);
                call.close();
                sCallState = callState.ERROR;
                logger("CALL ERROR" + errorCode + errorMessage);

            }
        };

        try {
            SipProfile.Builder builder = new SipProfile.Builder("+380713222303", "172.16.13.223");
            SipProfile profile = builder.build();

            mCall = mManager.makeAudioCall(mLocalProfile,
                    profile, listener, 20);
        } catch (SipException e) {
            logger("ERROR WHEN TRYING CALL");
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


    private void logger(String logMessage) {
        Log.d(TAG, logMessage);
    }

}
