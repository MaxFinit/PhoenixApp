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

import com.maxfin.phoenixapp.managers.StateManager;

import java.text.ParseException;

public class SipServerManager {
    private static final String TAG = "SipServerManager";

    private SipManager mManager = null;
    private SipProfile mLocalProfile = null;
    private SipAudioCall mCall = null;
    private SipSession mSipSession = null;
    private static SipServerManager sSipServerManager;
    private Context mContext;
    private static ConnectionSIPState sConnectionSIPState;
    private static LoggedInSIPState sLoggedInSIPState;
    private static CallSIPState sCallSIPState;
    private StateManager mStateManager;


    private OnStateCallback mOnSIPCallStateCallback;
    private OnStateCallback mOnSIPConnectionStateCallback;

    public void onSipStateCallChanged(OnStateCallback eventListener) {
        mStateManager.setCallSIPState(sCallSIPState);
        mOnSIPCallStateCallback = eventListener;
        mOnSIPCallStateCallback.onStateChanged();
    }


    public void onSipStateConnectionChanged(OnStateCallback eventListener) {
        logger("СМЕНА СОСТОЯНИЯ: "+sConnectionSIPState);
        if (eventListener != null) {
            mStateManager.setConnectionSIPState(sConnectionSIPState);
            mOnSIPConnectionStateCallback = eventListener;
            mOnSIPConnectionStateCallback.onStateChanged();
        }
    }


    public enum ConnectionSIPState {
        CONNECTED, CONNECTION, FAILED
    }

    public enum LoggedInSIPState {
        AUTHENTICATION, DONE, FAILED
    }

    public enum CallSIPState {
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
            mStateManager = StateManager.getStateManager();
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
                    //                sLoggedInSIPState = LoggedInSIPState.AUTHENTICATION;
                    //                 onSipStateConnectionChanged(mOnSIPConnectionStateCallback);
                    logger("AUTHENTICATION..." + localProfileUri);
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    sLoggedInSIPState = LoggedInSIPState.DONE;
                    sConnectionSIPState = ConnectionSIPState.CONNECTED;
                    logger("AUTHENTICATION DONE " + expiryTime);
                    onSipStateConnectionChanged(mOnSIPConnectionStateCallback);


                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    sLoggedInSIPState = LoggedInSIPState.FAILED;
                    sConnectionSIPState = ConnectionSIPState.FAILED;
                    logger("AUTHENTICATION FAILED " + errorCode + errorMessage);
                    onSipStateConnectionChanged(mOnSIPConnectionStateCallback);
                }
            };


            Intent i = new Intent();
            i.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i, Intent.FILL_IN_DATA);


            mManager.open(mLocalProfile, pi, null);

            mManager.register(mLocalProfile, 30, listener);




            logger(""+SipManager.isSipWifiOnly(mContext));




        } catch (SipException e) {
            e.printStackTrace();
            logger("CONNECTION ERROR" + e.getMessage());
            sConnectionSIPState = ConnectionSIPState.FAILED;
            onSipStateConnectionChanged(mOnSIPConnectionStateCallback);
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

        sCallSIPState = CallSIPState.CALLING;
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
                sCallSIPState = CallSIPState.ESTABLISHED;
                logger("CALL ESTABLISHED");
                onSipStateCallChanged(mOnSIPCallStateCallback);
            }

            @Override
            public void onCalling(SipAudioCall call) {
                super.onCalling(call);
                sCallSIPState = CallSIPState.CALLING;
                onSipStateCallChanged(mOnSIPCallStateCallback);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                logger("onCallEnded");
                sCallSIPState = CallSIPState.ENDED;
                onSipStateCallChanged(mOnSIPCallStateCallback);
                super.onCallEnded(call);
                endCall();
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                super.onError(call, errorCode, errorMessage);
                call.close();
                sCallSIPState = CallSIPState.ERROR;
                logger("CALL ERROR" + errorCode + errorMessage);
                onSipStateCallChanged(mOnSIPCallStateCallback);

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
            sCallSIPState = CallSIPState.ERROR;
            onSipStateCallChanged(mOnSIPCallStateCallback);
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

    public void refreshConnection() {
        initializeLocalProfile();
    }


    private void logger(String logMessage) {
        Log.d(TAG, logMessage);
    }

}
