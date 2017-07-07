
package org.codepond.daggersample.presentation.main.mvp;


import android.util.Log;
import android.view.View;

import org.codepond.daggersample.persistence.ForaRetrofitApi;
import org.codepond.daggersample.presentation.base.BasePresenter;
import org.codepond.daggersample.util.RxBus;

import com.arellomobile.mvp.InjectViewState;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MainActivityPresenter extends BasePresenter<MainActivityView> {

    ForaRetrofitApi api;

    RxBus bus;

    @Inject
    public MainActivityPresenter(ForaRetrofitApi api, RxBus bus) {
        this.api = api;
        this.bus = bus;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        Log.d("MainActivityPresenter", "firstAttach");
        requestSessionCredentials();
    }

    @Override
    public void attachView(MainActivityView view) {
        super.attachView(view);
        Log.d("MainActivityPresenter", "attached");
        bus.toObservable()
                .subscribe(o -> {
                    if (o instanceof Publisher) {
                        getViewState().onPublisherConnected(((Publisher) o).getView());
                    } else if (o instanceof Subscriber) {
                        getViewState().onSubscriberConnected(((Subscriber) o).getView());
                    }
                });
    }

    public void requestSessionCredentials() {
        api.getSession()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(session -> {
                    getViewState().onSessionObtained(session);
                });
    }
}
