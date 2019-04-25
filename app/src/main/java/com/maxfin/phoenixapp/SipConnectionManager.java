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

import java.text.ParseException;

public class SipConnectionManager {
    private static final String TAG = "SipConnectionManager";
    private SipManager mSipManager;
    private SipProfile mSipProfile;
    private SipAudioCall mCall;
    private Context mContext;
    private ConnectionState mConnectionState;
    private LoggedInState mLoggedInState;


    public enum ConnectionState {
        CONNECTED, AUTHENTICATED, CONNECTING, DISCONNECTING, DISCONNECTED
    }

    public enum LoggedInState {
        LOGGED_IN, LOGGED_OUT
    }


    public SipManager getSipManager(){
        return mSipManager;
    }

    public SipAudioCall getCall() {
        return mCall;
    }

    public void setCall(SipAudioCall call) {
        mCall = call;
    }

    public void InitSipManager(Context context) throws SipException, ParseException {
        mContext = context;
        if (mSipManager == null) {
            mSipManager = SipManager.newInstance(context);
        }
    }


    public void setSipProfile(String username, String domain, String password) throws ParseException, SipException {
        SipProfile.Builder builder = new SipProfile.Builder(username, domain);
        builder.setPassword(password);
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


    public void initCall(String adress) throws SipException {

        SipAudioCall.Listener listener = new SipAudioCall.Listener() {

            @Override
            public void onCallEstablished(SipAudioCall call) {
                call.startAudio();
                call.setSpeakerMode(true);
                call.toggleMute();
            }

            @Override
            public void onCallEnded(SipAudioCall call) {
                super.onCallEnded(call);
            }
        };

        mCall = mSipManager.makeAudioCall(mSipProfile.getUriString(), adress, listener, 30);

    }


}
