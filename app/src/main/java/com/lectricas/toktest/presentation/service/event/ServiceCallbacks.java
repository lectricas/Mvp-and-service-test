package com.lectricas.toktest.presentation.service.event;

import android.view.View;

import com.opentok.android.OpentokError;

/**
 * Created by lectricas on 10.07.2017.
 */

public interface ServiceCallbacks {

    void onPublisherConnected(View publisherView);
    void onSubscriberConnected(View subscriberView);
    void onStreamDropped();
    void onError(Throwable throwable);
    void onOpenTokError(OpentokError throwable);
}
