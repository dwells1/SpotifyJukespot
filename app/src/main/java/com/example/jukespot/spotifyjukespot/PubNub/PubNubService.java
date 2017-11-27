package com.example.jukespot.spotifyjukespot.PubNub;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MainActivity;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;

import java.util.Arrays;

/**
 * Created by linos on 11/20/2017.
 */

public class PubNubService extends IntentService {

    private static final String TAG = PubNubService.class.getSimpleName();
    Logging log = new Logging();
    public PubNubService() {
        super("PubNubService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        log.logMessage(TAG, "Inside onHandleIntent");
        String channel = intent.getDataString();
        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(PubNubConstants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(PubNubConstants.PUBNUB_SUBSCRIBE_KEY);

        PubNub pubnub = new PubNub(config);
        PubSubPnCallback callback = new PubSubPnCallback();
        pubnub.addListener(callback);
        pubnub.subscribe().channels(Arrays.asList(channel)).withPresence().execute(); //subscribe to a channel
        log.logMessage(TAG, "End of subscription");
    }
}
