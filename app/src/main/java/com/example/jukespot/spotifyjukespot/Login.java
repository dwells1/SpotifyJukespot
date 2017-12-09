package com.example.jukespot.spotifyjukespot;


import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.WebServices.ServiceGatewayListener;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;

import android.*;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{
    private Button bRegLogin;
    private EditText edUsername;
    private EditText edPassword;
    private TextView newMemberText;
    private ProgressBar loading;

    private User user;
    private static final String TAG = Login.class.getSimpleName();
    private Logging log;
    private ServicesGateway gateway;
    private Loading load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        gateway = ServicesGateway.getInstance();
        setContentView(R.layout.activity_login);
        initAllLayoutInteractions();
        log = new Logging();
        load = new Loading(this);

    }

    public void initAllLayoutInteractions(){
        bRegLogin = (Button)findViewById(R.id.bRegLogin);
        edUsername = (EditText) findViewById(R.id.edRegUsername);
        edPassword = (EditText) findViewById(R.id.edRegPassword);
        bRegLogin = (Button) findViewById(R.id.bRegLogin);
        newMemberText = (TextView)findViewById(R.id.newMemberText);
        loading = (ProgressBar)findViewById(R.id.loading);
    }

    public void onRegLoginClicked(View view){
//        if(!runtime_permissions()){
//            Intent i = new Intent(this,LocationIntentServices.class);
//            startService(i);
//        }
        runtime_permissions();
        log.logMessage(TAG,"LOGIN REG PRESSED");
        String UserName = edUsername.getText().toString();
        String Password = edPassword.getText().toString();
        log.logMessage(TAG,UserName);
        log.logMessage(TAG,Password);
        final Context con = this;

        if(UserName.equals("") || Password.equals("")){
            log.logMessageWithToast(this ,TAG,"Username or Password is empty");

        }else{
            setVisibility("LOADING");
            gateway.setListener(new ServiceGatewayListener() {
                @Override
                public void onSuccess() {
                    startJukeboxOptions();
                }
                @Override
                public void gotPlaylists(List<JukeBoxResponse> jukeboxes){

                }
                @Override
                public  void onError(){
                    setVisibility("");
                }
            });
            gateway.login(this,UserName,Password);
        }
    }

    public void onNewMemberClicked(View view){
        log.logMessage(TAG, "PRESSED NEW MEMBER!");
        startNewUser();
    }

    private void setVisibility(String visibility){
        if(visibility.equals("LOADING")){
            ArrayList text = new ArrayList<TextView>();
            ArrayList buttons = new ArrayList<Button>();
            text.add(edUsername);
            text.add(edPassword);
            buttons.add(bRegLogin);
            log.logMessage(TAG,"buttons " + findViewById(R.id.bRegLogin));
            load.startLoading(this,text,buttons);
        }
        else{
            ArrayList text = new ArrayList<TextView>();
            ArrayList buttons = new ArrayList<Button>();
            text.add(edUsername);
            text.add(edPassword);
            buttons.add(bRegLogin);
            load.finishLoading(this,text,buttons);
        }
    }

    public void startJukeboxOptions(){
        loading.setVisibility(View.GONE);
        Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
        startActivity(jukeboxOptionsIntent);
        finish();
    }

    private void startNewUser(){
        Intent newUserIntent = new Intent(this, NewUser.class);
        startActivity(newUserIntent);
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
//        Intent i =new Intent(getApplicationContext(),LocationService.class);
//        stopService(i);
//        if(broadcastReceiver != null){
//            unregisterReceiver(broadcastReceiver);
//        }
    }

    private void runtime_permissions() {
//        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
//
//            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    100);
//            return true;
//        }
//        return false;
        // Here, thisActivity is the current activity
        log.logMessage(TAG,"*****PERMISSIONS*****");
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        100);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent(this,LocationIntentServices.class);
                startService(i);
            }else {
                runtime_permissions();
            }
        }
    }

}
