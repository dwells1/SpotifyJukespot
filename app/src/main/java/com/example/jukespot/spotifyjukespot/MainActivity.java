package com.example.jukespot.spotifyjukespot;


        import android.content.BroadcastReceiver;
        import android.content.DialogInterface;
        import android.net.Uri;
/*TODO:WHEN MAKING A NEW FRAGMENT MAKE SURE THIS VERSION OF FRAGMENT IS IMPORTED IN THAT FILE**/
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
        import com.example.jukespot.spotifyjukespot.Enums.Discoverable;
        import com.example.jukespot.spotifyjukespot.Enums.UserType;
        import com.example.jukespot.spotifyjukespot.Classes.ViewTypeFragments;
        import com.example.jukespot.spotifyjukespot.CurrentQueue.CurrentQueueFragment;
        import com.example.jukespot.spotifyjukespot.CurrentlyPlaying.CurrentlyPlayingFragment;
        import com.example.jukespot.spotifyjukespot.Logging.Logging;
        import com.example.jukespot.spotifyjukespot.MusicPlayer.MusicPlayer;
        import com.example.jukespot.spotifyjukespot.PubNub.PubNubConstants;
        import com.example.jukespot.spotifyjukespot.PubNub.PubSubPnCallback;
        import com.example.jukespot.spotifyjukespot.Search.SearchFragment;
        import com.example.jukespot.spotifyjukespot.WebServices.ServiceGatewayListener;
        import com.example.jukespot.spotifyjukespot.WebServices.ServicesGateway;
        import com.google.android.gms.location.FusedLocationProviderClient;

/*music player imports*/
        import com.spotify.sdk.android.player.Config;
        import com.pubnub.api.PNConfiguration;

        import com.pubnub.api.PubNub;

        import java.util.Arrays;
        import java.util.List;


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
            //if(user.getTypeOfUser().equals("Creator"))
            initPlayer();
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
    }
    public final void initPubNub(String channel){
        PNConfiguration config = new PNConfiguration();
        config.setPublishKey(PubNubConstants.PUBNUB_PUBLISH_KEY);
        config.setSubscribeKey(PubNubConstants.PUBNUB_SUBSCRIBE_KEY);

        PubNub pubnub = new PubNub(config);
        PubSubPnCallback callback = new PubSubPnCallback();
        pubnub.addListener(callback);
        pubnub.subscribe().channels(Arrays.asList(channel)).withPresence().execute(); //subscribe to a channel
    }
    /*music player functions*/
    public void initPlayer(){
        Config playerConfig = new Config(this, token, CLIENT_ID);
        musicPlayer = new MusicPlayer();
        musicPlayer.initSpotifyPlayer(playerConfig);
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
                    log.logMessage(TAG, "pressed leave current jukebox");
                    Toast.makeText(getApplicationContext(), "Jukebox Ended", Toast.LENGTH_SHORT).show();
                    musicPlayer.endCurrentPlayer();
                    setDiscoverable(con);
                }
                if (message.equals("Are you sure you want to logout?")){
                    //Creator and subcriber have the same behavior for now.
                    /*TODO: Need to add ending a jukespot or leaving a jukespot to the logout
                    /*TODO: Create another logout for Subscriber*/
                    log.logMessage(TAG, "pressed Are you sure you want to logout");
                    Toast.makeText(getApplicationContext(), "You sucessfully logout", Toast.LENGTH_SHORT).show();
                    try {
                          musicPlayer.endCurrentPlayer();
                          gateway.setDiscoverable(con, Discoverable.N);
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
                log.logMessageWithToast(con,TAG,"Failed to create Jukebox");
            }
        });
        gateway.leaveJukebox(this,json);
    }
}