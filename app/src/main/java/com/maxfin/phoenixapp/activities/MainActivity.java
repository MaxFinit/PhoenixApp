package com.maxfin.phoenixapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.maxfin.phoenixapp.IncomingCallReceiver;
import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.XMPPConnectionService;
import com.maxfin.phoenixapp.managers.MessageManager;
import com.maxfin.phoenixapp.managers.SipServerManager;
import com.maxfin.phoenixapp.models.Contact;

import java.util.Objects;

import static com.maxfin.phoenixapp.Utils.PERMISSION_READ_CONTACT;
import static com.maxfin.phoenixapp.Utils.PERMISSION_RECORD_CALL;
import static com.maxfin.phoenixapp.Utils.PERMISSION_REQUEST_CODE;
import static com.maxfin.phoenixapp.Utils.PERMISSION_SIP;


public class MainActivity extends AppCompatActivity {
    private boolean mPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar mainToolbar = findViewById(R.id.main_tool_bar_menu);
        setSupportActionBar(mainToolbar);

        checkPermissions();
        receiverRegistration();
        startService();

        Button recordCallButton = findViewById(R.id.recorded_call_button);
        recordCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRecordCallActivity = new Intent(MainActivity.this, RecordCallsActivity.class);
                startActivity(toRecordCallActivity);
            }
        });


        Button blackListButton = findViewById(R.id.black_list_button);
        blackListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toBlackListActivity = new Intent(MainActivity.this, BlackListActivity.class);
                startActivity(toBlackListActivity);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (mPermissionGranted)
                    switch (menuItem.getItemId()) {
                        case R.id.menu_call:
                            startCallIntent();
                            break;
                        case R.id.menu_message:
                            startMessageIntent();
                            break;
                    }
                else
                    Toast.makeText(getApplicationContext(), "Вы не приняли разрешения", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        //*Временный контакт пока нет back-end
        MessageManager messageManager = MessageManager.get();
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


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        try {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Для корректной работы приложения рекомендуется принять разрешение", Toast.LENGTH_SHORT).show();
                    mPermissionGranted = false;
                } else {
                    mPermissionGranted = true;
                    SipServerManager.getSipServerManager(getApplicationContext());
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }


    private void startService() {
        Intent startXMPPConnectService = new Intent(this, XMPPConnectionService.class);
        startService(startXMPPConnectService);
    }


    private void receiverRegistration() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.Sip.INCOMING_CALL");
        IncomingCallReceiver callReceiver = new IncomingCallReceiver();
        registerReceiver(callReceiver, filter);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (Objects.requireNonNull(getApplicationContext()).
                checkSelfPermission(PERMISSION_SIP) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(PERMISSION_READ_CONTACT) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(PERMISSION_RECORD_CALL) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.USE_SIP, Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        } else {
            mPermissionGranted = true;
            SipServerManager.getSipServerManager(getApplicationContext());
        }
    }

    private void startCallIntent() {
        Intent callIntent = new Intent(MainActivity.this, CallActivity.class);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(callIntent);
    }

    private void startMessageIntent() {
        Intent messageIntent = new Intent(MainActivity.this, DialogListActivity.class);
        messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        messageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(messageIntent);
    }


}
