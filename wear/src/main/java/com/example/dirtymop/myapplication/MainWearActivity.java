package com.example.dirtymop.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainWearActivity extends WearableActivity {

    private Button mapButton, historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wear);

        // Ambient mode
        setAmbientEnabled();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                historyButton = (Button) stub.findViewById(R.id.historyButton);
                mapButton = (Button) stub.findViewById(R.id.mapButton);
                mapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Go to map activity
                        Intent intent = new Intent(MainWearActivity.this, HudWearActivity.class);
                        startActivity(intent);
                    }
                });
                historyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainWearActivity.this, HistoryWearActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
    }
}
