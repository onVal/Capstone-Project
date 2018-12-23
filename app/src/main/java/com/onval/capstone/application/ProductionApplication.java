package com.onval.capstone.application;

import android.app.Activity;
import android.app.Application;

import com.facebook.stetho.Stetho;
import com.onval.capstone.di.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;

public class ProductionApplication extends Application
        implements HasActivityInjector {
    @Inject
    public DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        DaggerAppComponent.create().inject(this);
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
