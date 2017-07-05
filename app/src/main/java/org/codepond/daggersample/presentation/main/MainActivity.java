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
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import org.codepond.daggersample.R;
import org.codepond.daggersample.persistence.ForaSession;
import org.codepond.daggersample.presentation.main.mvp.MainActivityPresenter;
import org.codepond.daggersample.presentation.main.mvp.MainActivityView;
import org.codepond.daggersample.presentation.service.ConnectionService;
import org.codepond.daggersample.util.Util;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends MvpAppCompatActivity implements MainActivityView,
        EasyPermissions.PermissionCallbacks {

    @BindView(R.id.subscriber_container)
    FrameLayout subscriberContainer;
    @BindView(R.id.publisher_container)
    FrameLayout publisherContainer;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((ConnectionService.LocalBinder) binder).getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private ConnectionService service;
    private boolean bound;

    @Inject
    @InjectPresenter
    MainActivityPresenter presenter;

    @ProvidePresenter
    MainActivityPresenter providesPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ConnectionService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        requestPermissions();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @AfterPermissionGranted(Util.RC_VIDEO_APP_PERM)
    private void requestPermissions() {

        if (EasyPermissions.hasPermissions(this, Util.PERMS)) {
            presenter.requestSessionCredentials();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_video_app), Util.RC_VIDEO_APP_PERM, Util.PERMS);
        }
    }

    @Override
    public void onSessionObtained(ForaSession sessionCredintials) {
        service.startSessions(sessionCredintials);
    }

    @Override
    public void onPublisherConnected(View publisherView) {
        publisherContainer.addView(publisherView);
    }

    @Override
    public void onSubscriberConnected(View subscriberView) {
        subscriberContainer.addView(subscriberView);
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


}
