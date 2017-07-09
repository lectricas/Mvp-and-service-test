package org.codepond.daggersample.presentation.base;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


public class BasePresenter<V extends MvpView> extends MvpPresenter<V> {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    protected void disposeOnDestroy(@NonNull Disposable subscription) {
        compositeDisposable.add(subscription);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
