package com.example.jukespot.spotifyjukespot;

import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
/**TODO:WHEN MAKING A NEW FRAGMENT MAKE SURE THIS VERSION OF FRAGMENT IS IMPORTED IN THAT FILE**/
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/*TODO: When Adding new Fragments you have to implement them as the ones here*/
public class MainActivity extends AppCompatActivity implements SearchFragment.OnFragmentInteractionListener, CurrentQueueFragment.OnFragmentInteractionListener{
    private FusedLocationProviderClient mFusedLocationClient;
    /*Drawer Navigation*/
    private ActionBarDrawerToggle menuDrawerToggle;
    private String currentSelectionFromMenuTitle;
    private String[] mainUserOptionsForDrawer;
    private String currentActivityTitle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ArrayAdapter<String> menuAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Functions for Navigable Menu*/
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        currentActivityTitle = getTitle().toString();
        addItemsToDrawerMenu();
        setupDrawerMenu();
        setFirstFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        /*Location Stuff*/
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitutde = location.getLongitude();
                        }
                    }
                });
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
    /*TODO: add detection so user cannot press same item twice and just reload*/
    public void selectMenuItem(int position){
        Fragment currentFrag = null;
        boolean isFragmentNeeded = true;

        /*TODO: Create Fragments for other menu options except maybe logout*/
        if(currentSelectionFromMenuTitle.equals("Search")){
            currentFrag = new SearchFragment();

        }else if(currentSelectionFromMenuTitle.equals("Current Queue")){
            currentFrag = new CurrentQueueFragment();
        }else if(currentSelectionFromMenuTitle.equals("End Current Jukebox")){
            /*TODO: Add Alert so user confirms ending jukebox*/
            Toast.makeText(this,"Jukebox Ended",Toast.LENGTH_SHORT).show();
            Intent jukeboxOptionsIntent = new Intent(this, JukeboxUserOptions.class);
            startActivity(jukeboxOptionsIntent);
            finish();
        }else if(currentSelectionFromMenuTitle.equals("Logout")){
            /*TODO: Should this log them out of spotify??*/
            Toast.makeText(this,"Logout Successfull",Toast.LENGTH_SHORT).show();
            Intent jukeboxLoginIntent = new Intent(this, Login.class);
            startActivity(jukeboxLoginIntent);
            finish();
        }
        if(currentFrag != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragTransaction = null;
            try {
                fragTransaction = fragmentManager.beginTransaction();
                fragTransaction.replace(R.id.content_frame, currentFrag);
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
            fragTransaction = fragmentManager.beginTransaction();
            fragTransaction.replace(R.id.content_frame, currentFrag);
            fragTransaction.commit();
        }catch(Exception FragNotFound){
            FragNotFound.printStackTrace();
        }

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
        int itemID = item.getItemId();

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

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            public static final int REQUEST_CHECK_SETTINGS = 0x1 ;

            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}