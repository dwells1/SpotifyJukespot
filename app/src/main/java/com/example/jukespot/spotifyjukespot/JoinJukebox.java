package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Classes.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

import java.util.ArrayList;
import java.util.List;

import static com.example.jukespot.spotifyjukespot.R.id.bJoinJukebox2;
import static com.example.jukespot.spotifyjukespot.R.id.list_of_jukeboxes;

/**
 * Created by Alex on 10/6/2017.
 */

public class JoinJukebox extends Activity {
    private User user;
    private Logging log;
    private String authToken;
    private ListView lv;
    private static final String TAG = Login.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        log = new Logging();
        Bundle extras = getIntent().getExtras();
        authToken = extras.getString("EXTRA_TOKEN");
        user = User.getInstance();
        user.setTypeOfUser(UserType.SUBSCRIBER);
        setContentView(R.layout.activity_jukebox_subscriber_options);



        lv = (ListView) findViewById(list_of_jukeboxes);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        List<String> jukebox_array_list = new ArrayList<String>();
        jukebox_array_list.add("Jukebox 1");
        jukebox_array_list.add("Jukebox 2");
        jukebox_array_list.add("Jukebox 3");
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                jukebox_array_list );

        lv.setAdapter(arrayAdapter);
    }


    public void onStartJukeboxClicked2(View view){
        log.logMessage(TAG, "JOINED JUKEBOX!");

        Intent MainActivityIntent = new Intent(this, MainActivity.class);
        MainActivityIntent.putExtra("EXTRA_TOKEN", authToken);
        startActivity(MainActivityIntent);
        finish();

        // startNewUser();
    }


    //create a listener and populate the list
}


