package com.onval.capstone.service;

import android.app.Service;
import android.os.Binder;

public class RecordBinder extends Binder {
    private Service service;

    RecordBinder(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
