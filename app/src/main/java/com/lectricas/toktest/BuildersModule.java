package com.lectricas.toktest;

import android.app.Activity;
import android.app.Service;

import com.lectricas.toktest.presentation.main.ForaActivity;
import com.lectricas.toktest.presentation.main.di.MainActivitySubComponent;
import com.lectricas.toktest.presentation.service.ForaService;
import com.lectricas.toktest.presentation.service.di.ForaServiceSubComponent;

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
    @ActivityKey(ForaActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindActivityInjector(MainActivitySubComponent.Builder builder);

    @Binds
    @IntoMap
    @ServiceKey(ForaService.class)
    abstract AndroidInjector.Factory<? extends Service> bindServiceInjector(ForaServiceSubComponent.Builder builder);
}
