package com.lectricas.toktest.presentation.service.event;

import android.view.View;

import lombok.Getter;

/**
 * Created by lectricas on 09-Jul-17.
 */

@Getter
public class PublisherConnectedEvent {

    View view;

    public PublisherConnectedEvent(View view) {
        this.view = view;
    }
}
