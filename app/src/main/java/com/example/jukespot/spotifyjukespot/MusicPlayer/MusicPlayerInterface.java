package com.example.jukespot.spotifyjukespot.MusicPlayer;

import android.support.annotation.Nullable;

import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Lino on 9/29/2017.
 */

public interface MusicPlayerInterface{

    void play(String uri);
    void queue(String uri);
    void pause();
    void resume();
    void next();
    void prev();

    boolean isPlaying();


    /* This will return the track in the format provided by the
     * SDK METADATA Which is as follows:
     *  https://spotify.github.io/android-sdk/player/com/spotify/sdk/android/player/Metadata.Track.html
     * */
    @Nullable
    Metadata.Track getCurrentTrack();
    @Nullable
    Metadata.Track getNextTrack();
    @Nullable
    Metadata.Track getPrevTrack();


    void endCurrentPlayer();
}
