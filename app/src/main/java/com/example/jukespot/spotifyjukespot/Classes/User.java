package com.example.jukespot.spotifyjukespot.Classes;

/**
 * Created by Alex on 9/20/2017.
 */

public class User {

    private static User instance = new User();
    private String userName = "";
    private String password = "";
    private String sessionToken;

    private User(){};

    public static User getInstance(){
        return instance;
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
}
