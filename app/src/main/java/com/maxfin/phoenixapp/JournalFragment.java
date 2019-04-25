package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.sip.SipException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParseException;
import java.util.Objects;

public class JournalFragment extends Fragment {
    private static final int PERMISSION_REQUEST_USE_SIP = 50;
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Objects.requireNonNull(getContext()).
                        checkSelfPermission(Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.USE_SIP}, PERMISSION_REQUEST_USE_SIP);
                } else {
                    SipConnectionManager sipConnectionManager = new SipConnectionManager();
                    try {
                        sipConnectionManager.InitSipManager(getActivity());
                        sipConnectionManager.setSipProfile("76920", "172.16.13.223", "238219325823bd838c23d9db90ee32cd");
                        sipConnectionManager.initCall("+380713222303@172.16.13.223");


                    } catch (SipException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


//                    Intent intent = new Intent(getActivity(), InputCallActivity.class);
//                    startActivity(intent);


                }
            }
        });


        return view;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_USE_SIP) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
              //  updateUi();
            } else {
                Toast.makeText(getActivity(), "Пока вы не приймите запрос мы не можем показать вам список контактов", Toast.LENGTH_SHORT).show();
            }
        }
    }




}
