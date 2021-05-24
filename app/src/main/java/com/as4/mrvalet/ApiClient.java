package com.as4.mrvalet;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by appleuser on 5/15/17.
 */

public class ApiClient {

    public static final String BASE_URL = "http://www.spotlightqa.com/api/MRVALET/";
    private static Retrofit retrofit = null;
    public static OkHttpClient httpClient;
    private static OkHttpClient.Builder httpClientBuilder;

    //
    public static Retrofit getClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient();
        httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(logging);  // <--



        httpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientBuilder.build())
                    .build();
        }

        return retrofit;
    }


}
