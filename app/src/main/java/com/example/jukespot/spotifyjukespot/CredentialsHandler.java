package com.example.jukespot.spotifyjukespot;

/**
 * Created by nique on 9/10/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.concurrent.TimeUnit;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

public class CredentialsHandler {

    private static final String ACCESS_TOKEN_NAME = "webapi.credentials.access_token";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String EXPIRES_AT = "expires_at";
    private static final String TAG = CredentialsHandler.class.getSimpleName();
    private static Logging log;

    public static void setToken(Context context, String token, long expiresIn, TimeUnit unit) {
        Context appContext = context.getApplicationContext();

        long now = System.currentTimeMillis();
        long expiresAt = now + unit.toMillis(expiresIn);

        SharedPreferences sharedPref = getSharedPreferences(appContext);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(ACCESS_TOKEN, token);
        editor.putLong(EXPIRES_AT, expiresAt);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context appContext) {
        return appContext.getSharedPreferences(ACCESS_TOKEN_NAME, Context.MODE_PRIVATE);
    }

    public static String getToken(Context context) {
        log = new Logging();
        Context appContext = context.getApplicationContext();
        SharedPreferences sharedPref = getSharedPreferences(appContext);

        String token = sharedPref.getString(ACCESS_TOKEN, null);
        long expiresAt = sharedPref.getLong(EXPIRES_AT, 0L);
        log.logMessage(TAG,"expires at " + Long.toString(expiresAt));

        if (token == null || expiresAt < System.currentTimeMillis()) {
            return null;
        }

        return token;
    }
}
