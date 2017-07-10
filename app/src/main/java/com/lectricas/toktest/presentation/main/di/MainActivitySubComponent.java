package com.lectricas.toktest.presentation.main.di;

import com.lectricas.toktest.presentation.main.ForaActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;


@Subcomponent(modules = { ForaActivityModule.class })
public interface MainActivitySubComponent extends AndroidInjector<ForaActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ForaActivity> {
    }
}
