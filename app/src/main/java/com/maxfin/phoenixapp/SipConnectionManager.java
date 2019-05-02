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

import java.text.ParseException;

public class SipConnectionManager {
    private static final String TAG = "SipConnectionManager";
    private static SipConnectionManager sSipConnectionManager;
    private SipManager mSipManager;
    private SipProfile mSipProfile;
    private SipAudioCall mCall;
    private Context mContext;
    private ConnectionState mConnectionState;
    private LoggedInState mLoggedInState;
    private onCustomEventListener mListener;

    public enum CallingState {
        BUSE, ENDED, ERROR, ESTABLISHED, CALLING, HELD, RINGING, RINGINGBACK, CHANGED
    }


    public enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    public enum LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }

    public static SipConnectionManager getSipConnectionManager(Context context) {
        if (sSipConnectionManager == null) {
            sSipConnectionManager = new SipConnectionManager(context);
        }
        return sSipConnectionManager;
    }

    private SipConnectionManager(Context context) {
        mContext = context;
        InitSipManager(context);
    }

    private void InitSipManager(Context context) {
        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(context);
        }
    }

    public SipManager getSipManager() {
        return mSipManager;
    }

    public SipAudioCall getCall() {
        return mCall;
    }

    public void setCall(SipAudioCall call) {
        mCall = call;
    }


    public void setSipProfile(String username, String domain, String password) {
        try {

            if (mSipProfile != null) {
                closeSipProfile();
            }


            SipProfile.Builder builder = null;
            builder = new SipProfile.Builder(username, domain);
            builder.setPassword(password);
            builder.setAuthUserName("76920");
            mSipProfile = builder.build();


            Intent intent = new Intent();
            intent.setAction("android.Sip.INCOMING_CALL");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, Intent.FILL_IN_DATA);
            mSipManager.open(mSipProfile, pendingIntent, null);


            mSipManager.setRegistrationListener(mSipProfile.getUriString(), new SipRegistrationListener() {
                @Override
                public void onRegistering(String s) {
                    Log.d(TAG, "Registration on SIP server");
                    mConnectionState = ConnectionState.CONNECTING;
                }

                @Override
                public void onRegistrationDone(String s, long l) {
                    Log.d(TAG, "Registration on SIP server done");
                    mConnectionState = ConnectionState.AUTHENTICATED;
                    mLoggedInState = LoggedInState.LOGGED_IN;


                }

                @Override
                public void onRegistrationFailed(String s, int i, String s1) {
                    Log.d(TAG, "Registration on SIP server fail");
                    mConnectionState = ConnectionState.DISCONNECTING;
                    mLoggedInState = LoggedInState.LOGGED_OUT;
                    closeSipProfile();
                }
            });
        } catch (SipException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void closeSipProfile() {
        if (mSipManager == null) {
            return;
        }
        try {
            if (mSipProfile != null) {
                mSipManager.close(mSipProfile.getUriString());
            }
        } catch (Exception ee) {
            Log.d(TAG, "Close profile");
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


    public void initCall(String adress) {

        SipAudioCall.Listener listener = new SipAudioCall.Listener() {

            @Override
            public void onCallEstablished(SipAudioCall call) {
                OutputCallActivity.sCallingState = CallingState.ESTABLISHED;
                Log.d(TAG, "onCallEstablished");
                call.startAudio();
                call.setSpeakerMode(true);
                call.toggleMute();
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                super.onCallEnded(call);
                OutputCallActivity.sCallingState = CallingState.ENDED;
                Log.d(TAG, "onCallEnded");
            }


            @Override
            public void onCallBusy(SipAudioCall call) {
                super.onCallBusy(call);
                call.close();
                OutputCallActivity.sCallingState = CallingState.BUSE;
                Log.d(TAG, "onCallBusy");
            }

            @Override
            public void onCallHeld(SipAudioCall call) {
                super.onCallHeld(call);
                OutputCallActivity.sCallingState = CallingState.HELD;
                Log.d(TAG, "onCallHeld");
            }


            @Override
            public void onReadyToCall(SipAudioCall call) {
                super.onReadyToCall(call);
                Log.d(TAG, "onReadyToCall");
            }

            @Override
            public void onCalling(SipAudioCall call) {
                super.onCalling(call);
                OutputCallActivity.sCallingState = CallingState.CALLING;
                Log.d(TAG, "onCalling");
            }

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                super.onRinging(call, caller);
                OutputCallActivity.sCallingState = CallingState.RINGING;
                Log.d(TAG, "onRinging");
            }

            @Override
            public void onRingingBack(SipAudioCall call) {
                super.onRingingBack(call);
                OutputCallActivity.sCallingState = CallingState.RINGINGBACK;
                Log.d(TAG, "onRingingBack");
            }

            @Override
            public void onError(SipAudioCall call, int errorCode, String errorMessage) {
                super.onError(call, errorCode, errorMessage);
                mListener.onEvent();
                OutputCallActivity.sCallingState = CallingState.ERROR;
                Log.d(TAG, "onError");

            }

            @Override
            public void onChanged(SipAudioCall call) {
                super.onChanged(call);
                OutputCallActivity.sCallingState = CallingState.CHANGED;
                Log.d(TAG, "onChanged");
            }
        };


        try {
            SipProfile sipTarget = (new SipProfile.Builder("+380713222303", "172.16.13.223")).build();
            mCall = mSipManager.makeAudioCall(mSipProfile, sipTarget, listener, 0);
        } catch (SipException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public void setCustomEventListener(onCustomEventListener eventListener) {
        mListener = eventListener;
    }

    public interface onCustomEventListener {
        void onEvent();   //method, which can have parameters
    }


}

