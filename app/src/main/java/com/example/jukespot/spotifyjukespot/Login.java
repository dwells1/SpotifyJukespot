package com.example.jukespot.spotifyjukespot;

/**
 * This will be the regular Login for the App itself and
 * not the Spotify Authentication even though as of now it is
 * still in there....
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

import java.util.concurrent.TimeUnit;

import com.google.firebase.database.ThrowOnExtraProperties;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class Login extends Activity{
    Button bRegLogin;
    EditText edUsername;
    User user;
    EditText edPassword;
    private static final String TAG = Login.class.getSimpleName();
    private Logging log;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "4309049aaf574f63b61d3408408a4ff2";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "jukebox://callback";

    private static final int REQUEST_CODE = 1337;




    // Validate txt1String
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TODO: initialize EditText field for username and password and check if inputs are correct*/
        bRegLogin = findViewById(R.id.bRegLogin);
        setContentView(R.layout.activity_login);
        String token = CredentialsHandler.getToken(this);
        log = new Logging();
      /*  if (token == null) {
            setContentView(R.layout.activity_login);
        } else {
            startMainActivity(token);
        }*/
        edUsername = (EditText) findViewById(R.id.edRegUsername);
        edPassword = (EditText) findViewById(R.id.edRegPassword);
        bRegLogin = (Button) findViewById(R.id.bRegLogin);

    }


    public void onLoginButtonClicked(View view) {
        log.logMessage(TAG,"login clicked");
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }


    public void onRegLoginClicked(View view){
        log.logMessage(TAG,"LOGIN REG PRESSED");
        /*TODO: check username and password are in database*/
        String UserName = edUsername.getText().toString();
        String Password = edPassword.getText().toString();

        if(UserName.equals("") || Password.equals("")){
            log.logMessageWithToast(this ,TAG,"Username or Password is empty");

        }else{
            /*Assume Password and Username are Correct and go to next instance*/
            log.logMessage(TAG,"Login Successful");
            user = new User(UserName, Password);

            startJukeboxOptions();
        }



    }
    /*TODO: Remove all mentions of original Spotify Login*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    log.logMessage(TAG,"Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    startMainActivity(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    log.logMessage(TAG,"Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    log.logMessage(TAG,"Auth result: " + response.getType());
            }
        }
    }

    private void startMainActivity(String token) {
        Intent intent = MainActivity.createIntent(this);
        intent.putExtra("EXTRA_TOKEN", token);
        startActivity(intent);
        finish();
    }
    private void startJukeboxOptions(){
        Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
        startActivity(jukeboxOptionsIntent);
        finish();

    }


}
