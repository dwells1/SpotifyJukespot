package com.example.jukespot.spotifyjukespot.Classes;

/**
 * Created by Alex on 9/20/2017.
 */

public class JukeBox {
    private String playlist_name ="";
    private String password ="";
    private Boolean isQueueEditable ;
    private Boolean isPlayAutomatic ;
    private Double latitude;
    private Double longitude;
    private Double max_distance;
    private static Integer max_contributor = 50;

    public JukeBox(String playlist_name, String password, Double max_distance, Boolean isQueueEditable, Boolean isPlayAutomatic ){
        this.playlist_name = playlist_name;
        this.password = password;
        this.max_distance = max_distance;
        this.isQueueEditable = isQueueEditable;
        this.isPlayAutomatic = isPlayAutomatic;
    }
    public JukeBox(String playlist_name, Double max_distance, Boolean isQueueEditable, Boolean isPlayAutomatic ){
        this.playlist_name = playlist_name;
        this.max_distance = max_distance;
        this.isQueueEditable = isQueueEditable;
        this.isPlayAutomatic = isPlayAutomatic;
    }

    public Boolean hasPassword(){
        if(password.equals(""))
            return false;

        return true;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getQueueEditable() {
        return isQueueEditable;
    }

    public void setQueueEditable(Boolean queueEditable) {
        isQueueEditable = queueEditable;
    }

    public Boolean getPlayAutomatic() {
        return isPlayAutomatic;
    }

    public void setPlayAutomatic(Boolean playAutomatic) {
        isPlayAutomatic = playAutomatic;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getMax_distance() {
        return max_distance;
    }

    public void setMax_distance(Double max_distance) {
        this.max_distance = max_distance;
    }

    public static Integer getMax_contributor() {
        return max_contributor;
    }

    @Override
    public String toString(){
        return playlist_name;
    }
}