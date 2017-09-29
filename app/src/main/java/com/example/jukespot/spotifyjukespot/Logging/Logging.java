package com.example.jukespot.spotifyjukespot.Logging;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Logging {
    public Logging(){

    }

    public void logMessage(String tag,String msg) {
        //Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(tag, msg);
    }

    public void logMessageWithToast(Context mContext, String tag, String msg){
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(tag,msg);
    }
}
