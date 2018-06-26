package com.onval.capstone.di;

import com.onval.capstone.ProductionApplication;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Component(modules = {
        AndroidInjectionModule.class,
        ActivityModule.class
})
public interface AppComponent {
    void inject(ProductionApplication app);
}
