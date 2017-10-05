package com.example.jukespot.spotifyjukespot.remote;

/**
 * Created by Alex on 10/3/2017.
 */

public class ApiUtils {

    public static final String BASE_URL = "http://easel2.fulgentcorp.com:8081/";

    public static UserService getUserService(){

        return RetrofitClient.getClient(BASE_URL).create(UserService.class);
    }
}
