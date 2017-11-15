package com.example.jukespot.spotifyjukespot;

import com.example.jukespot.spotifyjukespot.Enums.Discoverable;
import com.example.jukespot.spotifyjukespot.Classes.JukeBox;
import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.Classes.LoginResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.jukespot.spotifyjukespot.WebServices.RetrofitClient;
import com.example.jukespot.spotifyjukespot.WebServices.ServiceGatewayListener;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;
import com.example.jukespot.spotifyjukespot.WebServices.UserApiService;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JukeboxCreationOptions extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Logging log;

    Spinner distanceDropdownSpinner;
    CheckBox chkPlayAutomatic, chkEditQueue;
    EditText edJukeboxName,edJukeboxPassword;
    Button btnStartJukebox;

    JukeBox jukeBox;
    String jukeName, jukePassword;
    Double jukeDistance;
    Boolean isQueueEditable, isPlayAutomatic;

    String accessToken;
    private User user;
    private RetrofitClient rfit;
    private UserApiService client;
    private ServicesGateway gateway;

    private static final String TAG = JukeboxCreationOptions.class.getSimpleName();
    static final String EXTRA_TOKEN = "EXTRA_TOKEN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox_creation_options);
        Intent intent = this.getIntent();
        accessToken = intent.getStringExtra(EXTRA_TOKEN);
        initDistanceSpinner();
        initLayoutValues();
        log = new Logging();
        user = User.getInstance();
        user.setTypeOfUser(UserType.CREATOR);

        rfit = RetrofitClient.getInstance();
        gateway = ServicesGateway.getInstance();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            Intent jukeboxUserOptionsIntent = new Intent(this,
                    JukeboxUserOptions.class);
            startActivity(jukeboxUserOptionsIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initDistanceSpinner(){
        distanceDropdownSpinner = (Spinner) findViewById(R.id.spinnerDistanceOptions);
        Integer [] distanceOptions = new Integer[] {30,50,80,100};
        ArrayAdapter<Integer> distanceSpinnerAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, distanceOptions);
        distanceDropdownSpinner.setAdapter(distanceSpinnerAdapter);
        distanceDropdownSpinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
        log.logMessage(TAG,"max distance is " + parent.getItemAtPosition(pos).toString());
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void initLayoutValues(){
        btnStartJukebox = (Button) findViewById(R.id.btnStartJukebox);
        chkPlayAutomatic = (CheckBox) findViewById(R.id.chkPlayAutomatic);
        chkEditQueue = (CheckBox)findViewById(R.id.chkEditQueue);
        edJukeboxName = (EditText)findViewById(R.id.edJukeboxName);
        edJukeboxPassword = (EditText)findViewById(R.id.edJukeboxPassword);
    }

    public void onCheckBoxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.chkEditQueue:
                if (checked){
                    isQueueEditable = true;
                    log.logMessage(TAG, "Allow Subscribers to edit Queue True");
                }else{
                    isQueueEditable = false;
                    log.logMessage(TAG, "Allow Subscribers to edit Queue False");
                }
                break;
            case R.id.chkPlayAutomatic:
                if (checked){
                    isPlayAutomatic = true;
                    log.logMessage(TAG, "Play automaticly True");
                }else{
                    isPlayAutomatic = false;
                    log.logMessage(TAG, "Play automaticly False");
                };
                break;
        }
    }

    public void onStartJukeboxClicked(View view){
        //take to main activity for now
        /*TODO: Store all the setting inputted by Creator and check that they are correct*/
         if (chkEditQueue.isChecked()){
             isQueueEditable = true;
         }else {
             isQueueEditable = false;
         }
         if(chkPlayAutomatic.isChecked()){

             isPlayAutomatic = true;
             log.logMessage(TAG, "The value  is automatic"+ isPlayAutomatic);
         }else{
             isPlayAutomatic = false;
         }
         jukeName = edJukeboxName.getText().toString();
         jukePassword = edJukeboxPassword.getText().toString();
         jukeDistance = Double.parseDouble(distanceDropdownSpinner.getSelectedItem().toString()) / 5280;

        if(jukePassword.equals("")){
            jukeBox = new JukeBox(jukeName,jukeDistance, isQueueEditable, isPlayAutomatic);
        }else{
            jukeBox = new JukeBox(jukeName,jukePassword,jukeDistance, isQueueEditable, isPlayAutomatic);
        }

        if(jukeName.equals("")){
            log.logMessage(TAG,"No song_name inputted");
        }else{
            jukeBox.setLongitude(user.getLongitude());
            jukeBox.setLatitude(user.getLatitude());
            gateway.setListener(new ServiceGatewayListener() {
                @Override
                public void onSuccess() {
                    startJukeBox();
                }
                @Override
                public void gotPlaylists(List<JukeBoxResponse> jukeboxes){

                }
                @Override
                public  void onError(){

                }
            });
            /*TODO: Right now only proceeds if the user's jukespot is not set to discoverable, so if discoverable is to Y before executing set discoverable nothing happens
             */
            gateway.setDiscoverable(this, Discoverable.Y);

        }
    }

    private void startJukeBox(){
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {
                startMainActivity();
            }
            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes){

            }
            @Override
            public  void onError(){

            }
        });
        gateway.modifyPlaylistParameters(this,jukeBox);
    }

    private void startMainActivity(){
        Intent intent = MainActivity.createIntent(this);
        intent.putExtra(EXTRA_TOKEN, accessToken);
        intent.putExtra("TRANSACTION_ID",-1);
        intent.putExtra("CHANNEL","");
        startActivity(intent);
        finish();
    }

    /*TODO: Maybe add something that tells the user what things do?*/


}
