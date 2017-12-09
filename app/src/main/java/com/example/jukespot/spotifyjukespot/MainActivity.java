package com.example.jukespot.spotifyjukespot;


        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.DialogInterface;
        import android.location.Location;
        import android.net.Uri;
/*TODO:WHEN MAKING A NEW FRAGMENT MAKE SURE THIS VERSION OF FRAGMENT IS IMPORTED IN THAT FILE**/
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.content.Context;
        import android.content.Intent;
        import android.view.KeyEvent;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.jukespot.spotifyjukespot.Classes.JukeBoxResponse;
        import com.example.jukespot.spotifyjukespot.Classes.User;
        import com.example.jukespot.spotifyjukespot.CurrentQueue.ChangeType;
        import com.example.jukespot.spotifyjukespot.Enums.Discoverable;
        import com.example.jukespot.spotifyjukespot.Enums.UserType;
        import com.example.jukespot.spotifyjukespot.Classes.ViewTypeFragments;
        import com.example.jukespot.spotifyjukespot.CurrentQueue.CurrentQueueFragment;
        import com.example.jukespot.spotifyjukespot.CurrentlyPlaying.CurrentlyPlayingFragment;
        import com.example.jukespot.spotifyjukespot.Logging.Logging;
        import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
        import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayerDelegate;
        import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
        import com.example.jukespot.spotifyjukespot.MusicPlayer.SimpleTrack;
        import com.example.jukespot.spotifyjukespot.PubNub.PubNubConstants;
        import com.example.jukespot.spotifyjukespot.PubNub.PubNubService;
        import com.example.jukespot.spotifyjukespot.PubNub.PubSubPnCallback;
        import com.example.jukespot.spotifyjukespot.Search.SearchFragment;
        import com.example.jukespot.spotifyjukespot.WebServices.ServiceGatewayListener;
        import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.Result;
        import com.google.android.gms.common.api.ResultCallback;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.FusedLocationProviderClient;

/*music player imports*/
        import com.google.android.gms.location.Geofence;
        import com.google.android.gms.location.GeofencingRequest;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;
        import com.spotify.sdk.android.player.Config;
        import com.pubnub.api.PNConfiguration;

        import com.pubnub.api.PubNub;

        import java.io.Serializable;
        import java.util.Arrays;
        import java.util.List;
        import java.util.Observable;
        import java.util.Observer;


/*TODO: When Adding new Fragments you have to implement them as the ones here*/
public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener,
        CurrentlyPlayingFragment.OnFragmentInteractionListener, CurrentQueueFragment.OnFragmentInteractionListener
        ,Observer {
    private static final String TAG = MainActivity.class.getSimpleName();
    Logging log = new Logging();

    private GoogleApiClient googleClient = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationListener locListener;
    private ResultCallback<Status> geoCallback;
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
    private Integer transaction_id;
    private String channel;
    private MusicPlayer musicPlayer;
    private FragmentManager manager;

    boolean isConfirmed;
    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "4309049aaf574f63b61d3408408a4ff2";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "jukebox://callback";

    private static final int REQUEST_CODE = 1337;
    private User user;
    private ServicesGateway gateway;
    /* NOTE: When Changing fragments update current viewtype
     * and check if current view type is the same as the new viewtype
     * implemented for search and current queue look there
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = getSupportFragmentManager();
        user = User.getInstance();
        gateway = ServicesGateway.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            token = extras.getString("EXTRA_TOKEN");
            if(extras.getInt("TRANSACTION_ID") != -1) {
                transaction_id = extras.getInt("TRANSACTION_ID");
                log.logMessage(TAG, "transaction id is " + transaction_id);
            }
            if(!extras.get("CHANNEL").equals("none")){
                channel = extras.getString("CHANNEL");
                log.logMessage(TAG, "Channel is " + channel);
                initPubNub(channel);
            }
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();

            JukeBoxResponse response =
                    (JukeBoxResponse) bundle.getSerializable("CURRENT_QUEUE");
            //if(user.getTypeOfUser().equals("Creator"))
            if(response!=null) {
                initPlayer(response.getLocation_fields().getCurrentQueue());
            }else{
                initPlayer(null);
            }
//            Log.d(TAG,token);
            //The key argument here must match that used in the other activity
        }

        /*Functions for Navigable Menu*/
        initDrawerLayout();
        addItemsToDrawerMenu();
        setupDrawerMenu();
        setFirstFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        googleClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                log.logMessage(TAG,"API Connection sucessful");
                startLocatiionMonitoring();
                startGeofenceMonitoring();
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        }).build();
        googleClient.connect();
    }


    private void startLocatiionMonitoring(){
        try{
            LocationRequest locrec =LocationRequest.create().setInterval(10000)
                    .setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    log.logMessage(TAG,"Location update" + location.getLatitude() + " " + location.getLongitude());
                }
            };
            LocationServices.FusedLocationApi.requestLocationUpdates(googleClient, locrec, locListener);
        }catch(SecurityException e){
            log.logMessage(TAG,"Security exception");
        }
    }

    private void startGeofenceMonitoring(){
        log.logMessage(TAG,"Started GeoFenceMonitoring");
        try{
            Geofence gfence = new Geofence.Builder().setRequestId("Geofence").setCircularRegion(29.583,-98.6197,100)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setNotificationResponsiveness(1000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            GeofencingRequest grec =new GeofencingRequest.Builder().setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .addGeofence(gfence).build();

            Intent intent = new Intent(this,GeoFenceService.class);
            PendingIntent pendingIntent =PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            geoCallback = new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    if(status.isSuccess()){
                        log.logMessage(TAG,"Successfully added geofence");
                    }else{
                        log.logMessage(TAG,"Failed to add geofence:" + status.getStatus());
                    }
                }
            };

            LocationServices.GeofencingApi.addGeofences(googleClient,grec,pendingIntent)
                    .setResultCallback(geoCallback);
        }catch (SecurityException e){
            log.logMessage(TAG,"Exception cought " + e);
        }
    }

    public final void initPubNub(String channel){
        Intent pubNubServiceIntent = new Intent(this, PubNubService.class);
        pubNubServiceIntent.setData(Uri.parse(channel));
        startService(pubNubServiceIntent);
    }

    /*music player functions*/
    public void initPlayer(List<SimpleTrack> queue){
        Config playerConfig = new Config(this, token, CLIENT_ID);
       // musicPlayer = new MusicPlayer();
        musicPlayer = MusicPlayer.getInstance();
        log.logMessage(TAG,"current queue " + musicPlayer.getCurrentQueue());
        musicPlayer.initSpotifyPlayer(playerConfig);
        musicPlayer.addObserverToDelegate(this);
        if(queue!=null) {
            musicPlayer.setCurrentQueue(queue);
        }

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
        if(user.getTypeOfUser().equals(UserType.CREATOR))  {
            mainUserOptionsForDrawer = new String[]{"Search", "Current Queue",
                "Currently Playing", "End Current Jukebox", "Logout"};
        }   else{
             mainUserOptionsForDrawer = new String[]{"Search", "Current Queue",
                 "Currently Playing", "Leave Jukebox", "Logout"};
        }

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
            createAlert("Are you sure you want to end current jukebox?");

        }else if(currentSelectionFromMenuTitle.equals("Logout")){

            createAlert("Are you sure you want to logout?");

        }else if (currentSelectionFromMenuTitle.equals("Leave Jukebox")){
            //Subscriber
            createAlert("Are you sure you want to leave current jukebox?");
        }
        openChosenFrag(currentFrag);

    }

    public void createAlert(final String message){

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        final Context con = this;
        alertDlg.setMessage(message);
        alertDlg.setCancelable(false);
        alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which){
                isConfirmed = true;
                log.logMessage(TAG, "pressed yes to end current jukebox");
                if(message.equals("Are you sure you want to end current jukebox?")){
                    //Creator
                    log.logMessage(TAG, "pressed end current jukebox");
                    Toast.makeText(getApplicationContext(), "Jukebox Ended", Toast.LENGTH_SHORT).show();
                    musicPlayer.endCurrentPlayer();
                    setDiscoverable(con);
                }
                if (message.equals("Are you sure you want to logout?")){
                    log.logMessage(TAG, "pressed Are you sure you want to logout");
                    Toast.makeText(getApplicationContext(), "You sucessfully logged out", Toast.LENGTH_SHORT).show();
                    try {
                          musicPlayer.endCurrentPlayer();
                          /*check if user is subscriber they just leave jukebox
                            else it gets set to not discoverable*/
                          if(user.getTypeOfUser() == UserType.CREATOR) {
                              gateway.setDiscoverable(con, Discoverable.N);
                          }else{
                              gateway.leaveJukebox(con, "{\"transaction_id\":"+ transaction_id+ "}");
                          }
                    }catch(NullPointerException e) {
                         log.logMessage(TAG, "Music Player was not initialize before login out");
                    }
                    Intent jukeboxLoginIntent = new Intent(getApplicationContext(), Login.class);
                    startActivity(jukeboxLoginIntent);
                    finish();
                }
                if(message.equals("Are you sure you want to leave current jukebox?")){
                    //Subscriber
                    leaveJukebox(con);
                }

            }
        });
        alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isConfirmed = false;
            }
        });
        alertDlg.create().show();

    }

    public void sendAddSongToService(SimpleTrack trackToSend){
        gateway.addSongToPlaylist(this, transaction_id, trackToSend);
    }
    public void sendPlaySongToService(SimpleTrack trackToPlay){
        gateway.playSongNow(this, transaction_id, trackToPlay);
    }
    public void sendRemoveSongToService(SimpleTrack trackToRemove){
        gateway.removeSongFromPlaylist(this, transaction_id, trackToRemove);
    }

    public void sendSongToService(SimpleTrack trackToSend){
        gateway.addSongToPlaylist(this, transaction_id, trackToSend);
    }
    public void openChosenFrag(Fragment currentFrag){
        if(currentFrag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTransaction = null;
            try {
                fragTransaction = manager.beginTransaction();
                fragTransaction.replace(R.id.content_frame, currentFrag,currentFrag.getClass().toString());
                fragTransaction.commit();

            } catch (Exception FragNotFound) {
                FragNotFound.printStackTrace();
            }
            currentActivityTitle = currentSelectionFromMenuTitle;
            if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            }
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

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUICurrentlyPlaying();
        }
    };
    public void updateGUICurrentlyPlaying(){
        if(currentFragmentView.equals(ViewTypeFragments.CURRENTLY_PLAYING)){
            Fragment current = new CurrentlyPlayingFragment();
            openChosenFrag(current);
        }
    }

    public void updateCurrentViewType(ViewTypeFragments current){
        currentFragmentView = current;
    }

    public ViewTypeFragments getCurrentFrgament() {
        return currentFragmentView;
    }

    protected void onDestroy() {
        super.onDestroy();
        Intent i =new Intent(this,GeoFenceService.class);
        PendingIntent pendingIntent =PendingIntent.getService(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        LocationServices.GeofencingApi.removeGeofences(googleClient,pendingIntent);
        LocationServices.FusedLocationApi.removeLocationUpdates(googleClient,locListener);
        googleClient.disconnect();
        log.logMessage(TAG,"Destroyed Called *************");
    }

    private void setDiscoverable(final Context con){
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {
                Intent jukeboxOptionsIntent = new Intent(getApplicationContext(), JukeboxUserOptions.class);
                startActivity(jukeboxOptionsIntent);
                finish();
            }

            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes) {

            }

            @Override
            public void onError() {
                log.logMessageWithToast(con,TAG,"Failed to leave Jukebox");
            }
        });
        gateway.setDiscoverable(this,Discoverable.N);
    }

    private void leaveJukebox(final Context con){
        String json = "{\"transaction_id\":"+ transaction_id + "}";;
        gateway.setListener(new ServiceGatewayListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(),"Left Jukebox",Toast.LENGTH_SHORT).show();
                musicPlayer.endCurrentPlayer();
                Intent jukeboxOptionsIntent = new Intent(getApplicationContext(), JukeboxUserOptions.class);
                startActivity(jukeboxOptionsIntent);
                finish();
            }

            @Override
            public void gotPlaylists(List<JukeBoxResponse> jukeboxes) {

            }

            @Override
            public void onError() {
                log.logMessageWithToast(con,TAG,"Failed to Leave Jukebox");
            }
        });
        gateway.leaveJukebox(con,json);
    }

    @Override
    public void update(Observable observable, Object o) {
        MusicPlayerDelegate delegate = (MusicPlayerDelegate) observable;
        ChangeType whatToDo = (ChangeType) o;
        SimpleTrack trackChosen = delegate.trackChosen;
        if(whatToDo == ChangeType.REMOVE_FROM_SERVICE) {
            sendRemoveSongToService(trackChosen);
        }else if(whatToDo == ChangeType.UPDATE_GUI){
            if(currentFragmentView.equals(ViewTypeFragments.CURRENTLY_PLAYING)){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.logMessage(TAG, "unable to sleep");
                }
                Fragment current = new CurrentlyPlayingFragment();
                openChosenFrag(current);
            }
        }
    }
}