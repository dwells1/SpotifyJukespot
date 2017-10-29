package com.example.jukespot.spotifyjukespot.MusicPlayer;

/**
 * Created by linos on 10/27/2017.
 */

public class SimpleTrack {
    public String name;
    public String artist;
    public String uri;
    public String albumImgLink;

    public SimpleTrack(String trackName, String trackArtist
            , String trackUri, String albumImgLink) {
        this.name = trackName;
        this.artist = trackArtist;
        this.uri = trackUri;
        this.albumImgLink = albumImgLink;
    }

    public void setTrackArtist(String trackArtist) {
        this.artist = trackArtist;
    }

    public void setTrackUri(String trackUri) {
        this.uri = trackUri;
    }

    public void setAlbumImgLink(String albumImgLink) {
        this.albumImgLink = albumImgLink;
    }


    public void setTrackName(String trackName) {
        this.name = trackName;
    }
}

