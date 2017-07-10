package com.lectricas.toktest.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public final class RxBus {
    private final PublishSubject<Object> bus = PublishSubject.create();

    public void send(final Object event) {
        bus.onNext(event);
    }

    public void sendError(final Throwable throwable) {
        bus.onError(throwable);
    }

    public Observable<Object> toObservable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }
}