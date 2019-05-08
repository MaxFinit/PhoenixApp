package com.maxfin.phoenixapp;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.models.Contact;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_USE_SIP = 50;
    IncomingCallReceiver callReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.Sip.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        registerReceiver(callReceiver, filter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Objects.requireNonNull(getApplicationContext()).
                checkSelfPermission(Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.USE_SIP}, PERMISSION_REQUEST_USE_SIP);
        }


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
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(callIntent);
                        break;
                    case R.id.menu_message:
                        Intent messageIntent = new Intent(MainActivity.this, DialogListActivity.class);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(messageIntent);
                        break;
                }
                return false;
            }
        });


        //*Временный контакт пока нет back-end
        MessageManager messageManager = MessageManager.get(getApplicationContext());
        if (messageManager.getContactList().size() == 0) {
            Contact contact = new Contact();
            contact.setJId("maxfin2@jabber.ru");
            contact.setName("Max");
            contact.setNumber("+8945554");
            Uri path = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_contact_circle_api2);
            contact.setPhoto(path.toString());
            messageManager.uploadMessageList(contact);
            Contact contact1 = new Contact();
            contact1.setJId("maxfin3@jabber.ru");
            contact1.setName("Max");
            contact1.setNumber("+89");
            Uri path1 = Uri.parse("android.resource://com.maxfin.phoenixapp/" + R.drawable.ic_contact_circle_api2);
            contact1.setPhoto(path1.toString());
            messageManager.uploadMessageList(contact1);
        }
        //*


        Intent i1 = new Intent(this, XMPPConnectionService.class);
        startService(i1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_USE_SIP) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Пока вы не приймите запрос вы не сможете звонить", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
