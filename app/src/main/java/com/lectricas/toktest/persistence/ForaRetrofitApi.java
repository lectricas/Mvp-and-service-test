package com.lectricas.toktest.persistence;


import io.reactivex.Single;
import retrofit2.http.GET;

/**
 * Created by lectricas on 01-Jul-17.
 */

public interface ForaRetrofitApi {

    @GET("/session")
    Single<ForaSession> getSession();
}
