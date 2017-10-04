package com.example.jukespot.spotifyjukespot.CurrentlyPlaying;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.jukespot.spotifyjukespot.Logging.Logging;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Lino on 10/3/2017.
 */

public class DownloadCoverImage extends AsyncTask<String,String,Bitmap>{
    private static final String TAG = DownloadCoverImage.class.getSimpleName();
    Logging log = new Logging();

    @Override
    protected Bitmap doInBackground(String... strings) {
        String url = strings[0];
        URL coverImgUrl;
        try{
            coverImgUrl = new URL(url);
            InputStream in = coverImgUrl.openStream();
            Bitmap img = BitmapFactory.decodeStream(in);
            return img;
        }catch(MalformedURLException e){
            log.logErrorNoToast(TAG,"Not A valid URL For Album Cover Image");
        }catch(IOException e){
            log.logErrorNoToast(TAG,"Not A valid Connection For Album Cover Image");
        }

        return null;
    }
}
