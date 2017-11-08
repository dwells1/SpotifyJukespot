package com.example.jukespot.spotifyjukespot.Classes;

/**
 * Created by Dominique on 10/2/2017.
 */

public class LoginResponse {
    private String result;
    private String session_token;
    private String message;

    public LoginResponse(String result, String userSessionToken,String message){
        this.result = result;
        this.session_token = userSessionToken;
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUserSessionnToken() {
        return session_token;
    }

    public void setUserSessionnToken(String userSessionnToken) {
        this.session_token = userSessionnToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
