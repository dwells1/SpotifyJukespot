package com.example.jukespot.spotifyjukespot.MusicPlayer;

/**
 * Created by linos on 10/27/2017.
 */

public class SimpleTrack {
    public String song_name;
    public String artist;
    public String uri;
    public String album_image_link;

    public SimpleTrack(String trackName, String trackArtist
            , String trackUri, String album_image_link) {
        this.song_name = trackName;
        this.artist = trackArtist;
        this.uri = trackUri;
        this.album_image_link = album_image_link;
    }

    public void setTrackArtist(String trackArtist) {
        this.artist = trackArtist;
    }

    public void setTrackUri(String trackUri) {
        this.uri = trackUri;
    }

    public void setAlbum_image_link(String album_image_link) {
        this.album_image_link = album_image_link;
    }

    public void setTrackName(String trackName) {
        this.song_name = trackName;
    }
}

