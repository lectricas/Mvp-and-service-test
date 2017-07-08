package org.codepond.daggersample.presentation.main.di;

import org.codepond.daggersample.presentation.main.MainActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;


@Subcomponent(modules = { MainActivityModule.class })
public interface MainActivitySubComponent extends AndroidInjector<MainActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<MainActivity> {
    }
}
