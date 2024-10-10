package com.imax.app.data.api;

import android.content.Context;

import com.imax.app.BuildConfig;
import com.imax.app.data.api.interceptors.HeaderInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XMSApi {
    private static EasyfactApiInterface API_FACTURACION;
    private static Retrofit retrofit;

    public static EasyfactApiInterface getApiEasyfact(Context context){
        if (API_FACTURACION == null){
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            httpBuilder.addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY));//Crear el interceptor y configurar el nivel del log
            httpBuilder.addInterceptor(new HeaderInterceptor(context));

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build())
                    .build();
            API_FACTURACION = retrofit.create(EasyfactApiInterface.class);
        }
        return API_FACTURACION;
    }

    public static Retrofit getRetrofit(Context context){
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        httpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));//Crear el interceptor y configurar el nivel del log
        httpBuilder.addInterceptor(new HeaderInterceptor(context));

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build())
                    .build();
        }

        return retrofit;
    }
}
