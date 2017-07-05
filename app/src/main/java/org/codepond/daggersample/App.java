package org.codepond.daggersample;

import android.app.Activity;
import android.app.Application;
import android.app.Service;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;
import dagger.android.HasDispatchingServiceInjector;


public class App extends Application implements HasDispatchingActivityInjector, HasDispatchingServiceInjector {
    @Inject DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }
}
