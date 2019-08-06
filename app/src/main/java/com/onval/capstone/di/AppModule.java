package com.onval.capstone.di;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.onval.capstone.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    @Provides
    static FragmentManager provideFragmentManager(MainActivity activity) {
        return activity.getSupportFragmentManager();
    }

    @Provides
    static String provideString() {
        return "bubu is real, and injected leell";
    }
}
