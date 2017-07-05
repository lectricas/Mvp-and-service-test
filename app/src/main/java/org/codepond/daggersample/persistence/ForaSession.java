package org.codepond.daggersample.persistence;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lectricas on 05-Jul-17.
 */

@Getter
@Setter
public class ForaSession {

    @SerializedName("apiKey")
    private String apiKey;

    @SerializedName("sessionId")
    private String sessionId;

    @SerializedName("token")
    private String token;
}
