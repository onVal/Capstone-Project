package com.onval.capstone.di;

import com.onval.capstone.activities.MainActivity;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {
    //this lets know MainActivity that it is a potential client

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @Provides
    static String provideString() {
        return "bubu is real, and injected leell";
    }
}
