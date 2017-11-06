package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.WebServices.RetrofitClient;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;
import com.example.jukespot.spotifyjukespot.WebServices.UserApiService;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JukeboxUserOptions extends Activity {
    private Button btnCreateJukebox;
    private Button btnJoinJukebox;
    private Button btnLogoutJukebox;
    private User user;
    boolean isConfirmed;
    private Intent i;
    Logging log;
    private static final String TAG = JukeboxUserOptions.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "4309049aaf574f63b61d3408408a4ff2";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "jukebox://callback";

    private static final int REQUEST_CODE = 1337;

    private RetrofitClient rfit;
    private UserApiService client;
    private ServicesGateway gateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new Logging();
        user= User.getInstance();
        gateway = ServicesGateway.getInstance();

        rfit = RetrofitClient.getInstance();

        initJukeboxButtons();
        setContentView(R.layout.activity_jukebox_user_options);

        if(!runtime_permissions()){
            i = new Intent(this,LocationIntentServices.class);
            startService(i);
        }
        gateway.updateUser(this);

        getUser(user.getSessionToken());
    }

    public void initJukeboxButtons(){
        btnCreateJukebox = (Button) findViewById(R.id.bStartJukebox);
        btnJoinJukebox = (Button) findViewById(R.id.bJoinJukebox);
        btnLogoutJukebox = (Button) findViewById(R.id.bLogout);
    }

    public void getUser(final String token){
        final Context con  = this;
        client = rfit.getClient(getString(R.string.web_service_url)).create(UserApiService.class);
        Call<ResponseBody> call = client.getUser(token);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response!=null) {
                    log.logMessage(TAG,"User info is " +
                            response.raw());
                }else{
                    log.logMessageWithToast(con ,TAG,"Incorrect session token");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                log.logMessage(TAG,"login failed");
            }
        });
    }

    public void onStartNewJukeboxClicked(View view){
        log.logMessage(TAG,"START NEW PRESSED");
        openSpotifyLogin("CREATE");
    }

    public void onJoinJukeboxClicked(View view){
        log.logMessage(TAG,"JOIN JUKE PRESSED");
        openSpotifyLogin("JOIN");

    }

    public void onLogoutJukeboxClicked(View view){
        log.logMessage(TAG,"LOGOUT JUKE PRESSED");
        createAlert("Are you sure you want to logout?");
    }

    public void createAlert(final String message){

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage(message);
        alertDlg.setCancelable(false);
        alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                isConfirmed = true;
                log.logMessage(TAG, "pressed yes to logout");
                if(message.equals("Are you sure you want to logout?")){

                    Toast.makeText(getApplicationContext(), "Successfully logout", Toast.LENGTH_SHORT).show();

                    Intent jukeboxLoginIntent = new Intent(getApplicationContext(), Login.class);
                    startActivity(jukeboxLoginIntent);
                    finish();
                }

            }
        });
        alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isConfirmed = false;
            }
        });
        alertDlg.create().show();

    }

    public void openSpotifyLogin(String pressed){
        String loginToken = CredentialsHandler.getToken(this);
        if(loginToken == null) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            /**
             * TODO: check scopes necessary at : https://developer.spotify.com/web-api/using-scopes/
             */
            builder.setScopes(new String[]{
                    "user-read-private",
                    "streaming",
                    "user-read-currently-playing"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }else{
            if(pressed.equals("CREATE")){
                user.setTypeOfUser(UserType.CREATOR);
                startCreatorJukeboxOptions(loginToken);
            }else{
                user.setTypeOfUser(UserType.SUBSCRIBER);
                startJoinJukeboxOptions(loginToken);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // JukeResponse was successful and contains auth token
                case TOKEN:
                    log.logMessageWithToast(this,TAG,"Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);

                    if(user.getTypeOfUser() == UserType.CREATOR)
                        startCreatorJukeboxOptions(response.getAccessToken());
                    else
                        startJoinJukeboxOptions(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    log.logError(this,TAG,"Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    log.logError(this,TAG,"Auth result: " + response.getType());
            }
        }
    } //end onActivity

    private void startCreatorJukeboxOptions(String token){
        Intent jukeboxCreatorOptionsIntent = new Intent(this,
                JukeboxCreationOptions.class);
        jukeboxCreatorOptionsIntent.putExtra("EXTRA_TOKEN", token);
        startActivity(jukeboxCreatorOptionsIntent);
        finish();
    }

    private void startJoinJukeboxOptions(String token){
        startService(i);
        Intent jukeboxSubscriberOptionsIntent = new Intent(this,
                JoinJukebox.class);
        jukeboxSubscriberOptionsIntent.putExtra("EXTRA_TOKEN", token);
        startActivity(jukeboxSubscriberOptionsIntent);
        finish();
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

}// end class
