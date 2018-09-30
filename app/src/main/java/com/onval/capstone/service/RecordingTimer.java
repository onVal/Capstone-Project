package com.onval.capstone.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;

public class RecordingTimer {
    private ResultReceiver receiver;
    private Handler handler;
    private Bundle bundle;

    private long currentTimeMillis, startTime, timeElapsed;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            currentTimeMillis = SystemClock.uptimeMillis() - startTime + timeElapsed;
            bundle.putLong("current-time", currentTimeMillis);
            receiver.send(0, bundle);
            handler.postDelayed(this, 1000);
        }
    };

    RecordingTimer(ResultReceiver receiver) {
        this.receiver = receiver;
        handler = new Handler();
        bundle = new Bundle();
    }

    void setReceiver(ResultReceiver receiver) {
        this.receiver = receiver;
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

    public long getStartTime() {
        return startTime;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }
}
