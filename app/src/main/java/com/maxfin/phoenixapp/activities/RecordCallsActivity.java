package com.maxfin.phoenixapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.maxfin.phoenixapp.R;
import com.maxfin.phoenixapp.managers.RecordManager;

public class RecordCallsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_calls);

        final RecordManager recordManager = new RecordManager();

        Button record = findViewById(R.id.buttonRecord);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordManager.recordStart();
            }
        });


        Button stopRecord = findViewById(R.id.buttonStopRecord);
        stopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordManager.recordStop();
            }
        });


        Button play = findViewById(R.id.buttonPlay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordManager.playStart();
            }
        });


        Button stopPlay = findViewById(R.id.buttonStopPlay);
        stopPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordManager.playStop();
            }
        });


    }


}
