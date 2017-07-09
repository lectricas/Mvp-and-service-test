package org.codepond.daggersample.presentation.main.di;

import org.codepond.daggersample.presentation.main.ForaActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;


@Subcomponent(modules = { ForaActivityModule.class })
public interface MainActivitySubComponent extends AndroidInjector<ForaActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ForaActivity> {
    }
}
