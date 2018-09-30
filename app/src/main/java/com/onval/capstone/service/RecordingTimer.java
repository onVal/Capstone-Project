package com.onval.capstone.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import static com.onval.capstone.activities.RecordActivity.UPDATE_TIMER_ACTION;

class RecordingTimer {
    private Handler handler;
    private Context context;

    private long currentTimeMillis, startTime, timeElapsed;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentTimeMillis = SystemClock.uptimeMillis() - startTime + timeElapsed;
            Intent intent = new Intent(UPDATE_TIMER_ACTION);
            intent.putExtra("current-time", currentTimeMillis);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            handler.postDelayed(this, 1000);
        }
    };

    RecordingTimer(Context context) {
        this.context = context;
        handler = new Handler();
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
