package com.lectricas.toktest.presentation.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.lectricas.toktest.presentation.service.ForaService;

import com.lectricas.toktest.presentation.main.mvp.ForaActivityPresenter;
import com.lectricas.toktest.presentation.main.mvp.ForaActivityView;
import com.lectricas.toktest.presentation.service.event.ServiceCallbacks;
import com.lectricas.toktest.util.Util;
import com.opentok.android.OpentokError;

import org.lectricas.toktest.R;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class ForaActivity extends AppCompatActivity implements ForaActivityView, ServiceCallbacks,
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

    @Inject
    @InjectPresenter
    ForaActivityPresenter presenter;

    @ProvidePresenter
    ForaActivityPresenter providesPresenter() {
        return presenter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
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
        if (!isChangingConfigurations() && service != null) {
            service.stopSession();
        }
        unBindService();
        publisherContainer.removeView(publisherContainer.getChildAt(0));
        subscriberContainer.removeView(subscriberContainer.getChildAt(0));
    }

    @Override
    public void onStreamDropped() {
        subscriberContainer.removeView(subscriberContainer.getChildAt(0));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            stopService(serviceIntent);
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
                service.startSessions(ForaActivity.this);
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
    }

    @Override
    public void onError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onOpenTokError(OpentokError throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
