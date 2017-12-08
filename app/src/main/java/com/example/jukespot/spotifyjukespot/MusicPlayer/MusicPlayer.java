package com.example.jukespot.spotifyjukespot.MusicPlayer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.CurrentQueue.ChangeType;
import com.example.jukespot.spotifyjukespot.CurrentQueue.CurrentQueueFragment;
import com.example.jukespot.spotifyjukespot.CurrentlyPlaying.CurrentlyPlayingFragment;
import com.example.jukespot.spotifyjukespot.Enums.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;
import com.google.gson.Gson;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Metadata;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by Lino on 9/29/2017.
 */

public class MusicPlayer implements MusicPlayerInterface
        , CurrentQueueFragment.OnFragmentInteractionListener
        , CurrentlyPlayingFragment.OnFragmentInteractionListener
        , SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    private static final int MAX_PREV_QUEUE_SIZE = 15;
    private static final int MAX_CURR_QUEUE_SIZE = 50;
    private static final String TAG = MusicPlayer.class.getSimpleName();

    private MusicPlayerDelegate delegate;
    Logging log = new Logging();
    private SpotifyPlayer spotifyPlayer;
    private Metadata playerMetadata;
    private PlaybackState playerPlaybackState;
    private PlayerEvent currentEvent;
    private SimpleTrack currentTrackNotFromPlayer;
    private boolean isPaused;
    private static MusicPlayer instance = null;
    private User user;

    private List<SimpleTrack> currentQueue;

    /*Set max size of prev tracks saved*/
    private List<SimpleTrack> previousTrackQueue;

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
        user = User.getInstance();
        delegate = new MusicPlayerDelegate();

        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayerToImplement) {
                spotifyPlayer = spotifyPlayerToImplement;
                spotifyPlayer.addConnectionStateCallback(MusicPlayer.this);
                spotifyPlayer.addNotificationCallback(MusicPlayer.this);
                currentQueue = new ArrayList<SimpleTrack>();
                previousTrackQueue = new ArrayList<SimpleTrack>();
                //playerPlaybackState = spotifyPlayer.getPlaybackState();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });

    }
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }
    @Override
    public void play(SimpleTrack trackToPlay) {
        currentTrackNotFromPlayer = trackToPlay;
        if(user.getTypeOfUser() == UserType.CREATOR) {
            spotifyPlayer.playUri(null, trackToPlay.uri, 0, 0);
            setIsPaused(false);
        }else {
            log.logMessage(TAG, "SUBS CANNOT PLAY ON PHONE");
        }
        log.logMessage(TAG,"Song Currently Playing : " + trackToPlay.song_name);
    }

    @Override
    public void queue(SimpleTrack track){
        if(currentQueue.isEmpty()){
            queueAtPosition(0, track);
        }else{
            currentQueue.add(track);
        }
        printCurrentQueue();
    }

    public void addToPrevQueue(SimpleTrack trackToAdd){
        if(!doesPrevQueueHaveSpace())
            previousTrackQueue.remove(0);
        previousTrackQueue.add(trackToAdd);
        printCurrentQueue();
    }

    public void queueAtPosition(int position, SimpleTrack trackToQueue){
        currentQueue.add(position, trackToQueue);
        if(position == 0 ) {
            play(trackToQueue);
        }
        log.logMessage(TAG,"Add " + trackToQueue.song_name + " at Position : " + position);
        printCurrentQueue();
    }

    public void removeFromQueue(SimpleTrack toRemove){
        if(currentQueue.get(0).equals(toRemove) && !(currentQueue.size() <= 1)){
            play(currentQueue.get(1));
        }
        currentQueue.remove(toRemove);
        log.logMessage(TAG,"QUEUE AFTER REMOVAL:");
        printCurrentQueue();
    }
    public void removeFromPrevQueue(SimpleTrack toRemove){

        previousTrackQueue.remove(toRemove);
    }



    public List<SimpleTrack> getQueue(){return currentQueue;}

    public void printCurrentQueue(){
        if(currentQueue.isEmpty())
            return;

        log.logMessage(TAG,"Current Queue: ");
        for (SimpleTrack t : currentQueue ){
            log.logMessage(TAG, "track: " + t.song_name);
        }
    }

    public boolean doesCurrentQueueHaveSpace(){
        if (currentQueue.size() >= MAX_CURR_QUEUE_SIZE)
            return false;
        return true;
    }
    public boolean doesPrevQueueHaveSpace(){
        if (previousTrackQueue.isEmpty())
            return true;
        if (previousTrackQueue.size() >= MAX_PREV_QUEUE_SIZE)
            return false;
        return true;
    }
    /*Removes from the queue based on what is returned from the pubnub channel*/
    public void removeFromQueueFromService(SimpleTrack serviceTrack){
        List<Integer> indexOfRemovedSongs = new ArrayList<Integer>();
        int currentNdx = 0;
        for(SimpleTrack track : currentQueue){
            if(track.song_name.equals(serviceTrack.song_name) && track.artist.equals(serviceTrack.artist)){
                log.logMessage(TAG,"Track to remove : " + track.song_name);
                indexOfRemovedSongs.add(currentNdx);
            }
            currentNdx++;
        }

        for(int ndx : indexOfRemovedSongs){
            removeFromQueue(currentQueue.get(ndx));
        }
       // updateCurrentGUI();
    }


    @Override
    public void pause() {
       spotifyPlayer.pause(mOperationCallback);
       setIsPaused(true);
    }


    @Override
    public void next() {
        //spotifyPlayer.skipToNext(mOperationCallback);
        log.logMessage(TAG,"TRACK TO REMOVE: " + currentQueue.get(0).song_name);
        delegate.removeAndUpdate(currentQueue.get(0), ChangeType.REMOVE_FROM_SERVICE);
    }

    @Override
    public void prev() {
        int prevQueueNdx = previousTrackQueue.size() - 1;
        queueAtPosition(0, previousTrackQueue.get(prevQueueNdx));
        previousTrackQueue.remove(prevQueueNdx);
    }

    @Override
    public void resume() {
        spotifyPlayer.resume(mOperationCallback);
        setIsPaused(false);
    }

    @Override
    public boolean isPlaying() {
        if(playerPlaybackState == null){
            return false;
        }
        return playerPlaybackState.isPlaying;
    }
    public void setIsPaused(boolean isPaused){
        this.isPaused = isPaused;
    }

    public boolean getIsPaused(){
        return isPaused;
    }
    /* This will return the track in the format provided by the
     * SDK METADATA Which is as follows:
     *  https://spotify.github.io/android-sdk/player/com/spotify/sdk/android/player/Metadata.Track.html
     **/
    public List<SimpleTrack> getCurrentQueue(){
        return currentQueue;
    }

    public List<SimpleTrack> getPrevQueue(){
        return previousTrackQueue;
    }
    public SimpleTrack getCurrentTrackNotFromPlayer(){
        return currentTrackNotFromPlayer;
    }

    @Nullable
    @Override
    public Metadata.Track getCurrentTrack() {
        return playerMetadata.currentTrack;
    }
    @Nullable
    @Override
    public SimpleTrack getNextTrack() {
        return currentQueue.get(1);
    }
    @Nullable
    @Override
    public SimpleTrack getPrevTrack() {
        return previousTrackQueue.get(previousTrackQueue.size() - 1 );
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
        currentEvent = playerEvent;
        //check if song end naturally
        if(playerEvent.equals(PlayerEvent.kSpPlaybackNotifyAudioDeliveryDone)){
            log.logMessage(TAG,"Song Ended!");
            next();
        }
        if(playerEvent.equals(PlayerEvent.kSpPlaybackNotifyMetadataChanged)){
            updateCurrentGUI();
        }
        playerMetadata = spotifyPlayer.getMetadata();
        playerPlaybackState = spotifyPlayer.getPlaybackState();
        log.logMessage(TAG, "META : " + playerMetadata );
        log.logMessage(TAG,"Playback State : " + playerPlaybackState);

    }

    @Override
    public void onPlaybackError(Error error) {
        log.logErrorNoToast(TAG,"ERROR PLAYBACK :" + error);
    }

    public void updateCurrentGUI(){
        log.logMessage(TAG, "update gui from music player calls");
        delegate.updateGUI(ChangeType.UPDATE_GUI);

    }
    public void addObserverToDelegate(MainActivity activity){
        delegate.addObserver(activity);
    }


    public void removeObserverFragment(Fragment fragToObserve){
        delegate.deleteObserver((Observer)fragToObserve);
    }

}
