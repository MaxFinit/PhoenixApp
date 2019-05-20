package com.maxfin.phoenixapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipProfile;

import com.maxfin.phoenixapp.UI.MainActivity;
import com.maxfin.phoenixapp.managers.SipServerManager;


public class IncomingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SipAudioCall sipAudioCall = null;

        SipAudioCall.Listener listener = new SipAudioCall.Listener() {

            @Override
            public void onRinging(SipAudioCall call, SipProfile caller) {
                try {
                    call.answerCall(30);
                } catch (SipException e) {
                    e.printStackTrace();
                }
            }
        };

        SipServerManager sipConnectionManager = SipServerManager.getSipServerManager(context);
        try {
            sipAudioCall = sipConnectionManager.getManager().takeAudioCall(intent, listener);
            sipAudioCall.answerCall(30);
            sipAudioCall.startAudio();
            sipAudioCall.setSpeakerMode(true);

        } catch (SipException e) {
            if (sipAudioCall != null)
                sipAudioCall.close();
            e.printStackTrace();
        }


    }
}
