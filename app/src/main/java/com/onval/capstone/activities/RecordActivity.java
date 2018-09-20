package com.onval.capstone.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.onval.capstone.R;
import com.onval.capstone.service.RecordBinder;
import com.onval.capstone.service.RecordService;

public class RecordActivity extends AppCompatActivity {
    private boolean isBound = false;
    private RecordService service;
    Intent intentService;

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentService = new Intent(this, RecordService.class);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_record);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                startService(intentService);
                bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
                break;
        }
        if (!permissionToRecordAccepted) finish();

    }

    public void recordButton(View view) {
        if (isBound && service.isPlaying())
            service.pauseRecording();
        else
            service.startRecording();

        upgradeRecordDrawable(view);
    }

    private void upgradeRecordDrawable(View view) {
        if (view instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) view;

            int drawableId = (service.isPlaying()) ? R.drawable.ic_pause_white_24dp : R.drawable.ic_fab_dot;
            fab.setImageDrawable(ContextCompat.getDrawable(this, drawableId));
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            RecordBinder recordBinder = (RecordBinder) binder;
            service = (RecordService) recordBinder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
}
