package com.example.jukespot.spotifyjukespot.remote;

/**
 * Created by Alex on 10/3/2017.
 */

import retrofit2.Retrofit;
import retrofit2.*;
import com.google.gson.Gson;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Converter;
public class RetrofitClient {

    private static Retrofit retrofit = null;

//static
    public static Retrofit getClient(String url){
            if(retrofit == null){
                retrofit = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;

        }


}
