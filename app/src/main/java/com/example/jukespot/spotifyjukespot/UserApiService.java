package com.example.jukespot.spotifyjukespot;

import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface UserApiService {
    @GET("login")
    Call<LoginResponse> login(@Query("service_key") String key,
                              @Query("json") String json);

    @GET("addUser")
    Call<LoginResponse> addUser(@Query("service_key") String key,
                                @Query("json") String json);
}
