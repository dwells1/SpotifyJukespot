package com.example.jukespot.spotifyjukespot.MusicPlayer;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.jukespot.spotifyjukespot.CurrentQueueFragment;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Lino on 9/29/2017.
 */

public class MusicPlayer implements MusicPlayerInterface
        , CurrentQueueFragment.OnFragmentInteractionListener
        , SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    private static final String TAG = MusicPlayer.class.getSimpleName();
    Logging log = new Logging();

    private SpotifyPlayer spotifyPlayer;
    private Metadata playerMetadata;
    private PlaybackState playerPlaybackState;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
            log.logMessage(TAG,"Callback Success");
        }

        @Override
        public void onError(Error error) {
            log.logMessage(TAG, "ERROR:" + error);
        }
    };
    public void initSpotifyPlayer(Config playerConfig){
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayerToImplement) {
                spotifyPlayer = spotifyPlayerToImplement;
                spotifyPlayer.addConnectionStateCallback(MusicPlayer.this);
                spotifyPlayer.addNotificationCallback(MusicPlayer.this);
                //playerPlaybackState = spotifyPlayer.getPlaybackState();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });

        //playerMetadata = spotifyPlayer.getMetadata();
    }
    @Override
    public void play(String uri) {
       // log.logMessage(TAG,"song playing: " + uri);
        spotifyPlayer.playUri(null, uri, 0, 0);
        //playerMetadata = spotifyPlayer.getMetadata();
        //log.logMessage(TAG,"CURRENTLY PLAYING METADATA TRACK" + playerMetadata.currentTrack.name);
    }
    @Override
    public void queue(String uri){
        spotifyPlayer.queue(mOperationCallback,uri);
    }

    @Override
    public void pause() {
       spotifyPlayer.pause(mOperationCallback);
    }
    @Override
    public void next() {
        spotifyPlayer.skipToNext(mOperationCallback);
    }

    @Override
    public void prev() {
        spotifyPlayer.skipToPrevious(mOperationCallback);
    }

    @Override
    public void resume() {
        spotifyPlayer.resume(mOperationCallback);
    }

    @Override
    public boolean isPlaying() {
        if(playerPlaybackState == null){
            return false;
        }
        return playerPlaybackState.isPlaying;
    }

    /* This will return the track in the format provided by the
     * SDK METADATA Which is as follows:
     *  https://spotify.github.io/android-sdk/player/com/spotify/sdk/android/player/Metadata.Track.html
     **/

    @Nullable
    @Override
    public Metadata.Track getCurrentTrack() {
        return playerMetadata.currentTrack;
    }
    @Nullable
    @Override
    public Metadata.Track getNextTrack() {
        return playerMetadata.currentTrack;
    }
    @Nullable
    @Override
    public Metadata.Track getPrevTrack() {
        return playerMetadata.currentTrack;
    }

    @Override
    public void endCurrentPlayer() {
        log.logMessage(TAG,"destroying player");
        spotifyPlayer.logout();
        spotifyPlayer.removeConnectionStateCallback(this);
        spotifyPlayer.removeNotificationCallback(this);
        Spotify.destroyPlayer(this);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLoggedIn() {
        log.logMessage(TAG,"PLAYER LOGGED IN");
    }

    @Override
    public void onLoggedOut() {
        log.logMessage(TAG,"Player ended Logged Out");
    }

    @Override
    public void onLoginFailed(Error error) {
        log.logErrorNoToast(TAG,"PLAYER FAILED TO LOGIN USER");
    }

    @Override
    public void onTemporaryError() {
        log.logErrorNoToast(TAG, "TEMPORARY ERROR");
    }

    @Override
    public void onConnectionMessage(String s) {
        log.logErrorNoToast(TAG, "Connection Message : " + s);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        log.logMessage(TAG, "EVENT : " + playerEvent);
        playerMetadata = spotifyPlayer.getMetadata();
        playerPlaybackState = spotifyPlayer.getPlaybackState();
        log.logMessage(TAG, "META : " + playerMetadata );
        log.logMessage(TAG,"Playback State : " + playerPlaybackState);
    }

    @Override
    public void onPlaybackError(Error error) {
        log.logErrorNoToast(TAG,"ERROR PLAYBACK :" + error);
    }
}
