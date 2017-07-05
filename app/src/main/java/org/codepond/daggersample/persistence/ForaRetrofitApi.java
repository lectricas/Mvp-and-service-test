package org.codepond.daggersample.persistence;


import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lectricas on 01-Jul-17.
 */

public interface ForaRetrofitApi {

    @GET("/session")
    Single<ForaSession> getSession();
}
