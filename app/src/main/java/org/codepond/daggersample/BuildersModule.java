package org.codepond.daggersample;

import android.app.Activity;
import android.app.Service;

import org.codepond.daggersample.presentation.main.MainActivity;
import org.codepond.daggersample.presentation.main.di.MainActivitySubComponent;
import org.codepond.daggersample.presentation.service.ConnectionService;
import org.codepond.daggersample.presentation.service.di.CServiceSubComponent;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.android.ServiceKey;
import dagger.multibindings.IntoMap;

@Module
public abstract class BuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindActivityInjector(MainActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @ServiceKey(ConnectionService.class)
    abstract AndroidInjector.Factory<? extends Service> bindServiceInjector(CServiceSubComponent.Builder builder);
}
