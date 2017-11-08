package com.example.jukespot.spotifyjukespot.MusicPlayer;


import com.google.common.util.concurrent.Service;

/**
 * Created by linos on 11/8/2017.
 */

public interface SongEndListener {
    public abstract void onSuccess();
    public abstract void onFailure();
}
