package com.example.jukespot.spotifyjukespot.remote;
import com.example.jukespot.spotifyjukespot.model.RestObj;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Alex on 10/3/2017.
 */

public interface UserService {
    //@GET(" /login?service_key=xxx&json={login:{username},pw_hash:{password}"}  ");

//http://easel2.fulgentcorp.com:8081/login?service_key=jukespot&json={"login":"bob5","pw_hash":"xxx"}

    @GET("login?service_key=jukespot&json=/login/{username}/pw_hash/{password}")
    Call<RestObj> login(@Query("service_key")String servic_key, @Query("login") String username, @Query("pw_hash")String password);

}
