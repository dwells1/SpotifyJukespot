package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by nique on 10/3/2017.
 */

public class NewUser extends Activity {
    private static final String TAG = NewUser.class.getSimpleName();
    private static final String loginUrl = "http://easel2.fulgentcorp.com:8081/";

    private Button bNewUser;
    private EditText edNewLoginName;
    private EditText edNewPassword;
    private EditText edNewUserName;
    private EditText edNewEmail;
    private Logging log;

    private User user;

    //private Retrofit retrofit;
    private RetrofitClient rfit;
    private UserApiService client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new Logging();
        user = User.getInstance();
        setContentView(R.layout.activity_new_user);
        initFields();

        rfit = RetrofitClient.getInstance();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            Intent LoginIntent = new Intent(this, Login.class);
            startActivity(LoginIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void initFields(){
        edNewLoginName = (EditText) findViewById(R.id.edNewLoginName);
        edNewPassword = (EditText) findViewById(R.id.edNewPassword);
        edNewUserName = (EditText) findViewById(R.id.edNewUserName);
        edNewEmail = (EditText) findViewById(R.id.edNewEmail);
        bNewUser = (Button) findViewById(R.id.bNewUser);
    }

    public void onCreateNewUserClicked(View view){
        String newUser = edNewLoginName.getText().toString();
        String password = edNewPassword.getText().toString();
        String userName = edNewUserName.getText().toString();
        String email = edNewEmail.getText().toString();
        validateUser(newUser, password, userName, email);

    }

    private void validateUser(final String user, final String password, final String userName, final String email){
        if(user.equals("") || password.equals("")){
            log.logMessageWithToast(this ,TAG,"Username or Password is empty");
        }else{
            createNewUser(user,password,userName,email);
        }
    }

    private void createNewUser(final String userLogin, final String password, final String userName, final String email){
        final Context con = this;
        String newJson = buildJsonObject(userLogin,password,userName,email);
        client = rfit.getClient(getString(R.string.web_service_url)).create(UserApiService.class);
        Call<LoginResponse> call = client.addUser("jukespot",newJson);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.body().getResult().equals("ok")) {
                    log.logMessage(TAG,"Creation " +
                            response.body().getResult()+" "+
                            response.body().getMessage());
                    String loginJson = "{\"login\":\""+userLogin+"\",\"pw_hash\":\""+password+"\"}";
                    Call<LoginResponse> call2 = client.login("jukespot",loginJson);
                    call2.enqueue(new Callback<LoginResponse>() {
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
                }else{
                    log.logMessageWithToast(con ,TAG,"Creation of user failed");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

            }
        });
    }

    private String buildJsonObject(String user, String password, String userName, String email){

        String json = "{\"login\":\""+user+"\",\"pw_hash\":\""+password+"\"," +
                "\"user_name\":\""+userName+"\",\"email\":\""+email+"\"}";
        log.logMessage(TAG,json);
        return json;
    }

    private void startJukeboxOptions(){
        Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
        startActivity(jukeboxOptionsIntent);
        finish();
    }
}
