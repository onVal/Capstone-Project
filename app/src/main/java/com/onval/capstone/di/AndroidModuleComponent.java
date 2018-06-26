package com.onval.capstone.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AndroidModule.class})
@Singleton
public interface AndroidModuleComponent {
    Context context();
}
