package com.example.jukespot.spotifyjukespot.WebServices;

import android.content.Context;

import com.example.jukespot.spotifyjukespot.Classes.JukeBox;
import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.Classes.JukeResponse;
import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.Discoverable;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
import com.example.jukespot.spotifyjukespot.R;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dominique on 11/2/2017.
 */

public class ServicesGateway {
    private static Logging log;
    private RetrofitClient rfit;
    private UserApiService client;
    private static final String TAG = ServicesGateway.class.getSimpleName();
    private static final String loginUrl = "http://easel2.fulgentcorp.com:8081/";
    private User user;
    private static ServicesGateway instance = null;
    private ServiceGatewayListener mListener;

    public static ServicesGateway getInstance() {
        if (instance == null) {
            instance = new ServicesGateway();
        }
        return instance;
    }

    private ServicesGateway() {
        rfit = RetrofitClient.getInstance();
        log = new Logging();
        user = User.getInstance();
    }

    public void setListener(ServiceGatewayListener listener) {
        this.mListener = listener;
    }

    public void login(final Context con, final String userName, final String password) {
        client = rfit.getClient(con.getString(R.string.web_service_url)).create(UserApiService.class);
        String json = "{\"login\":\"" + userName + "\",\"pw_hash\":\"" + password + "\"}";
        log.logMessage(TAG, json);
        Call<LoginResponse> call = client.login("jukespot", json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getResult().equals("ok")) {
                    log.logMessage(TAG, "login successful response is " +
                            response.body().getResult() + " " +
                            response.body().getUserSessionnToken());
                    user.setUserName(userName);
                    user.setPassword(password);
                    user.setSessionToken(response.body().getUserSessionnToken());
                    mListener.onSuccess();
                } else {
                    log.logMessageWithToast(con, TAG, "Incorrect Username or Password");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG, "login failed");
            }
        });
    }

    public void updateUser(final Context con) {
        client = rfit.getClient(con.getString(R.string.web_service_url)).create(UserApiService.class);
        String json = "{\"latitude\":\"" + user.getLatitude() + "\",\"longitude\":\"" + user.getLongitude() + "\"}";
        log.logMessage(TAG, json);
        Call<LoginResponse> call = client.updateUser(user.getSessionToken(), json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response != null) {
                    log.logMessage(TAG, "Successful update result " +
                            response.body().getResult() + " " + response.body().getMessage());
                } else {
                    log.logMessageWithToast(con, TAG, "Incorrect session token");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG, "update failed");
            }
        });
    }

    public void setDiscoverable(final Context con, Discoverable disc) {
        //client = rfit.getClient(con.getString(R.string.web_service_url)).create(UserApiService.class);
        String enabled = "{\"discoverable\":\"" + disc + "\"}";
        log.logMessage(TAG, "jukebox converted to json: " + enabled);
        updatePlaylist(con, enabled);
    }

    public void modifyPlaylistParameters(final Context con, JukeBox jukeBox) {
        Gson gson = new Gson();
        String json = gson.toJson(jukeBox);
        log.logMessage(TAG, "jukebox converted to json: " + json);
        updatePlaylist(con, json);
    }

    public void addSongTOPlaylist(final Context con,Integer transaction_id, SimpleTrack currentSong) {
        Gson gson = new Gson();
        String json = gson.toJson(currentSong);
        log.logMessage(TAG,"track:" + json);
        json =  "{\"transaction_id\":"+ transaction_id + ","+json.substring(1);
        log.logMessage(TAG,"track w/ trans id: " + json);
//        String json = gson.toJson(currentSong);
//        log.logMessage(TAG, "Queue converted to json: " + json);
        Call<LoginResponse> call = client.addSongToPlaylist(user.getSessionToken(), json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
               if (response.body().getResult().equals("ok")) {
                    log.logMessage(TAG, "updatePlaylist successful response is " +
                            response.body().getResult() + " ");
                    mListener.onSuccess();
                } else if(response.body().getResult().equals("error")){
                    log.logMessageWithToast(con, TAG, response.body().getMessage());
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG, "update playlist failed");
            }
        });
    }

    private void updatePlaylist(final Context con, String json) {
        Call<LoginResponse> call = client.updateMyPlaylist(user.getSessionToken(), json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getResult().equals("ok")) {
                    log.logMessage(TAG, "updatePlaylist successful response is " +
                            response.body().getResult() + " " +
                            response.body().getMessage());
                    mListener.onSuccess();
                } else if(response.body().getResult().equals("error")){
                    log.logMessageWithToast(con, TAG, response.body().getMessage());
                    mListener.onError();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG, "update playlist failed");
            }
        });
    }

    public void getJukeboxes() {
        Gson gson = new Gson();
        log.logMessage(TAG,gson.toJson(new JukeResponse("ok",null)));
        String json = "{\"distance\":100000}";
        Call<JukeResponse> call = client.getPlaylists(user.getSessionToken(), json);
        call.enqueue(new Callback<JukeResponse>() {
            @Override
            public void onResponse(Call<JukeResponse> call, Response<JukeResponse> response) {
                if (response.body().getResult().equals("ok")) {
                    JukeResponse rows = new JukeResponse(response.body().getResult(),response.body().getRows());
                    log.logMessage(TAG, "Result is " +
                            response.body().getResult() + " " );
                    mListener.gotPlaylists(rows.getRows());
                    for(JukeBoxResponse j : rows.getRows()){
                        log.logMessage(TAG,"Transaction_id:"+Integer.toString(j.getTransaction_id())+
                        "\nPlaylist playlist_name:" + j.getLocation_fields().getPlaylist_name() +
                        "\nLatitude:" + j.getLatitude() + " Longitude:" + j.getLongitude());
                    }
                }


            }

            @Override
            public void onFailure(Call<JukeResponse> call, Throwable t) {
                log.logMessage(TAG, "Failed to get Jukeboxes");
            }
        });
    }
    public void getMyPlaylist(){
        Call<JukeResponse> call = client.getMyPlaylist(user.getSessionToken());
        call.enqueue(new Callback<JukeResponse>() {
            @Override
            public void onResponse(Call<JukeResponse> call, Response<JukeResponse> response) {
                if (response.body().getResult().equals("ok")) {
                    JukeResponse playlistResponse = new JukeResponse(response.body().getResult(),response.body().getRows());
                    log.logMessage(TAG, "Result is " +
                            response.body().getResult() + " " );
                    mListener.gotPlaylists(playlistResponse.getRows());
                }
            }

            @Override
            public void onFailure(Call<JukeResponse> call, Throwable t) {
                log.logMessage(TAG, "Failed to get Jukeboxes");
            }
        });
    }
    public void joinJukebox(final Context con, String json){
        log.logMessage(TAG, "JSON RECV IN JOIN JUKEBOX: " + json);
        Call<LoginResponse> call = client.joinPlaylist(user.getSessionToken(), json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getResult().equals("ok")) {
                    log.logMessage(TAG, "Playlist Joined" +
                            response.body().getResult() + " " +
                            response.body().getMessage());
                    getJukeboxes();
                    //mListener.onSuccess();
                } else if(response.body().getResult().equals("error")){
                    log.logMessageWithToast(con, TAG, response.body().getMessage() + "in JoinJukebox");
                    mListener.onError();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG, "Playlist failed to join");
            }
        });
    }

    public void leaveJukebox(final Context con, String json){
        log.logMessage(TAG, "JSON RECV IN LEAVE JUKEBOX: " + json);
        Call<LoginResponse> call = client.leavePlaylist(user.getSessionToken(), json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.body().getResult().equals("ok")) {
                    log.logMessage(TAG, "Playlist Left" +
                            response.body().getResult() + " " +
                            response.body().getMessage());
                    mListener.onSuccess();
                } else if(response.body().getResult().equals("error")){
                    log.logMessageWithToast(con, TAG, response.body().getMessage());
                    mListener.onError();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG, "Failed to leave Playlist");
            }
        });
    }


}
