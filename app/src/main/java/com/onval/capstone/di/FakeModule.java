package com.onval.capstone.di;

import com.onval.capstone.FakeData;

import dagger.Module;
import dagger.Provides;

@Module
public class FakeModule {
    @Provides
    FakeData provideFakeData() {
        return new FakeData("MAMMAMIA CE L'HO FATTA");
    }
}
