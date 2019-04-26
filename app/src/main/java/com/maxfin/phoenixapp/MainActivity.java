package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_USE_SIP = 50;
    SipConnectionManager mSipConnectionManager;
    IncomingCallReceiver callReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Objects.requireNonNull(getApplicationContext()).
                checkSelfPermission(Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.USE_SIP}, PERMISSION_REQUEST_USE_SIP);
        }else {
            mSipConnectionManager = SipConnectionManager.getSipConnectionManager(getApplicationContext());
            mSipConnectionManager.setSipProfile("76920", "172.16.13.223", "238219325823bd838c23d9db90ee32cd");
        }


        IntentFilter filter = new IntentFilter();
        filter.addAction("android.Sip.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        registerReceiver(callReceiver, filter);



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_call:
                        Intent callIntent = new Intent(MainActivity.this, CallActivity.class);
                        startActivity(callIntent);
                        break;
                    case R.id.menu_message:
                        Intent messageIntent = new Intent(MainActivity.this, DialogListActivity.class);
                        startActivity(messageIntent);
                        break;
                }
                return false;
            }
        });

        Intent i1 = new Intent(this,XMPPConnectionService.class);
        startService(i1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_USE_SIP) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mSipConnectionManager = SipConnectionManager.getSipConnectionManager(getApplicationContext());
                mSipConnectionManager.setSipProfile("76920", "172.16.13.223", "238219325823bd838c23d9db90ee32cd");
            } else {
                Toast.makeText(this, "Пока вы не приймите запрос мы не можем показать вам список контактов", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
