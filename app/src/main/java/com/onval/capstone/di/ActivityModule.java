package com.onval.capstone.di;

//@Module(subcomponents = CategoriesFragmentSubcomponent.class)
//abstract class ActivityModule {
//    @Binds
//    @IntoMap
//    @ActivityKey(MainActivity.class)
//    abstract AndroidInjector.Factory<? extends Activity>
//        bindCategoriesFragmentInjectorFactory(CategoriesFragmentSubcomponent.Builder builder);
//
//    @Subcomponent(modules = MainActivitySubcomponent)
//
//}

import android.app.Activity;

import com.onval.capstone.MainActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Subcomponent;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module (subcomponents = ActivityModule.MainActivitySubcomponent.class)
abstract class ActivityModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
    bindMainActivityInjectorFactory(MainActivitySubcomponent.Builder builder);

    @Subcomponent(modules = FakeModule.class)
    public interface MainActivitySubcomponent extends AndroidInjector<MainActivity> {
        @Subcomponent.Builder
        abstract class Builder extends AndroidInjector.Builder<MainActivity> { }
    }
}

//    @ContributesAndroidInjector(modules = FakeModule.class)
//    abstract MainActivity contributeMainActivityInjector();
