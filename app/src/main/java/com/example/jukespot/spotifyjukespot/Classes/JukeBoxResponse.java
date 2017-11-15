package com.example.jukespot.spotifyjukespot.Classes;

import com.example.jukespot.spotifyjukespot.JoinJukebox;

/**
 * Created by Dominique on 11/5/2017.
 */

public class JukeBoxResponse {
    private Integer transaction_id;
    private Double latitude;
    private Double longitude;
    private String channel;
    private JukeBox location_fields;

    public JukeBoxResponse(Integer transaction_id,Double latitude, Double longitude, JukeBox location_fields,String channel){
        this.transaction_id = transaction_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location_fields = location_fields;
        this.channel = channel;
    }

    public Integer getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(Integer transaction_id) {
        this.transaction_id = transaction_id;
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

    public JukeBox getLocation_fields() {
        return location_fields;
    }

    public void setLocation_fields(JukeBox location_fields) {
        this.location_fields = location_fields;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
