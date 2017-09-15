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
import android.widget.Spinner;

public class JukeboxCreationOptions extends AppCompatActivity {
    Spinner distanceDropdownSpinner;
    Button btnStartJukebox;
    /*TODO: initialize other inputs Jukebox Name, Password, Permission Checkboxes*/

    private static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox_creation_options);
        /*TODO: Store info from distance dropdown menu initiated here*/
        initDistanceSpinner();
        btnStartJukebox = (Button) findViewById(R.id.btnStartJukebox);

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

    public void onStartJukeboxClicked(View view){
        //take to main activity for now
        /*TODO: Store all the setting inputted by Creator and check that they are correct*/
        Intent intent = MainActivity.createIntent(this);
        //intent.putExtra(MainActivity.EXTRA_TOKEN, token);
        startActivity(intent);
        finish();
    }

    /*TODO: Maybe add something that tells the user what thing do?*/


}
