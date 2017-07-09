package org.codepond.daggersample.presentation.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import org.codepond.daggersample.persistence.ForaSession;
import org.codepond.daggersample.util.RxBus;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


public class ForaService extends Service implements Session.SessionListener, PublisherKit.PublisherListener {

    private final IBinder binder = new LocalBinder();

    private boolean connected;

    private Session session;
    private Publisher publisher;
    private Subscriber subscriber;

    @Inject
    RxBus bus;

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startSessions(ForaSession foraSession) {
        if (!connected) {
            session = new Session.Builder(this, foraSession.getApiKey(), foraSession.getSessionId()).build();
            session.setSessionListener(this);
            session.connect(foraSession.getToken());
        } else {
            session.onResume();
        }
    }

    public void onActivityPaused() {
        Log.d("ForaService", "activityPaused");
        session.onPause();
    }

    public void stopSession() {
        session.disconnect();
    }

    @Override
    public void onConnected(Session session) {
        connected = true;
        publisher = new Publisher.Builder(this).build();
        publisher.setPublisherListener(this);

        publisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        this.session.publish(publisher);

        bus.send(publisher);
    }

    @Override
    public void onDisconnected(Session session) {
        connected = false;
        Log.d("ForaService", "disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (subscriber == null) {
            subscriber = new Subscriber.Builder(this, stream).build();
            subscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            this.session.subscribe(subscriber);
            bus.send(subscriber);
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d("ForaService", "dropped");
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.d("ForaService", opentokError.getMessage());
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    public class LocalBinder extends Binder {
        public ForaService getService() {
            return ForaService.this;
        }
    }
}
