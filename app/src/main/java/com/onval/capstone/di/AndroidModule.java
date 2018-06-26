package com.onval.capstone.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AndroidModule {
    private Context context;

    public AndroidModule(Context context) {
        this.context = context;
    }

    @Provides @Singleton
    public Context provideContext() {
        return context;
    }
}
