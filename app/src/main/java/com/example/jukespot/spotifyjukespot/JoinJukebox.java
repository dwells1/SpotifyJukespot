package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.jukespot.spotifyjukespot.Adapters.JukeboxListAdapter;
import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.WebServices.ServiceGatewayListener;
import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;

import java.util.ArrayList;
import java.util.List;

import static com.example.jukespot.spotifyjukespot.R.id.list_of_jukeboxes;

/**
 * Created by Alex on 10/6/2017.
 */

public class JoinJukebox extends Activity {
    private User user;
    private Logging log;
    private String authToken;
    private ListView lv;
    private ServicesGateway gateway;
    private List<JukeBoxResponse> jukebox_array_list;
    private String EXTRA_TOKEN = "EXTRA_TOKEN";
    private String TRANSACTION_ID = "TRANSACTION_ID";
    private String CHANNEL = "CHANNEL";

    private static final String TAG = JoinJukebox.class.getSimpleName();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        log = new Logging();
        Bundle extras = getIntent().getExtras();
        authToken = extras.getString("EXTRA_TOKEN");
        user = User.getInstance();
        user.setTypeOfUser(UserType.SUBSCRIBER);
        jukebox_array_list = new ArrayList<JukeBoxResponse>();
        gateway = ServicesGateway.getInstance();
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes) {
                for(JukeBoxResponse j : jukeboxes){
                    log.logMessage(TAG,"got Playlist:"+j.getLocation_fields().toString());
                    addJukeBox(j);
                }
                initListView();

            }
            @Override
            public void onError(){

            }
        });
        gateway.getJukeboxes();
    }

    private void initListView(){
        setContentView(R.layout.activity_jukebox_subscriber_options);

        JukeboxListAdapter adapter = new JukeboxListAdapter(this,jukebox_array_list);

        lv = (ListView) findViewById(list_of_jukeboxes);

        // Instanciating an array list (you don't need to do this,
        // you already have yours).
        //List<JukeBox> jukebox_array_list = new ArrayList<JukeBox>();
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter<JukeBoxResponse> arrayAdapter = new ArrayAdapter<JukeBoxResponse>(
                this,
                android.R.layout.simple_list_item_1,
                jukebox_array_list );

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,int position, long id)
            {
                Object selected = lv.getItemAtPosition(position);
                JukeBoxResponse messageSelected = (JukeBoxResponse) selected;
                if (messageSelected != null) {
                    log.logMessage(TAG,messageSelected.getLocation_fields().toString());
                    joinJukebox(messageSelected.getTransaction_id());
                }

            }});

    }

    public void onJoinJukeboxClicked(View view){
        log.logMessage(TAG, "JOINED JUKEBOX!");

        Intent i = new Intent(this,LocationIntentServices.class);
        startService(i);

        Intent MainActivityIntent = new Intent(this, MainActivity.class);
        MainActivityIntent.putExtra("EXTRA_TOKEN", authToken);
        startActivity(MainActivityIntent);
        finish();

        // startNewUser();
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

    private void addJukeBox(JukeBoxResponse jukebox){
        jukebox_array_list.add(jukebox);
    }

    private void joinJukebox(final Integer id){
        String json = "{\"transaction_id\":"+ id + "}";
        log.logMessage(TAG,json);
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes) {
                String channel="";
                for(JukeBoxResponse jboxes : jukeboxes){
                    if(!jboxes.getChannel().equals("none")){
                        channel = jboxes.getChannel();
                        log.logMessage(TAG,"Channel:" + channel);
                    }
                }
                log.logMessage(TAG,"join successful");
                finishJoin(id,channel);
            }

            @Override
            public void onError() {

            }
        });
        gateway.joinJukebox(this,json);
    }

    private void finishJoin(Integer id,String channel){
        log.logMessage(TAG, "JOINED JUKEBOX!");

        Intent i = new Intent(this,LocationIntentServices.class);
        startService(i);

        Intent MainActivityIntent = new Intent(this, MainActivity.class);
        MainActivityIntent.putExtra(EXTRA_TOKEN, authToken);
        MainActivityIntent.putExtra(CHANNEL,channel);
        MainActivityIntent.putExtra(TRANSACTION_ID,id);
        startActivity(MainActivityIntent);
        finish();
    }

    //create a listener and populate the list
}


