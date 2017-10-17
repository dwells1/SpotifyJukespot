package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
public class JukeboxUserOptions extends Activity {
    private Button btnCreateJukebox;
    private Button btnJoinJukebox;
    private User user;
    Logging log;
    private static final String TAG = JukeboxUserOptions.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "4309049aaf574f63b61d3408408a4ff2";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "jukebox://callback";

    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new Logging();
        user= User.getInstance();

        initJukeboxButtons();
        setContentView(R.layout.activity_jukebox_user_options);
    }

    public void initJukeboxButtons(){
        btnCreateJukebox = (Button) findViewById(R.id.bStartJukebox);
        btnJoinJukebox = (Button) findViewById(R.id.bJoinJukebox);
    }

    public void onStartNewJukeboxClicked(View view){
        log.logMessage(TAG,"START NEW PRESSED");
        openSpotifyLogin("CREATE");
    }


    public void onJoinJukeboxClicked(View view){
        log.logMessage(TAG,"JOIN JUKE PRESSED");
        openSpotifyLogin("JOIN");

    }// end onJoinJukeboxClicked

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
            //startMainActivity(loginToken);
            if(pressed.equals("CREATE")){
                user.setTypeOfUser("Creator");
                startCreatorJukeboxOptions(loginToken);
            }else{
                user.setTypeOfUser("Subscriber");
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
                // Response was successful and contains auth token
                case TOKEN:
                    log.logMessageWithToast(this,TAG,"Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    startCreatorJukeboxOptions(response.getAccessToken());
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
        Intent jukeboxSubscriberOptionsIntent = new Intent(this,
                JoinJukebox.class);
        jukeboxSubscriberOptionsIntent.putExtra("EXTRA_TOKEN", token);
        startActivity(jukeboxSubscriberOptionsIntent);
        finish();
    }

}// end class
