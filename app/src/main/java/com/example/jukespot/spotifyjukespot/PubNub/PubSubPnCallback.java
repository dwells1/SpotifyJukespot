package com.example.jukespot.spotifyjukespot.PubNub;

/**
 * Created by rialt on 11/16/2017.
 */

import android.util.Log;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.Map;



public class PubSubPnCallback extends SubscribeCallback {
    private static final String TAG = PubSubPnCallback.class.getSimpleName();
    Logging log = new Logging();
    private Gson gson = new Gson();
    ///private final PubSubListAdapter pubSubListAdapter;

    // public PubSubPnCallback(PubSubListAdapter pubSubListAdapter) {
    //     this.pubSubListAdapter = pubSubListAdapter;
    // }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
      log.logMessage(TAG, "Callback Status : " + status.getCategory().toString());
        /*  switch (status.getCategory()) {
             // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
             case PNConnectedCategory:
             case PNUnexpectedDisconnectCategory:
             case PNReconnectedCategory:
             case PNDecryptionErrorCategory:
         }
        */

        // no status handling for simplicity
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {

        try {
             log.logMessage(TAG, message.toString());
             JsonArray msgJsonArray = message.getMessage().getAsJsonArray();
             String msgJsonStr =  gson.toJson(msgJsonArray.get(0));
             Map<String, String> jsonMap = gson.fromJson(msgJsonStr, new TypeToken<Map<String,String>>(){}.getType());

             //if message is to add song to queue we convert it to song and add it
             if(jsonMap.get("message_type").equals("add_song")){
                 SimpleTrack convertedTrack = convertToSimpleTrack(jsonMap);
                 MusicPlayer musicPlayer = MusicPlayer.getInstance();
                 musicPlayer.queue(convertedTrack);
                 musicPlayer.printCurrentQueue();
             }

        } catch (Exception e) {
            log.logMessage(TAG,"Message from PubNub Channel Failed to Convert into SimpleTrack");
            e.printStackTrace();
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        // no presence handling for simplicity
    }

    public SimpleTrack convertToSimpleTrack(Map<String, String> jsonMap){
        SimpleTrack converted;
        converted = new SimpleTrack(jsonMap.get("name"), jsonMap.get("artist"), jsonMap.get("uri"), jsonMap.get("albumImgLink"));
        log.logMessage(TAG,"TRACK CONVERTED:\nName: " +converted.song_name
                +"\nArtist: " + converted.artist
                +"\nURI: " + converted.uri
                +"\nAlbum Image Link: " + converted.album_image_link);
        return converted;
    }
}

