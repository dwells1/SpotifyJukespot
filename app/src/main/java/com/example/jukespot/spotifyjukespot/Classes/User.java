package com.example.jukespot.spotifyjukespot.Classes;

import com.example.jukespot.spotifyjukespot.Enums.UserType;

/**
 * Created by Alex on 9/20/2017.
 */

public class User {

    private static User instance = null;
    private String userName = "";
    private String password = "";
    private String sessionToken;
    private UserType typeOfUser;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    //make a type of user creator or subscriber
    private User() {
        sessionToken = null;
        typeOfUser = null;
    }

    public static User getInstance(){
        if(instance == null){
            instance = new User();

        }
        return instance;
    }

    public UserType getTypeOfUser() {
        return typeOfUser;
    }

    public void setTypeOfUser(UserType typeOfUser) {
        this.typeOfUser = typeOfUser;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getPassword() {

        return password;
    }

    public String getUserName() {

        return userName;
    }
    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
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
}
