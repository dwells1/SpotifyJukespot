package com.example.jukespot.spotifyjukespot.Queue;

import com.google.firebase.appindexing.Action;
import com.spotify.sdk.android.player.Metadata;

/**
 * Created by linos on 10/4/2017.
 */

public class SongNode{
    private int id;
    private Metadata.Track track;
    SongNode(int id, Metadata.Track track){
        this.id = id;
        this.track = track;
    }


    public int getId(){
        return id;
    }

    public Metadata.Track getTrack(){
        return track;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setTrack(Metadata.Track track){
        this.track = track;
    }


}
