package com.onval.capstone.service;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

public class RecordService extends IntentService {
    public static final String START_RECORDING = "start-recording";
    public static final String PAUSE_RECORDING = "pause=recording";

    private MediaRecorder recorder;
    private boolean isPlaying = false;

    public RecordService() {
        super("RecordService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        initializeRecorder();
    }

    private void initializeRecorder() {
        String fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.mp4";

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            recorder.prepare();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void startRecording() {
        if (!isPlaying) {
            recorder.start();
            isPlaying = true;
        }
    }

    public void pauseRecording() {
        if (isPlaying) {
            recorder.stop();
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        RecordBinder binder = new RecordBinder(this);
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recorder != null)
            recorder.release();
    }
}
