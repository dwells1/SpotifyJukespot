package com.example.jukespot.spotifyjukespot;

import com.example.jukespot.spotifyjukespot.Classes.JukeBox;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Classes.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class JukeboxCreationOptions extends AppCompatActivity {
    private Logging log;

    Spinner distanceDropdownSpinner;
    CheckBox chkPlayAutomatic;
    CheckBox chkEditQueue;
    EditText edJukeboxName;
    EditText edJukeboxPassword;
    Button btnStartJukebox;

    JukeBox jukeBox;
    String jukeName;
    String jukePassword;
    String jukeDistance;
    Boolean isQueueEditable;
    Boolean isPlayAutomatic;

    String accessToken;
    private User user;

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
        String [] distanceOptions = new String[] {"20ft","30ft","40ft"};
        ArrayAdapter<String> distanceSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, distanceOptions);
        distanceDropdownSpinner.setAdapter(distanceSpinnerAdapter);
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
                    log.logMessage(TAG, "Allow Subscrivers to edit Queue True");
                }else{
                    isQueueEditable = false;
                    log.logMessage(TAG, "Allow Subscrivers to edit Queue False");
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
         jukeDistance = distanceDropdownSpinner.getSelectedItem().toString();

        if(jukePassword.equals("")){
            jukeBox = new JukeBox(jukeName,jukeDistance, isQueueEditable, isPlayAutomatic);
        }else{
            jukeBox = new JukeBox(jukeName,jukePassword,jukeDistance, isQueueEditable, isPlayAutomatic);
        }

        if(jukeName.equals("")){
            logMessage("No name inputted");
        }else{
            Intent intent = MainActivity.createIntent(this);
            intent.putExtra(EXTRA_TOKEN, accessToken);
            startActivity(intent);
            finish();
        }
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    /*TODO: Maybe add something that tells the user what things do?*/


}
