package com.onval.capstone.di;

import android.app.Application;

import com.onval.capstone.application.ProductionApplication;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityBuildersModule.class,
})
public interface AppComponent extends AndroidInjector<ProductionApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
