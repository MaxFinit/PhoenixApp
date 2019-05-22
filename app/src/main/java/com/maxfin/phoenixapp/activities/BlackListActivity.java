package com.maxfin.phoenixapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.maxfin.phoenixapp.R;

public class BlackListActivity extends AppCompatActivity {
    private RecyclerView mBlackListRecyclerView;
   // private BlackListAdapter mBlackListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        Toolbar toolbar = findViewById(R.id.black_list_tool_bar);
        setSupportActionBar(toolbar);

        mBlackListRecyclerView = findViewById(R.id.black_list_recycler_view);
    }


}
