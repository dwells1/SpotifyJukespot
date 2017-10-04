package com.example.jukespot.spotifyjukespot.Classes;

/**
 * Created by Alex on 9/20/2017.
 */

public class User {
    String userName = "";
    String password = "";
    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
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
}
