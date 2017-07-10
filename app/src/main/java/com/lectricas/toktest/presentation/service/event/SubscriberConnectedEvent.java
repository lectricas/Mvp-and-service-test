package com.lectricas.toktest.presentation.service.event;

import android.view.View;

import lombok.Getter;

/**
 * Created by lectricas on 09-Jul-17.
 */

@Getter
public class SubscriberConnectedEvent {

    View view;

    public SubscriberConnectedEvent(View view) {
        this.view = view;
    }
}
