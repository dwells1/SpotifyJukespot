package com.example.jukespot.spotifyjukespot.PubNub;

/**
 * Created by rialt on 11/16/2017.
 */

import android.util.Log;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.fasterxml.jackson.databind.JsonNode;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;


public class PubSubPnCallback extends SubscribeCallback {
    private static final String TAG = PubSubPnCallback.class.getName();
    Logging log = new Logging();
    ///private final PubSubListAdapter pubSubListAdapter;

    // public PubSubPnCallback(PubSubListAdapter pubSubListAdapter) {
    //     this.pubSubListAdapter = pubSubListAdapter;
    // }

    @Override
    public void status(PubNub pubnub, PNStatus status) {
        /*
        switch (status.getCategory()) {
             // for common cases to handle, see: https://www.pubnub.com/docs/java/pubnub-java-sdk-v4
             case PNStatusCategory.PNConnectedCategory:
             case PNStatusCategory.PNUnexpectedDisconnectCategory:
             case PNStatusCategory.PNReconnectedCategory:
             case PNStatusCategory.PNDecryptionErrorCategory:
         }
        */

        // no status handling for simplicity
    }

    @Override
    public void message(PubNub pubnub, PNMessageResult message) {
        try {
             log.logMessage(TAG, message.toString());

            //JsonNode jsonMsg = message.getMessage();
            //PubSubPojo dsMsg = JsonUtil.convert(jsonMsg, PubSubPojo.class);

            //this.pubSubListAdapter.add(dsMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void presence(PubNub pubnub, PNPresenceEventResult presence) {
        // no presence handling for simplicity
    }
}

