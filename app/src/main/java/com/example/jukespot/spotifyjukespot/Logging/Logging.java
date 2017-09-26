package com.example.jukespot.spotifyjukespot.Logging;

import android.util.Log;

public class Logging {
    public Logging(){

    }

    public void logMessage(String tag,String msg) {
        //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(tag, msg);
    }
}
