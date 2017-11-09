package com.example.jukespot.spotifyjukespot.WebServices;

import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.JukeResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface UserApiService {
    @GET("login")
    Call<LoginResponse> login(@Query("service_key") String key,
                              @Query("json") String json);

    @GET("addUser")
    Call<LoginResponse> addUser(@Query("service_key") String key,
                                @Query("json") String json);

    @GET("updateMyPlaylist")
    Call<LoginResponse> updateMyPlaylist(@Query("session_token") String token,
                                       @Query("json") String json);

    @GET("updateUser")
    Call<LoginResponse> updateUser(@Query("session_token") String token,
                                       @Query("json") String json);

    @GET("getUser")
    Call<ResponseBody> getUser(@Query("session_token") String token);

    @GET("getPlaylists")
    Call<JukeResponse> getPlaylists(@Query("session_token") String token,
                                    @Query("json") String json);

    @GET("addSongToPlaylist")
    Call<LoginResponse> addSongToPlaylist(@Query("session_token") String token,
                                    @Query("json") String json);
}