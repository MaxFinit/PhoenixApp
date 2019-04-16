package com.maxfin.phoenixapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MessageListActivity extends AppCompatActivity {

    private RecyclerView mMessageRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        mMessageRecyclerView = findViewById(R.id.message_recycler_view);
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO Дописать ресайклер диалогов

        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.menu_home:
                        Intent homeIntent = new Intent(MessageListActivity.this,MainActivity.class);
                        startActivity(homeIntent);
                        break;
                    case R.id.menu_call:
                        Intent callIntent = new Intent(MessageListActivity.this,CallActivity.class);
                        startActivity(callIntent);
                        break;
                }
                return false;
            }
        });

        FloatingActionButton mFloatingActionButton = findViewById(R.id.add_dialog_fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageListActivity.this,AddingDialogActivity.class);
                startActivity(intent);
            }
        });

       // Intent i1 = new Intent(this,XMPPConnectionService.class);
       // startService(i1);

    }





}
