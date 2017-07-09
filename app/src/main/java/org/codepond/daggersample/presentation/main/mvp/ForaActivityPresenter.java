
package org.codepond.daggersample.presentation.main.mvp;


import android.util.Log;

import org.codepond.daggersample.persistence.ForaRetrofitApi;
import org.codepond.daggersample.presentation.base.BasePresenter;
import org.codepond.daggersample.util.RxBus;

import com.arellomobile.mvp.InjectViewState;
import com.opentok.android.Publisher;
import com.opentok.android.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
@Singleton
public class ForaActivityPresenter extends BasePresenter<ForaActivityView> {

    ForaRetrofitApi api;

    RxBus bus;

    @Inject
    public ForaActivityPresenter(ForaRetrofitApi api, RxBus bus) {
        Log.d("ForaActivityPresenter", "constructor");
        this.api = api;
        this.bus = bus;

        Disposable d1 = api.getSession()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(session -> {
                    getViewState().onSessionObtained(session);
                });
        disposeOnDestroy(d1);

        Disposable d2 = bus.toObservable()
                .subscribe(o -> {
                    if (o instanceof Publisher) {
                        getViewState().onPublisherConnected(((Publisher) o).getView());
                    } else if (o instanceof Subscriber) {
                        getViewState().onSubscriberConnected(((Subscriber) o).getView());
                    }
                });
        disposeOnDestroy(d2);
    }

    @Override
    public void destroyView(ForaActivityView view) {
        super.destroyView(view);
        Log.d("ForaActivityPresenter", "destroyView");
    }

    @Override
    public void attachView(ForaActivityView view) {
        super.attachView(view);
        Log.d("ForaActivityPresenter", "attach");
    }

    @Override
    public void detachView(ForaActivityView view) {
        super.detachView(view);
        Log.d("ForaActivityPresenter", "detach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ForaActivityPresenter", "destroy");
    }
}
