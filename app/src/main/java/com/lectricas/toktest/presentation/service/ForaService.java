package com.lectricas.toktest.presentation.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import com.lectricas.toktest.persistence.ForaRetrofitApi;
import com.lectricas.toktest.presentation.service.event.PublisherConnectedEvent;
import com.lectricas.toktest.presentation.service.event.StreamDroppedBySubscriberEvent;
import com.lectricas.toktest.presentation.service.event.SubscriberConnectedEvent;
import com.lectricas.toktest.util.RxBus;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ForaService extends Service implements Session.SessionListener, PublisherKit.PublisherListener {

    private final IBinder binder = new LocalBinder();

    private boolean connected;

    Disposable disposableGetServerSession;

    @Nullable
    private Session session;

    private Publisher publisher;
    private Subscriber subscriber;

    @Inject
    RxBus bus;

    @Inject
    ForaRetrofitApi api;

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

    public void startSessions() {
        if (!connected) {
            disposableGetServerSession = api.getSession()
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foraSession -> {
                        session = new Session.Builder(this, foraSession.getApiKey(), foraSession.getSessionId()).build();
                        session.setSessionListener(this);
                        session.connect(foraSession.getToken());
                    }, throwable -> {
                        bus.sendError(new RuntimeException(throwable));
                    });
        } else if (session != null) {
            session.onResume();
        }
    }

    public void onActivityPaused() {
        disposableGetServerSession.dispose();
        if (session != null) {
            session.onPause();
        }
    }

    public void stopSession() {
        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public void onConnected(Session session) {
        connected = true;
        publisher = new Publisher.Builder(this).build();
        publisher.setPublisherListener(this);

        publisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.publish(publisher);

        bus.send(new PublisherConnectedEvent(publisher.getView()));
    }

    @Override
    public void onDisconnected(Session session) {
        connected = false;
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        // TODO: 09-Jul-17 black Screen if subscriber is not reinitilized
//        if (subscriber == null) {
        subscriber = new Subscriber.Builder(this, stream).build();
        subscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.subscribe(subscriber);
        bus.send(new SubscriberConnectedEvent(subscriber.getView()));
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        bus.send(new StreamDroppedBySubscriberEvent());
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        // TODO: 10-Jul-17 errorHandling
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
