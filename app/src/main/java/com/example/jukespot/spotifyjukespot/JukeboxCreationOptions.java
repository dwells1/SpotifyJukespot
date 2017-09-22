package com.example.jukespot.spotifyjukespot;

import android.content.Context;
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
    Spinner distanceDropdownSpinner;
    CheckBox chkPlayAutomatic;
    CheckBox chkEditQueue;
    EditText edJukeboxName;
    EditText edJukeboxPassword;
    Button btnStartJukebox;
    /*TODO: initialize other inputs Jukebox Name, Password, Permission Checkboxes*/
    JukeBox jukeBox;
    String jukeName;
    String jukePassword;
    String jukeDistance;
    Boolean isQueueEditable;
    Boolean isPlayAutomatic;



    private static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox_creation_options);
        /*TODO: Store info from distance dropdown menu initiated here*/

        initDistanceSpinner();
        initLayoutValues();

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
            //intent.putExtra(MainActivity.EXTRA_TOKEN, token);
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
