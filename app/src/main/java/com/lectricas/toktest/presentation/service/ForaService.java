package com.lectricas.toktest.presentation.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.lectricas.toktest.presentation.service.event.ServiceCallbacks;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import com.lectricas.toktest.persistence.ForaRetrofitApi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ForaService extends Service implements Session.SessionListener, PublisherKit.PublisherListener {

    private final IBinder binder = new LocalBinder();

    Disposable disposableGetServerSession;

    private int status;

    @Nullable
    Subscriber subscriber;

    @Nullable
    Publisher publisher;

    @Nullable
    private Session session;

    @Nullable
    ServiceCallbacks callbacks;

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
        disposableGetServerSession.dispose();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startSessions(ServiceCallbacks callbacks) {
        this.callbacks = callbacks;
        if (session == null || status == Status.DISCONNECTED ||
                status == Status.DISCONNECTING) {
            disposableGetServerSession = api.getSession()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(foraSession -> {
                        session = new Session.Builder(this, foraSession.getApiKey(), foraSession.getSessionId()).build();
                        session.setSessionListener(this);
                        session.connect(foraSession.getToken());
                        status = Status.CONNECTING;
                    }, callbacks::onError);
        } else {
            session.onResume();
            if (publisher != null) {
                callbacks.onPublisherConnected(publisher.getView());
            }

            if (subscriber != null) {
                callbacks.onSubscriberConnected(subscriber.getView());
            }
        }
    }

    public void onActivityPaused() {
        if (session != null) {
            session.onPause();
        }
    }

    public void stopSession() {
        callbacks = null;
        if (session != null) {
            session.disconnect();
            session = null;
            status = Status.DISCONNECTING;
        }
    }

    @Override
    public void onConnected(Session session) {
        status = Status.CONNECTED;

        publisher = new Publisher.Builder(this).build();
        publisher.setPublisherListener(this);
        publisher.setPublishAudio(false);

        publisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.publish(publisher);

        if (callbacks != null) {
            callbacks.onPublisherConnected(publisher.getView());
        }
    }

    @Override
    public void onDisconnected(Session session) {
        status = Status.DISCONNECTED;
        publisher = null;
        subscriber = null;
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {

        subscriber = new Subscriber.Builder(this, stream).build();
        subscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
        session.subscribe(subscriber);

        if (callbacks != null) {
            callbacks.onSubscriberConnected(subscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (callbacks != null) {
            callbacks.onStreamDropped();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        if (callbacks != null) {
            callbacks.onOpenTokError(opentokError);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        if (callbacks != null) {
            callbacks.onOpenTokError(opentokError);
        }
    }

    public class LocalBinder extends Binder {
        public ForaService getService() {
            return ForaService.this;
        }
    }

    @IntDef({Status.CONNECTED, Status.DISCONNECTED, Status.CONNECTING, Status.DISCONNECTING})
    @Retention(RetentionPolicy.SOURCE)
    @interface Status {
        int CONNECTED = 1;
        int DISCONNECTED = 0;
        int CONNECTING = 2;
        int DISCONNECTING = 3;
    }
}
