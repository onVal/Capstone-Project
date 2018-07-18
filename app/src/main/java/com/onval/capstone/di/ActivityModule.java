package com.onval.capstone.di;

import android.app.Activity;

import com.onval.capstone.MainActivity;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module (subcomponents = ActivityModule.MainActivitySubcomponent.class)
public abstract class ActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindMainActivityInjectorFactory(MainActivitySubcomponent.Builder builder);

    @Subcomponent(modules = AppModule.class)
    @Singleton
    public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {
        @Subcomponent.Builder
        abstract class Builder extends AndroidInjector.Builder<MainActivity> {
            abstract Builder requestModule(AppModule module);

            @Override
            public void seedInstance(MainActivity instance) {
                requestModule(new AppModule(instance));
            }
        }
    }
}
