package com.maxfin.phoenixapp.managers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.util.Log;


import com.maxfin.phoenixapp.OnStateCallback;

import java.text.ParseException;

public class SipServerManager {
    private static final String TAG = "SipServerManager";

    private SipManager mManager;
    private SipProfile mLocalProfile;
    private SipAudioCall mCall;
    private Context mContext;
    private static ConnectionSIPState sConnectionSIPState;
    private static LoggedInSIPState sLoggedInSIPState;
    private static CallSIPState sCallSIPState;
    private StateManager mStateManager;

    public enum ConnectionSIPState {
        CONNECTED, CONNECTION, FAILED
    }

    public enum LoggedInSIPState {
        AUTHENTICATION, DONE, FAILED
    }

    public enum CallSIPState {
        BUSE, ENDED, ERROR, ESTABLISHED, CALLING, HELD, RINGING, RINGINGBACK, CHANGED
    }

    private OnStateCallback mOnSIPCallStateCallback;
    private OnStateCallback mOnSIPConnectionStateCallback;

    private static SipServerManager sSipServerManager;

    private SipServerManager(Context context) {
        mContext = context.getApplicationContext();
        initializeManager(context);
    }

    public static SipServerManager getSipServerManager(Context context) {
        if (sSipServerManager == null) {
            sSipServerManager = new SipServerManager(context);
        }
        return sSipServerManager;
    }

    private void initializeManager(Context context) {
        if (mManager == null) {
            mManager = SipManager.newInstance(context);
            mStateManager = StateManager.getStateManager();
        }
        initializeLocalProfile();
    }

    private void initializeLocalProfile() {

        if (mLocalProfile != null) {
            // closeLocalProfile();
        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder("76920", "172.16.13.223");
            builder.setPassword("238219325823bd838c23d9db90ee32cd");
            builder.setAutoRegistration(false);
            mLocalProfile = builder.build();

            SipRegistrationListener listener = new SipRegistrationListener() {
                @Override
                public void onRegistering(String localProfileUri) {
                    sLoggedInSIPState = LoggedInSIPState.AUTHENTICATION;
                    onSipStateConnectionChanged(mOnSIPConnectionStateCallback);
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


            Intent incomingCallIntent = new Intent();
            incomingCallIntent.setAction("android.SipDemo.INCOMING_CALL");
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, incomingCallIntent, Intent.FILL_IN_DATA);


            mManager.open(mLocalProfile, pi, null);
            mManager.register(mLocalProfile, 30, listener);


            logger("" + SipManager.isSipWifiOnly(mContext));


        } catch (SipException e) {
            e.printStackTrace();
            logger("CONNECTION ERROR" + e.getMessage());
            sConnectionSIPState = ConnectionSIPState.FAILED;
            onSipStateConnectionChanged(mOnSIPConnectionStateCallback);
            try {
                mManager.unregister(mLocalProfile, null);
            } catch (SipException e1) {
                e1.printStackTrace();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger("CONNECTION ERROR" + e.getMessage());
        }


    }


    public void closeLocalProfile() {
        try {
            mManager.close(mLocalProfile.getUriString());
            sSipServerManager = null;
        } catch (SipException e) {
            e.printStackTrace();
            logger("FAILED TO CLOSE LOCAL PROFILE" + e.getMessage());
        }
    }

    public void onSipStateCallChanged(OnStateCallback eventListener) {
        if (eventListener != null) {
            mStateManager.setCallSIPState(sCallSIPState);
            mOnSIPCallStateCallback = eventListener;
            mOnSIPCallStateCallback.onStateChanged();
        }

    }

    public void onSipStateConnectionChanged(OnStateCallback eventListener) {
        logger("СМЕНА СОСТОЯНИЯ: " + sConnectionSIPState);
        if (eventListener != null) {
            mStateManager.setConnectionSIPState(sConnectionSIPState);
            mOnSIPConnectionStateCallback = eventListener;
            mOnSIPConnectionStateCallback.onStateChanged();
        }
    }

    public void initiateCall(String number) {

//        if (mCall != null && mCall.isInCall()) {
//            Toast.makeText(mContext, "You're currently busy...", Toast.LENGTH_SHORT);
//            return;
//        }

        sCallSIPState = CallSIPState.CALLING;
        logger("INIT CALL");
        SipAudioCall.Listener listener = new SipAudioCall.Listener() {

            @Override
            public void onCallEstablished(SipAudioCall call) {
                call.startAudio();
                call.setSpeakerMode(true);
                call.toggleMute();
                sCallSIPState = CallSIPState.ESTABLISHED;
                logger("CALL ESTABLISHED" + call.getState());
                onSipStateCallChanged(mOnSIPCallStateCallback);
            }

            @Override
            public void onCalling(SipAudioCall call) {
                logger("ON CALLING" + call.getState());
                sCallSIPState = CallSIPState.CALLING;
                onSipStateCallChanged(mOnSIPCallStateCallback);
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                logger("ON CAL ENDED" + call.getState());
                sCallSIPState = CallSIPState.ENDED;
                onSipStateCallChanged(mOnSIPCallStateCallback);
                endCall();
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                super.onError(call, errorCode, errorMessage);
                call.close();
                sCallSIPState = CallSIPState.ERROR;
                logger("CALL ERROR" + errorCode + errorMessage + call.getState());
                endCall();
                onSipStateCallChanged(mOnSIPCallStateCallback);
            }

            @Override
            public void onReadyToCall(SipAudioCall call) {
                logger("onReadyToCall" + call.getState());
            }

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                logger("onRinging" + call.getState());
            }

            @Override
            public void onRingingBack(SipAudioCall call) {
                logger("onRingingBack" + call.getState());
            }

            @Override
            public void onCallBusy(SipAudioCall call) {
                logger("onCallBusy" + call.getState());
            }

            @Override
            public void onCallHeld(SipAudioCall call) {
                logger("onCallHeld" + call.getState());
            }

            @Override
            public void onChanged(SipAudioCall call) {
                logger("onChanged" + call.getState());
            }
        };

        try {
            SipProfile.Builder builder = new SipProfile.Builder(number, "172.16.13.223");
            SipProfile profile = builder.build();

            mCall = mManager.makeAudioCall(mLocalProfile,
                    profile, listener, 30);

            logger(mCall.toString());




        } catch (SipException e) {
            logger("ERROR WHEN TRYING CALL");
            sCallSIPState = CallSIPState.ERROR;
            onSipStateCallChanged(mOnSIPCallStateCallback);
            e.printStackTrace();
            if (mLocalProfile != null)
                //closeLocalProfile();
                if (mCall != null) {
                    mCall.close();
                }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void endCall() {

        if (mCall != null) {
            logger("ENDING CALL");
            logger(mCall.toString());

            try {
                mCall.endCall();
                logger("CALL ENDED");
            } catch (SipException e) {
                e.printStackTrace();
            }
            mCall.close();

        }
    }


    public SipManager getManager() {
        return mManager;
    }

    public void refreshConnection() {
        initializeLocalProfile();
    }

    private void logger(String logMessage) {
        Log.d(TAG, logMessage);
    }

}
