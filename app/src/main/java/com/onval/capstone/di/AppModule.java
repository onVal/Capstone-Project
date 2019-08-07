package com.onval.capstone.di;

import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.onval.capstone.activities.MainActivity;
import com.onval.capstone.viewmodel.CategoriesViewModel;

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
    static CategoriesViewModel provideCategoriesViewModel(MainActivity activity) {
        return ViewModelProviders.of(activity).get(CategoriesViewModel.class);
    }
}
