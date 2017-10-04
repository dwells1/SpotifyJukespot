package com.example.jukespot.spotifyjukespot.MusicPlayer;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.jukespot.spotifyjukespot.Logging.Logging;

/**
 * Created by Lino on 9/29/2017.
 */

public class MusicPlayerService extends Service {
    private static final String TAG = MusicPlayerService.class.getSimpleName();
    Logging log = new Logging();
    private final IBinder musicPlayerBinder = new PlayerBinder();
    private MusicPlayer musicPlayer = new MusicPlayer();


    public static Intent getIntent(Context context) {
        return new Intent(context, MusicPlayerService.class);
    }

    public class PlayerBinder extends Binder {
        public MusicPlayerInterface getService() {
            return musicPlayer;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicPlayerBinder;
    }

    @Override
    public void onDestroy() {
        musicPlayer.endCurrentPlayer();
        super.onDestroy();
    }
}
