package com.example.jukespot.spotifyjukespot.WebServices;

import android.support.annotation.NonNull;

import com.example.jukespot.spotifyjukespot.Classes.JukeBox;
import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;

import java.util.List;

/**
 * Created by Dominique on 11/3/2017.
 */

public interface ServiceGatewayListener {
    public void onSuccess();

    public void gotPlaylists(List<JukeBoxResponse> jukeboxes);

    public void onError();
}
