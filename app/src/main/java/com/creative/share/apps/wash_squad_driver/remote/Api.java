package com.creative.share.apps.wash_squad_driver.remote;

import com.creative.share.apps.wash_squad_driver.services.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Api {

    private static Retrofit retrofit = null;

    private static Retrofit getRetrofit(String lang,String base_url)
    {

        Interceptor interceptor   = chain -> {
            Request request = chain.request();

            Request accept = request.newBuilder()
                    .addHeader("ACCEPT", "application/json")
                    .addHeader("lang",lang)
                    .build();
            return chain.proceed(accept);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .writeTimeout(90,TimeUnit.SECONDS)
                .readTimeout(90,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        return retrofit;
    }


    public static Service getService(String  lang,String base_url)
    {
        return getRetrofit(lang,base_url).create(Service.class);
    }
}

