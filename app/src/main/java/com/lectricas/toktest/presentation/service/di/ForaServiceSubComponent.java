package com.lectricas.toktest.presentation.service.di;

import com.lectricas.toktest.presentation.service.ForaService;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by lectricas on 05-Jul-17.
 */
@Subcomponent(modules = { ForaServiceModule.class })
public interface ForaServiceSubComponent extends AndroidInjector<ForaService> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ForaService> {
    }
}