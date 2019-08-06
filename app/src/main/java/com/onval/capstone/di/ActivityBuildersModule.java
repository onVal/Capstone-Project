package com.onval.capstone.di;

import com.onval.capstone.activities.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    //this lets know MainActivity that it is a potential client

    @ContributesAndroidInjector(modules = AppModule.class)
    abstract MainActivity contributeMainActivity();
}
