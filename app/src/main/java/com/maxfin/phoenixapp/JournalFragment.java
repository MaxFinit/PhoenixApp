package com.maxfin.phoenixapp;

import android.content.IntentFilter;
import android.net.sip.SipException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;

public class JournalFragment extends Fragment {
    FloatingActionButton mFloatingActionButton;
    IncomingCallReceiver callReceiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
        mFloatingActionButton = view.findViewById(R.id.call_button);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.Sip.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        getActivity().registerReceiver(callReceiver, filter);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SipConnectionManager sipConnectionManager = new SipConnectionManager();
                try {
                    sipConnectionManager.InitSipManager(getActivity());
                    sipConnectionManager.setSipProfile("","","");
                    sipConnectionManager.initCall("");





                } catch (SipException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        return view;
    }


}
