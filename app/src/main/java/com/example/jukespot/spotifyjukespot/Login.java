package com.example.jukespot.spotifyjukespot;


import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.WebServices.ServiceGatewayListener;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends Activity{
    private Button bRegLogin;
    private EditText edUsername;
    private EditText edPassword;
    private TextView newMemberText;

    private User user;
    private static final String TAG = Login.class.getSimpleName();
    private static final String loginUrl = "http://easel2.fulgentcorp.com:8081/";
    private Logging log;
    private ServicesGateway gateway;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = User.getInstance();
        gateway = ServicesGateway.getInstance();
        setContentView(R.layout.activity_login);
        initAllLayoutInteractions();
        log = new Logging();

        if(!runtime_permissions()){
//            Intent in = new Intent(this,LocationService.class);
//            startService(in);
            Intent i = new Intent(this,LocationIntentServices.class);
            startService(i);
        }

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
        String UserName = edUsername.getText().toString();
        String Password = edPassword.getText().toString();
        log.logMessage(TAG,UserName);
        log.logMessage(TAG,Password);
        final Context con = this;

        if(UserName.equals("") || Password.equals("")){
            log.logMessageWithToast(this ,TAG,"Username or Password is empty");

        }else{
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

                }
            });
            gateway.login(this,UserName,Password);
        }
    }

    public void onNewMemberClicked(View view){
        log.logMessage(TAG, "PRESSED NEW MEMBER!");
        startNewUser();
    }

    public void startJukeboxOptions(){
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

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Intent i = new Intent(this,LocationIntentServices.class);
                startService(i);
            }else {
                runtime_permissions();
            }
        }
    }

}
