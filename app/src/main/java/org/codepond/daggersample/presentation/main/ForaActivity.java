/*
 * Copyright 2017 Polusov Alexey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codepond.daggersample.presentation.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import org.codepond.daggersample.R;
import org.codepond.daggersample.persistence.ForaSession;
import org.codepond.daggersample.presentation.main.mvp.ForaActivityPresenter;
import org.codepond.daggersample.presentation.main.mvp.ForaActivityView;
import org.codepond.daggersample.presentation.service.ForaService;
import org.codepond.daggersample.util.Util;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class ForaActivity extends AppCompatActivity implements ForaActivityView,
        EasyPermissions.PermissionCallbacks {

    @BindView(R.id.subscriber_container)
    FrameLayout subscriberContainer;
    @BindView(R.id.publisher_container)
    FrameLayout publisherContainer;

    private ServiceConnection connection;

    @Nullable
    private ForaService service;

    private boolean bound;
    private Intent serviceIntent;

    MvpDelegate<? extends ForaActivity> mvpDelegate;

    @Inject
    @InjectPresenter
    ForaActivityPresenter presenter;

    @ProvidePresenter
    ForaActivityPresenter providesPresenter() {
        Log.d("ForaActivity", "presenter:" + presenter);
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        getMvpDelegate().onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        serviceIntent = new Intent(this, ForaService.class);
        getApplicationContext().startService(serviceIntent);
        connection = createServiceConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (service != null) {
            service.onActivityPaused();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        getMvpDelegate().onDetach();
        if (!isChangingConfigurations() && service != null) {
            service.stopSession();
        }
        unBindService();
        publisherContainer.removeAllViews();
        subscriberContainer.removeAllViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            getMvpDelegate().onDestroy();
        }
    }

    @AfterPermissionGranted(Util.RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(this, Util.PERMS)) {
            bindService();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_video_app), Util.RC_VIDEO_APP_PERM, Util.PERMS);
        }
    }

    @Override
    public void onSessionObtained(ForaSession sessionCredintials) {
        Log.d("ForaActivity", "sessionObtained");
        if (service != null) {
            Log.d("ForaActivity", "startSession");
            service.startSessions(sessionCredintials);
        }
    }

    @Override
    public void onPublisherConnected(View publisherView) {
        Log.d("ForaActivity", "publisherConnected");
        publisherContainer.addView(publisherView);
    }

    @Override
    public void onSubscriberConnected(View subscriberView) {
        Log.d("ForaActivity", "subscriberConnected");
//        subscriberContainer.addView(subscriberView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void bindService(){
        getApplicationContext().bindService(serviceIntent, connection, 0);
    }

    private void unBindService(){
        if (bound) {
            getApplicationContext().unbindService(connection);
            bound = false;
        }
    }

    private ServiceConnection createServiceConnection() {
        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                service = ((ForaService.LocalBinder) binder).getService();
                Log.d("ForaActivity", "ServiceConnectionConnected");
                getMvpDelegate().onAttach();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("ForaActivity", "disconnected");
                getMvpDelegate().onDetach();
                bound = false;
            }
        };
    }

    public MvpDelegate getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new MvpDelegate<>(this);
        }
        return mvpDelegate;
    }
}
