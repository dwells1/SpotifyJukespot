package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
public class JukeboxUserOptions extends Activity {
    Button btnCreateJukebox;
    Button btnJoinJukebox;
    Logging log;
    private static final String TAG = Login.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "4309049aaf574f63b61d3408408a4ff2";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "jukebox://callback";

    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log = new Logging();
        initJukeboxButtons();
        setContentView(R.layout.activity_jukebox_user_options);
    }

    public void initJukeboxButtons(){
        btnCreateJukebox = (Button) findViewById(R.id.bStartJukebox);
        btnJoinJukebox = (Button) findViewById(R.id.bJoinJukebox);
    }
    public void onStartNewJukeboxClicked(View view){
        Log.v(TAG,"START NEW PRESSED");
        String loginToken = CredentialsHandler.getToken(this);
        if(loginToken == null) {
            AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                    AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
            builder.setScopes(new String[]{"user-read-private", "streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        }else{
            //startMainActivity(loginToken);
            startCreatorJukeboxOptions(loginToken);
        }

    }

    public void onJoinJukeboxClicked(View view){
        /*TODO: add functionality for joining a jukebox button at the moment it does nothing*/
        Log.v(TAG,"JOIN JUKE PRESSED");
    }// end onJoinJukeboxClicked

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    logMessage("Got token: " + response.getAccessToken());
                    CredentialsHandler.setToken(this, response.getAccessToken(), response.getExpiresIn(), TimeUnit.SECONDS);
                    //startMainActivity(response.getAccessToken());
                    startCreatorJukeboxOptions(response.getAccessToken());
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }
    } //end onActivity

    private void startMainActivity(String token) {
        Intent intent = MainActivity.createIntent(this);
       // intent.putExtra("EXTRA_TOKEN", token);
        startActivity(intent);
        finish();
    }//end startMain

    private void startCreatorJukeboxOptions(String token){
        Intent jukeboxCreatorOptionsIntent = new Intent(this,
                JukeboxCreationOptions.class);
        jukeboxCreatorOptionsIntent.putExtra("EXTRA_TOKEN", token);
        startActivity(jukeboxCreatorOptionsIntent);
        finish();
    }
    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

}// end class
