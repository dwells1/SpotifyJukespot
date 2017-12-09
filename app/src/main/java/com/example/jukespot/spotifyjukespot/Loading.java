package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jukespot.spotifyjukespot.Logging.Logging;

import java.util.ArrayList;

/**
 * Created by Dominique on 11/27/2017.
 */

public class Loading {
    private Context con;
    private Logging log;
    private static final String TAG = Loading.class.getSimpleName();
    ProgressBar progress;

    public Loading(Context con){
        log = new Logging();
        con = con;

        ViewGroup layout = (ViewGroup) ((Activity) con).findViewById(android.R.id.content).getRootView();

        progress = new ProgressBar(con, null, android.R.attr.progressBarStyleLarge);
        progress.setIndeterminate(true);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(con);

        rl.setGravity(Gravity.CENTER);
        rl.addView(progress);

        layout.addView(rl, params);

        progress.setVisibility(View.INVISIBLE);
    }

    public <T> void startLoading(Context con , ArrayList<TextView> nonVisibleText, ArrayList<Button> nonVisibleButton){
        this.con = con;
//        if(loading != null) {
//            loading.setVisibility(View.VISIBLE);
//            log.logMessage(TAG, "Loading set visible");
//        }
        progress.setVisibility(View.VISIBLE);
        if(nonVisibleText != null) {
            for (TextView item : nonVisibleText) {
                item.setEnabled(false);
            }
        }
        if(nonVisibleButton != null) {
            for (Button item : nonVisibleButton) {
                item.setEnabled(false);
            }
        }

    }

    public void finishLoading(Context con, ArrayList<TextView> nonVisibleText, ArrayList<Button> nonVisibleButton){
        this.con = con;
//        if(loading != null) {
//            loading.setVisibility(View.INVISIBLE);
//            log.logMessage(TAG, "Loading set invisible");
//        }
        progress.setVisibility(View.INVISIBLE);
        if(nonVisibleText != null) {
            for (TextView item : nonVisibleText) {
                item.setEnabled(true);
            }
        }
        if(nonVisibleButton != null) {
            for (Button item : nonVisibleButton) {
                item.setEnabled(true);
            }
        }
    }
}
