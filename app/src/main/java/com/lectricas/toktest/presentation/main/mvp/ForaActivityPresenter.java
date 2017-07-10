
package com.lectricas.toktest.presentation.main.mvp;


import com.lectricas.toktest.persistence.ForaRetrofitApi;
import com.lectricas.toktest.presentation.base.BasePresenter;
import com.lectricas.toktest.presentation.service.event.PublisherConnectedEvent;
import com.lectricas.toktest.presentation.service.event.StreamDroppedBySubscriberEvent;
import com.lectricas.toktest.presentation.service.event.SubscriberConnectedEvent;
import com.lectricas.toktest.util.RxBus;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

@InjectViewState
@Singleton
public class ForaActivityPresenter extends BasePresenter<ForaActivityView> {

    ForaRetrofitApi api;

    @Inject
    public ForaActivityPresenter(ForaRetrofitApi api, RxBus bus) {
        this.api = api;

        Disposable d2 = bus.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    if (o instanceof PublisherConnectedEvent) {
                        getViewState().onPublisherConnected(((PublisherConnectedEvent) o).getView());
                    } else if (o instanceof SubscriberConnectedEvent) {
                        getViewState().onSubscriberConnected(((SubscriberConnectedEvent) o).getView());
                    } else if (o instanceof StreamDroppedBySubscriberEvent) {
                        getViewState().onStreamDropped();
                    }
                }, throwable -> {
                    getViewState().onError(throwable);
                });
        disposeOnDestroy(d2);
    }
}
