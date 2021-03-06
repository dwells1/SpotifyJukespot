package com.example.jukespot.spotifyjukespot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jukespot.spotifyjukespot.Adapters.JukeboxListAdapter;
import com.example.jukespot.spotifyjukespot.Classes.JukeBox;
import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Enums.Discoverable;
import com.example.jukespot.spotifyjukespot.Enums.UserPermissions;
import com.example.jukespot.spotifyjukespot.Enums.UserType;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
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
    private String CURRENT_QUEUE = "CURRENT_QUEUE";

    private boolean isConfirmed = true;
    private String passwordForJuke;

    private Loading load;
    private Context con = this;

    private static final String TAG = JoinJukebox.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        log = new Logging();
        Bundle extras = getIntent().getExtras();
        authToken = extras.getString("EXTRA_TOKEN");
        user = User.getInstance();
        user.setTypeOfUser(UserType.SUBSCRIBER);
        load = new Loading(this);
        jukebox_array_list = new ArrayList<JukeBoxResponse>();
        gateway = ServicesGateway.getInstance();
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {

            }
            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes) {
                for (JukeBoxResponse j : jukeboxes) {
                    log.logMessage(TAG, "got Playlist:" + j.getLocation_fields().toString());
                    addJukeBox(j);
                }
                initListView();

            }
            @Override
            public void onError() {

            }
        });
        gateway.getJukeboxes();
    }

    private void initListView(){
        setContentView(R.layout.activity_jukebox_subscriber_options);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

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
                jukebox_array_list);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object selected = lv.getItemAtPosition(position);
                JukeBoxResponse messageSelected = (JukeBoxResponse) selected;
                if (messageSelected != null) {
                    JukeBox jukeToJoin = messageSelected.getLocation_fields(); //LOCATION FIELDS IS FOR SUBSCRIBERS
                    String password = "";
                    if(jukeToJoin.hasPassword()){
                        password = jukeToJoin.getPassword();
                        createPasswordAlert(password, messageSelected);
                    }else{
                        log.logMessage(TAG, "NO PASSWORD Required");
                        log.logMessage(TAG, messageSelected.getLocation_fields().toString());
                        load.startLoading(con,null,null);
                        joinJukebox(messageSelected.getTransaction_id());
                    }
                }

            }
        });

    }

    public void onJoinJukeboxClicked(View view) {
        log.logMessage(TAG, "Nothing Happens!");
        // startNewUser();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent jukeboxUserOptionsIntent = new Intent(this,
                    JukeboxUserOptions.class);
            startActivity(jukeboxUserOptionsIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addJukeBox(JukeBoxResponse jukebox) {
        jukebox_array_list.add(jukebox);
    }

    private void joinJukebox(final Integer id) {
        String json = "{\"transaction_id\":" + id + "}";
        log.logMessage(TAG, json);
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes) {
                String channel = "";
                JukeBoxResponse jukeboxResponse = null;
                for (JukeBoxResponse jboxes : jukeboxes) {
                    if (!jboxes.getChannel().equals("none")) {
                        channel = jboxes.getChannel();
                        jukeboxResponse = jboxes;
                        JukeBox joinedBox = jboxes.getLocation_fields();
                        if (joinedBox.getPlayAutomatic() && joinedBox.getQueueEditable()) {
                            user.setUserPermissions(UserPermissions.CAN_PLAY_AND_EDIT);
                        } else if (!joinedBox.getQueueEditable() && joinedBox.getPlayAutomatic()) {
                            user.setUserPermissions(UserPermissions.CAN_PLAY_NO_EDIT);
                        } else if (joinedBox.getQueueEditable() && !joinedBox.getPlayAutomatic()) {
                            user.setUserPermissions(UserPermissions.CAN_EDIT_NO_PLAY);
                        } else if (!joinedBox.getQueueEditable() && !joinedBox.getPlayAutomatic()) {
                            user.setUserPermissions(UserPermissions.NO_EDIT_NO_PLAY);
                        }

                        log.logMessage(TAG, "User Permissions: " + user.getUserPermissions().toString());
                        log.logMessage(TAG, "Channel:" + channel);
                    }
                }
                log.logMessage(TAG, "join successful");
                finishJoin(id, channel,jukeboxResponse);
                log.logMessage(TAG,"joinedQueue:"+jukeboxResponse.getLocation_fields().getCurrentQueue());
            }

            @Override
            public void onError() {
                load.finishLoading(con,null,null);
            }
        });
        gateway.joinJukebox(this, json);
    }

    private void finishJoin(Integer id, String channel, JukeBoxResponse jukebox) {
        log.logMessage(TAG, "JOINED JUKEBOX!");

        Intent i = new Intent(this, LocationIntentServices.class);
        startService(i);

        Intent MainActivityIntent = new Intent(this, MainActivity.class);
        MainActivityIntent.putExtra(EXTRA_TOKEN, authToken);
        MainActivityIntent.putExtra(CHANNEL, channel);
        MainActivityIntent.putExtra(TRANSACTION_ID, id);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CURRENT_QUEUE, jukebox);
        MainActivityIntent.putExtras(bundle);
        startActivity(MainActivityIntent);
        finish();
    }

    //create a listener and populate the list


    public void createPasswordAlert(final String passwordFromJukebox, final JukeBoxResponse messageSelected) {
        final String message = "Enter Password";
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);

        final EditText edPassword = new EditText(this);
        edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertDlg.setView(edPassword);
        alertDlg.setTitle("Password Required");
        alertDlg.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(edPassword.getText().equals("") || edPassword.equals(null)){
                    log.logMessageWithToast(JoinJukebox.this, TAG, "No password inputted!");
                }else{
                    passwordForJuke = edPassword.getText().toString();
                    log.logMessage(TAG, "Password Inputted: " + passwordForJuke);
                    log.logMessage(TAG, "PASSWORD: " + passwordFromJukebox);
                    if(passwordFromJukebox.equals(passwordForJuke)){
                        log.logMessageWithToast(JoinJukebox.this, TAG, "Password is Correct!");
                        log.logMessage(TAG, messageSelected.getLocation_fields().toString());
                        joinJukebox(messageSelected.getTransaction_id());
                    }else{
                        log.logMessageWithToast(JoinJukebox.this, TAG, "Password is incorrect!");
                    }

                }
            }
        });


        alertDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                log.logMessage(TAG, "nothing inputted");
                passwordForJuke="";
            }
        });
        alertDlg.create().show();
    }
}


