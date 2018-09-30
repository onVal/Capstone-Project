package com.onval.capstone.service;

import android.app.Service;
import android.os.Binder;

public class RecordingBinder extends Binder {
    private Service service;

    RecordingBinder(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
