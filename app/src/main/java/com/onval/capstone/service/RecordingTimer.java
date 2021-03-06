package com.onval.capstone.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.onval.capstone.activities.RecordActivity.UPDATE_TIMER_ACTION;

public class RecordingTimer {
    public static final String CURRENT_TIME_EXTRA = "current-time";

    private Handler handler;
    private Context context;

    private long currentTimeMillis, startTime, timeElapsed;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentTimeMillis = SystemClock.uptimeMillis() - startTime + timeElapsed;
            sendUpdateTimerBroadcast();
            handler.postDelayed(this, 1000);
        }
    };

    RecordingTimer(Context context) {
        this.context = context;
        handler = new Handler();
    }

    private void sendUpdateTimerBroadcast() {
        Intent intent = new Intent(UPDATE_TIMER_ACTION);
        intent.putExtra(CURRENT_TIME_EXTRA, currentTimeMillis);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    void updateTimer() {
        sendUpdateTimerBroadcast();
    }

    void startTimer() {
        startTime = SystemClock.uptimeMillis();
        handler.post(timerRunnable);
    }

    void removeCallback() {
        handler.removeCallbacks(timerRunnable);
    }

    void pauseTimer() {
        removeCallback();
        timeElapsed = currentTimeMillis;
    }

    long getStartTime() {
        return startTime;
    }

    long getTimeElapsed() {
        return timeElapsed;
    }
}
