
package com.lectricas.toktest.presentation.main.mvp;


import com.lectricas.toktest.persistence.ForaRetrofitApi;
import com.lectricas.toktest.presentation.base.BasePresenter;
import com.lectricas.toktest.util.RxBus;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;
import javax.inject.Singleton;

@InjectViewState
@Singleton
public class ForaActivityPresenter extends BasePresenter<ForaActivityView> {

    ForaRetrofitApi api;

    @Inject
    public ForaActivityPresenter(ForaRetrofitApi api, RxBus bus) {
        this.api = api;
    }
}
