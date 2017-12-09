package com.example.jukespot.spotifyjukespot.MusicPlayer;

import com.example.jukespot.spotifyjukespot.Enums.ChangeType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;

import java.util.Observable;

/**
 * Created by linos on 12/6/2017.
 */

public class MusicPlayerDelegate extends Observable {
    Logging log = new Logging();
    private ServicesGateway gateway;
    private MusicPlayer player;

    public SimpleTrack trackChosen;

    public void removeAndUpdate(SimpleTrack track, ChangeType whatToDo){
        log.logMessage(MusicPlayerDelegate.class.getSimpleName(), "removeAndUpdate Called!");
        trackChosen = track;
        this.setChanged();
        this.notifyObservers(whatToDo);
    }

    public void updateGUI(ChangeType whatToDo){
        this.setChanged();
        this.notifyObservers(whatToDo);
    }

}
