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
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.onval.capstone.dialog_fragment.DeleteRecordingDialogFragment;
import com.onval.capstone.R;
import com.onval.capstone.dialog_fragment.ChooseCategoryDialogFragment;
import com.onval.capstone.dialog_fragment.SaveRecordingDialogFragment;
import com.onval.capstone.service.RecordingBinder;
import com.onval.capstone.service.RecordingService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.onval.capstone.activities.RecordingsActivity.CATEGORY_ID;
import static com.onval.capstone.service.RecordingService.DEFAULT_REC_NAME;
import static com.onval.capstone.service.RecordingTimer.CURRENT_TIME_EXTRA;

public class RecordActivity extends AppCompatActivity
        implements SaveRecordingDialogFragment.OnSaveCallback,
                DeleteRecordingDialogFragment.OnDeleteCallback {

    @BindView(R.id.timer_tv) TextView timerTextView;
    @BindView(R.id.record_fab) FloatingActionButton fab;


    public static final String UPDATE_TIMER_ACTION = "com.onval.capstone.UPDATE_TIMER";

    public static final String PAUSE_ACTION = "com.onval.capstone.PAUSE";
    public static final String PLAY_ACTION = "com.onval.capstone.PLAY";
    public static final String RESET_ACTION = "com.onval.capstone.RESET";

    private static final String CURRENT_TIME_KEY = "current-time";
    private static final String CC_FRAGMENT_TAG = "choose-category";

    @Nullable Integer categoryId;

    private Intent intentService;
    private RecordingService service;
    private final ServiceConnection serviceConnection = new MyServiceConnection();
    private boolean isBound = false;

    private Bundle recInfoBundle;

    private BroadcastReceiver timerReceiver = new TimerBroadcastReceiver();
    private BroadcastReceiver uiReceiver = new UIBroadcastReceiver();

    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private final String[] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        registerTimerReceiver();
        registerUIReceiver();

        Bundle extras = getIntent().getExtras();
        categoryId = (extras != null) ? extras.getInt(CATEGORY_ID) : null;
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
        intentService = new Intent(this, RecordingService.class);
        startService(intentService);
        bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void registerUIReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_ACTION);
        filter.addAction(PLAY_ACTION);
        filter.addAction(RESET_ACTION);

        LocalBroadcastManager.getInstance(this).registerReceiver(uiReceiver, filter);
    }

    private void registerTimerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_TIMER_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(timerReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timerReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uiReceiver);

        if(isBound)
            unbindService(serviceConnection);
    }

    @OnClick(R.id.record_fab)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void recordButton(View view) {
        if (service == null) {
            kickStartService();
        } else {
            if (service.isPlaying() && isBound)
                service.pauseRecording();
            else
                service.resumeRecording();
        }
    }

    @OnClick(R.id.stop_recording)
    public void stopRecording(View view) {
        if (isBound) {
            service.pauseRecording();
            prepareRecordingInfoBundle();

            if (categoryId == null) {
                ChooseCategoryDialogFragment chooseCategory = new ChooseCategoryDialogFragment();
                chooseCategory.setArguments(recInfoBundle);
                chooseCategory.show(getSupportFragmentManager(), CC_FRAGMENT_TAG);
            } else {
                recInfoBundle.putInt(CATEGORY_ID, categoryId);

                SaveRecordingDialogFragment saveRecording = new SaveRecordingDialogFragment();
                saveRecording.setArguments(recInfoBundle);
                saveRecording.show(getSupportFragmentManager(), "derpo");
            }
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
    public void onSaveRecording(long id, String name) {
        service.stopRecording();
        String physicalName = id + "_" + name; //ex. 4_WednesdayMemo
        assignNameToRecording(physicalName);

        String msg = "The recording " + name + " has been created.";
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        resetService();
    }

    @Override
    public void onDeleteRecording() {
        resetService();
        Toast.makeText(this, "The recording has been deleted.", Toast.LENGTH_SHORT).show();
    }

    private void resetService() {
        unbindService(serviceConnection);
        stopService(intentService);
        isBound = false;
        service = null; //I can't rely on onServiceDisconnected callback
        fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fab_dot));
        timerTextView.setText(getString(R.string.starting_timer));
    }

    private void assignNameToRecording(String newRecName) {
        String externalPath = getExternalCacheDir().getAbsolutePath();
        File rec = new File(externalPath + DEFAULT_REC_NAME);
        File newName = new File(externalPath + "/" + newRecName + ".mp4");
        rec.renameTo(newName);
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
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBound = false;
            service = null;
        }
    }

    private class TimerBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            long currentTimeMillis = intent.getExtras().getLong(CURRENT_TIME_EXTRA);
            String currentTime = timeFormatFromMills(currentTimeMillis);
            timerTextView.setText(currentTime);
        }
    }

    private class UIBroadcastReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            int drawableId = -1;

            switch (action) {
                case PLAY_ACTION:
                    drawableId = R.drawable.ic_play_white_24dp;
                    break;
                case PAUSE_ACTION:
                    drawableId = R.drawable.ic_pause_white_24dp;
                    break;
                case RESET_ACTION:
                    drawableId = R.drawable.ic_fab_dot;
                    break;
            }

            if (drawableId != -1)
                fab.setImageDrawable(ContextCompat.getDrawable(context, drawableId));
        }
    }
}
