package org.codepond.daggersample.presentation.service.di;

import org.codepond.daggersample.presentation.main.MainActivity;
import org.codepond.daggersample.presentation.main.di.MainActivityModule;
import org.codepond.daggersample.presentation.service.ConnectionService;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by lectricas on 05-Jul-17.
 */
@Subcomponent(modules = { CServiceModule.class })
public interface CServiceSubComponent extends AndroidInjector<ConnectionService> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ConnectionService> {
    }
}