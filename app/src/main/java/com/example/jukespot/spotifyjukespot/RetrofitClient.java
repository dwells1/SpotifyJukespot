package com.example.jukespot.spotifyjukespot;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nique on 10/4/2017.
 */

public class RetrofitClient {
    private static RetrofitClient instance = new RetrofitClient();
    private static Retrofit retrofit =null;

    private RetrofitClient() {};

    public static RetrofitClient getInstance(){
        return instance;
    }

    public static Retrofit getClient(String baseUrl) {
        if (retrofit == null) {
            OkHttpClient.Builder okh = new OkHttpClient.Builder();
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            okh.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okh.build())
                    .build();
        }
        return retrofit;
    }
}
