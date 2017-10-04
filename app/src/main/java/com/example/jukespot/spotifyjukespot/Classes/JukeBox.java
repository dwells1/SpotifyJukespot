package com.example.jukespot.spotifyjukespot.Classes;

/**
 * Created by Alex on 9/20/2017.
 */

public class JukeBox {
    String userName ="";
    String password ="";
    String distance ="";
    Boolean isQueueEditable ;
    Boolean isPlayAutomatic ;


    public JukeBox(String userName, String password, String distance, Boolean isQueueEditable, Boolean isPlayAutomatic ){
        this.userName = userName;
        this.password = password;
        this.distance = distance;
        this.isQueueEditable = isQueueEditable;
        this.isPlayAutomatic = isPlayAutomatic;
    }
    public JukeBox(String userName,  String distance, Boolean isQueueEditable, Boolean isPlayAutomatic ){
        this.userName = userName;
        this.distance = distance;
        this.isQueueEditable = isQueueEditable;
        this.isPlayAutomatic = isPlayAutomatic;
    }

    public Boolean hasPassword(){
        if(password.equals(""))
            return false;

        return true;
    }


}