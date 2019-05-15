package com.maxfin.phoenixapp.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.managers.SipServerManager;

public class JournalFragment extends Fragment {
  //  private static final int PERMISSION_REQUEST_USE_SIP = 50;
    FloatingActionButton mFloatingActionButton;
    SipServerManager sipServerManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);
//        sipServerManager = SipServerManager.getSipServerManager(Objects.requireNonNull(getContext()).getApplicationContext());
        mFloatingActionButton = view.findViewById(R.id.call_button);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), OutgoingCallActivity.class);
                startActivity(intent);

            }
        });


        return view;
    }


}