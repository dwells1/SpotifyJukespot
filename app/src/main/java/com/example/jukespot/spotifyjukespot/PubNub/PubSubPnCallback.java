package com.example.jukespot.spotifyjukespot.PubNub;

/**
 * Created by rialt on 11/16/2017.
 */

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
        SimpleTrack convertedTrack;
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        String msgForException = "getting message";
        try {
             log.logMessage(TAG, message.toString());
             JsonArray msgJsonArray = message.getMessage().getAsJsonArray();
             String msgJsonStr =  gson.toJson(msgJsonArray.get(0));
             Map<String, String> jsonMap = gson.fromJson(msgJsonStr, new TypeToken<Map<String,String>>(){}.getType());
             msgForException =  jsonMap.get("message_type");
             //if message is to add song to queue we convert it to song and add it
             if(jsonMap.get("message_type").equals("add_song")){
                 convertedTrack = convertToSimpleTrack(jsonMap);
                 log.logMessage(TAG,"Add song " +convertedTrack.song_name+ " Current queue before");
                 for(SimpleTrack s : musicPlayer.getCurrentQueue()){
                     log.logMessage(TAG,s.song_name);
                 }
                 musicPlayer.queue(convertedTrack);
                 //musicPlayer.printCurrentQueue();
             }else if (jsonMap.get("message_type").equals("play_song")){
                 convertedTrack = convertToSimpleTrack(jsonMap);
                 musicPlayer.queueAtPosition(0,convertedTrack);
                 musicPlayer.printCurrentQueue();
             }else if(jsonMap.get("message_type").equals("remove_song")){
                 convertedTrack = convertToSimpleTrack(jsonMap);
                 musicPlayer.removeFromQueueFromService(convertedTrack);
             }



        } catch (Exception e) {
            log.logMessage(TAG,"Message from PubNub Channel Failed to " + msgForException);
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

