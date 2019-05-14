package com.maxfin.phoenixapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.maxfin.phoenixapp.managers.StateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallActivity extends AppCompatActivity {
    private static final String TAG = "CallActivity";
    private View mStateSipView;
    private Button mRefreshConnectionButton;
    private SipServerManager mSipConnectionManager;
    private StateManager mStateManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        mStateManager = StateManager.getStateManager();
        mStateSipView = findViewById(R.id.sip_call_state_view);
        mRefreshConnectionButton = findViewById(R.id.refresh_connection_button);
        mSipConnectionManager = SipServerManager.getSipServerManager(Objects.requireNonNull(getApplicationContext()));


        ViewPager viewPager = findViewById(R.id.container_vp);
        setUpViewPager(viewPager);


        TabLayout tabLayout = findViewById(R.id.call_tabs);
        tabLayout.setupWithViewPager(viewPager);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText(R.string.call_text);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText(R.string.contact_text);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_home:
                        Intent homeIntent = new Intent(CallActivity.this, MainActivity.class);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        break;
                    case R.id.menu_message:
                        Intent messageIntent = new Intent(CallActivity.this, DialogListActivity.class);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(messageIntent);
                        break;
                }
                return false;
            }
        });


        mRefreshConnectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSipConnectionManager.refreshConnection();
            }
        });


        mSipConnectionManager.onSipStateConnectionChanged(new OnStateCallback() {
            @Override
            public void onStateChanged() {
                updateState();
            }
        });


    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (checkForInternet()) {
//            mSipConnectionManager.refreshConnection();
//        } else {
//            mStateManager.setConnectionSIPState(SipServerManager.ConnectionSIPState.FAILED);
//            updateState();
//        }
//    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (checkForInternet() && mStateManager.getConnectionSIPState() == SipServerManager.ConnectionSIPState.FAILED) {
            mSipConnectionManager.refreshConnection();
        } else if (!checkForInternet()) {
            mStateManager.setConnectionSIPState(SipServerManager.ConnectionSIPState.FAILED);
            updateState();
        }


        Log.d(TAG, "focus changed");
    }

    private void updateState() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (mStateManager.getConnectionSIPState()) {

                    case CONNECTED:
                        mStateSipView.setVisibility(View.GONE);
                        break;
                    case FAILED:
                        mStateSipView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


    }


    public boolean checkForInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new JournalFragment());
        viewPagerAdapter.addFragment(new ContactFragment());
        viewPager.setAdapter(viewPagerAdapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();


        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }


}
