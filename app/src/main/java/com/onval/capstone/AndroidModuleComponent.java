package com.onval.capstone;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {AndroidModule.class})
@Singleton
public interface AndroidModuleComponent {
    Context context();

}
