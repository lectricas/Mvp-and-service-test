package com.lectricas.toktest;

import com.lectricas.toktest.persistence.ForaRetrofitApi;
import com.lectricas.toktest.presentation.main.di.MainActivitySubComponent;
import com.lectricas.toktest.presentation.service.di.ForaServiceSubComponent;
import com.lectricas.toktest.util.RxBus;

import org.lectricas.toktest.BuildConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module(subcomponents = {
        MainActivitySubComponent.class,
        ForaServiceSubComponent.class,
})
public class AppModule {

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        final OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging);
        }
        return clientBuilder.build();
    }

    @Provides
    @Singleton
    ForaRetrofitApi provideRetrofitApi(OkHttpClient okHttpClient){
        Retrofit r = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return r.create(ForaRetrofitApi.class);
    }

    @Provides
    @Singleton
    RxBus providesRxbus() {
        return new RxBus();
    }
}
