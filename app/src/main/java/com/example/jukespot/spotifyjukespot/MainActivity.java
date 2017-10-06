package com.example.jukespot.spotifyjukespot;

import android.net.Uri;
/*TODO:WHEN MAKING A NEW FRAGMENT MAKE SURE THIS VERSION OF FRAGMENT IS IMPORTED IN THAT FILE**/
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.jukespot.spotifyjukespot.Classes.ViewTypeFragments;
import com.example.jukespot.spotifyjukespot.CurrentlyPlaying.CurrentlyPlayingFragment;
import com.example.jukespot.spotifyjukespot.Logging.Logging;
import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
import com.example.jukespot.spotifyjukespot.Search.SearchFragment;
import com.google.android.gms.location.FusedLocationProviderClient;

/*music player imports*/
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.SpotifyPlayer;


import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/*TODO: When Adding new Fragments you have to implement them as the ones here*/
public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
    CurrentlyPlayingFragment.OnFragmentInteractionListener, CurrentQueueFragment.OnFragmentInteractionListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    Logging log = new Logging();

    private FusedLocationProviderClient mFusedLocationClient;
    /*Drawer Navigation*/
    private ActionBarDrawerToggle menuDrawerToggle;
    private String currentSelectionFromMenuTitle;
    private String[] mainUserOptionsForDrawer;
    private String currentActivityTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> menuAdaptor;

    private ViewTypeFragments currentFragmentView;
    private String token;
    private MusicPlayer musicPlayer;
    private FragmentManager manager;

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "4309049aaf574f63b61d3408408a4ff2";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "jukebox://callback";

    private static final int REQUEST_CODE = 1337;
    /* NOTE: When Changing fragments update current viewtype
     * and check if current view type is the same as the new viewtype
     * implemented for search and current queue look there
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getSupportFragmentManager();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("EXTRA_TOKEN");
            initPlayer();
            Log.d(TAG,token);
            //The key argument here must match that used in the other activity
        }

        /*Functions for Navigable Menu*/
        initDrawerLayout();
        addItemsToDrawerMenu();
        setupDrawerMenu();
        setFirstFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /*Location Stuff*/
    /** Called when the user taps the Send button */
    public void sendMessage (View view){
        Intent intent = new Intent(this, GoogleLocActivity.class);
        startActivity(intent);
    }

    /*music player functions*/
    public void initPlayer(){
        Config playerConfig = new Config(this, token, CLIENT_ID);
        musicPlayer = new MusicPlayer();
        musicPlayer.initSpotifyPlayer(playerConfig);
    }
    public void playSong(String uri){
        musicPlayer.play(uri);
    }

    public void queueSong(Track toQueue){
        log.logMessage(TAG, "queue song is called! for toQueue = " + toQueue.name);
        musicPlayer.queue(toQueue.uri);
    }
    public MusicPlayer getMusicPlayer(){
        if(musicPlayer == null){
            log.logErrorNoToast(TAG, "Error Null Music Player");
            return null;
        }
        return musicPlayer;
    }
    public void initDrawerLayout(){
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        currentActivityTitle = getTitle().toString();
    }
    public void addItemsToDrawerMenu() {
        mainUserOptionsForDrawer = new String[]{"Search", "Current Queue",
                "Currently Playing", "End Current Jukebox", "Logout"};

        menuAdaptor = new ArrayAdapter<String>(this,
                R.layout.drawer_options_list_layout, mainUserOptionsForDrawer);

        mDrawerList.setAdapter(menuAdaptor);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSelectionFromMenuTitle = ((TextView) view.findViewById(R.id.itemChosen)).getText().toString();
               // Toast.makeText(MainActivity.this, currentSelectionFromMenuTitle, Toast.LENGTH_SHORT).show();
                selectMenuItem(position);
            }
        });
    }
    public void selectMenuItem(int position){
        Fragment currentFrag = null;
        boolean isFragmentNeeded = true;
        FragmentManager fm = getSupportFragmentManager();

        /*TODO: Create Fragments for Current Queue, and Currently Playing*/
        if(currentSelectionFromMenuTitle.equals("Search")){
            if(currentFragmentView != ViewTypeFragments.SEARCH_VIEW){
                currentFrag = new SearchFragment();
                updateCurrentViewType(ViewTypeFragments.SEARCH_VIEW);
            }else{
                mDrawerLayout.closeDrawer(mDrawerList);
            }

        }else if(currentSelectionFromMenuTitle.equals("Currently Playing")){
            if(currentFragmentView != ViewTypeFragments.CURRENTLY_PLAYING){
                currentFrag = new CurrentlyPlayingFragment();
                updateCurrentViewType(ViewTypeFragments.CURRENTLY_PLAYING);
            }else{
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        }else if(currentSelectionFromMenuTitle.equals("Current Queue")){
            if(currentFragmentView != ViewTypeFragments.CURRENT_QUEUE){
                currentFrag = new CurrentQueueFragment();
                updateCurrentViewType(ViewTypeFragments.CURRENT_QUEUE);
            }else{
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        }else if(currentSelectionFromMenuTitle.equals("End Current Jukebox")){
            /*TODO: Add Alert so user confirms ending jukebox*/
            Toast.makeText(this,"Jukebox Ended",Toast.LENGTH_SHORT).show();
            musicPlayer.endCurrentPlayer();
            Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
            startActivity(jukeboxOptionsIntent);
            finish();
        }else if(currentSelectionFromMenuTitle.equals("Logout")){
            /*TODO: Should this log them out of spotify??*/
            Toast.makeText(this,"Logout Successful",Toast.LENGTH_SHORT).show();
            musicPlayer.endCurrentPlayer();
            Intent jukeboxLoginIntent = new Intent(this, Login.class);
            startActivity(jukeboxLoginIntent);
            finish();
        }
        if(currentFrag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTransaction = null;
            try {
                fragTransaction = manager.beginTransaction();
                fragTransaction.replace(R.id.content_frame, currentFrag,currentFrag.getClass().toString());
                fragTransaction.commit();
//                fragTransaction = fragmentManager.beginTransaction();
//                fragTransaction.replace(R.id.content_frame, currentFrag);
//                fragTransaction.commit();
            } catch (Exception FragNotFound) {
                FragNotFound.printStackTrace();
            }
            currentActivityTitle = currentSelectionFromMenuTitle;
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    public void setupDrawerMenu(){
        menuDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(currentActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        menuDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(menuDrawerToggle);
    }


    public void setFirstFragment(){
        Fragment currentFrag = new SearchFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = null;
        try {
            fragTransaction = manager.beginTransaction();
            fragTransaction.replace(R.id.content_frame, currentFrag);
            fragTransaction.commit();
        }catch(Exception FragNotFound){
            FragNotFound.printStackTrace();
        }
        updateCurrentViewType(ViewTypeFragments.SEARCH_VIEW);
        currentActivityTitle = "Search";
        setTitle(currentActivityTitle);
        mDrawerLayout.closeDrawer(mDrawerList);

    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        menuDrawerToggle.syncState();
    }
    /*TODO: Check what this does is it needed for loading more complex Fragments??*/
    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
       // int itemID = item.getItemId();

        if (menuDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK)){
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                mDrawerLayout.closeDrawer(mDrawerList);
                return true;
            }else {
                mDrawerLayout.openDrawer(mDrawerList);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    public void updateCurrentViewType(ViewTypeFragments current){
        currentFragmentView = current;
    }

}