package com.onval.capstone.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.icu.lang.UCharacter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.onval.capstone.R;
import com.onval.capstone.fragment.ChooseCategoryDialogFragment;
import com.onval.capstone.fragment.SaveRecordingDialogFragment;
import com.onval.capstone.service.RecordingBinder;
import com.onval.capstone.service.RecordingService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.service.RecordingService.DEFAULT_REC_NAME;

public class RecordActivity extends AppCompatActivity
        implements SaveRecordingDialogFragment.OnSaveCallback {

    @BindView(R.id.timer_tv) TextView timerTextView;
    @BindView(R.id.record_fab) FloatingActionButton fab;

    public static final String UPDATE_TIMER_ACTION = "com.onval.capstone.UPDATE_TIMER";
    private static final String CURRENT_TIME_KEY = "current-time";
    private static final String CC_FRAGMENT_TAG = "choose-category";

    private RecordingService service;
    private final ServiceConnection serviceConnection = new MyServiceConnection();
    private boolean isBound = false;

    private Bundle recInfoBundle;

    private BroadcastReceiver receiver = new TimerBroadcastReceiver();

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final String[] permissions   = {Manifest.permission.RECORD_AUDIO};

    private static final String SAVE_RECORDING_TAG = "SAVE_RECORDING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        IntentFilter filter = new IntentFilter(UPDATE_TIMER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);

        if (savedInstanceState != null) {
            CharSequence cs = savedInstanceState.getCharSequence(CURRENT_TIME_KEY);
            timerTextView.setText(cs);
        } else {
            timerTextView.setText(getString(R.string.starting_timer));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION)
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if (permissionToRecordAccepted) {
            kickStartService();
        }
        else {
            finish();
        }
    }

    private void kickStartService() {
        Intent intentService = new Intent(this, RecordingService.class);
        startService(intentService);
        bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence(CURRENT_TIME_KEY, timerTextView.getText());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        if(isBound)
            unbindService(serviceConnection);
    }

    @OnClick(R.id.record_fab)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordButton(View view) {
        if (service.isPlaying() && isBound)
            service.pauseRecording();
        else
            service.resumeRecording();

        upgradeRecordDrawable(view);
    }

    @OnClick(R.id.stop_recording)
    public void stopRecording(View view) {
        if (isBound) {
            service.pauseRecording();
            upgradeRecordDrawable(fab);

            prepareRecordingInfoBundle();

            ChooseCategoryDialogFragment chooseCategory = new ChooseCategoryDialogFragment();
            chooseCategory.setArguments(recInfoBundle);
            chooseCategory.show(getSupportFragmentManager(), CC_FRAGMENT_TAG);
        }
    }

    private void upgradeRecordDrawable(View view) {
        if (view instanceof FloatingActionButton) {
            FloatingActionButton fab = (FloatingActionButton) view;

            int drawableId = (service.isPlaying()) ? R.drawable.ic_pause_white_24dp : R.drawable.ic_fab_dot;
            fab.setImageDrawable(ContextCompat.getDrawable(this, drawableId));
        }
    }

    private void prepareRecordingInfoBundle() {
        Date currentDate = service.getStartDate();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String formattedDate = dateFormat.format(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
        String formattedTime = timeFormat.format(currentDate);

        recInfoBundle = new Bundle();
        recInfoBundle.putString("REC_START_TIME", formattedTime);
        recInfoBundle.putString("REC_DATE", formattedDate);
        recInfoBundle.putString("REC_DURATION", timeFormatFromMills(service.getTimeElapsed()));
    }

    @Override
    public void onSaveRecording(String name) {
        service.stopRecording();
        assignNameToRecording(name);

        String msg = "The recording " + name + " has been created.";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void assignNameToRecording(String newRecName) {
        String externalPath = getExternalCacheDir().getAbsolutePath();
        File rec = new File(externalPath + DEFAULT_REC_NAME);
        File newName = new File(externalPath + "/" + newRecName + ".mp4");
        boolean success = rec.renameTo(newName);
    }

    @SuppressLint("DefaultLocale")
    private String timeFormatFromMills(long millis) {
        int seconds = (int) (millis / 1000);

        int hh = seconds / 3600;
        int mm = seconds / 60 % 60;
        int ss = seconds % 60;

        if (hh == 0)
            return String.format("%02d:%02d", mm, ss);

        return String.format("%02d:%02d:%02d", hh, mm, ss);
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            RecordingBinder recordingBinder = (RecordingBinder) binder;
            service = (RecordingService) recordingBinder.getService();
            isBound = true;

            if (!service.isPlaying()) {
                upgradeRecordDrawable(fab);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBound = false;
            service = null;
        }
    }

    public class TimerBroadcastReceiver extends BroadcastReceiver {
        public TimerBroadcastReceiver() { }

        public void onReceive(Context context, Intent intent) {
            long currentTimeMillis = intent.getExtras().getLong("current-time");
            String currentTime = timeFormatFromMills(currentTimeMillis);
            timerTextView.setText(currentTime);
        }
    }
}
