package com.example.jukespot.spotifyjukespot;


import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;


public class Login extends Activity{
    private Button bRegLogin;
    private EditText edUsername;
    private EditText edPassword;
    private TextView newMemberText;

    private User user;
    private static final String TAG = Login.class.getSimpleName();
    private static final String loginUrl = "http://easel2.fulgentcorp.com:8081/";
    private Logging log;
    private RetrofitClient rfit;
    private UserApiService client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        setContentView(R.layout.activity_login);
        initAllLayoutInteractions();
        log = new Logging();

        rfit = RetrofitClient.getInstance();

    }

    public void initAllLayoutInteractions(){
        bRegLogin = findViewById(R.id.bRegLogin);
        edUsername = (EditText) findViewById(R.id.edRegUsername);
        edPassword = (EditText) findViewById(R.id.edRegPassword);
        bRegLogin = (Button) findViewById(R.id.bRegLogin);
        newMemberText = findViewById(R.id.newMemberText);

    }

    public void onRegLoginClicked(View view){
        log.logMessage(TAG,"LOGIN REG PRESSED");
        /*TODO: check username and password are in database*/
        String UserName = edUsername.getText().toString();
        String Password = edPassword.getText().toString();
        log.logMessage(TAG,UserName);
        log.logMessage(TAG,Password);
        final Context con = this;

        if(UserName.equals("") || Password.equals("")){
            log.logMessageWithToast(this ,TAG,"Username or Password is empty");

        }else{
            login(UserName,Password);

        }
    }

    public void login(final String userName, final String password){
        final Context con  = this;
        client = rfit.getClient(getString(R.string.web_service_url)).create(UserApiService.class);
        String json = "{\"login\":\""+userName+"\",\"pw_hash\":\""+password+"\"}";
        log.logMessage(TAG,json);
        Call<LoginResponse> call = client.login("jukespot",json);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.body().getResult().equals("ok")) {
                    log.logMessage(TAG,"login successful response is " +
                            response.body().getResult()+" "+
                            response.body().getUserSessionnToken());
                    user.setUserName(userName);
                    user.setPassword(password);
                    user.setSessionToken(response.body().getUserSessionnToken());
                    startJukeboxOptions();
                }else{
                    log.logMessageWithToast(con ,TAG,"Incorrect Username or Password");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                log.logMessage(TAG,"login failed");
            }
        });
    }

    public void onNewMemberClicked(View view){
        log.logMessage(TAG, "PRESSED NEW MEMBER!");
        startNewUser();
    }

    private void startJukeboxOptions(){
        Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
        startActivity(jukeboxOptionsIntent);
        finish();
    }

    private void startNewUser(){
        Intent newUserIntent = new Intent(this, NewUser.class);
        startActivity(newUserIntent);
        finish();
    }


}
