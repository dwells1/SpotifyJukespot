package com.example.jukespot.spotifyjukespot;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

public class LocationIntentServices extends IntentService {
    private LocationListener listener;
    private LocationManager locationManager;
    private Logging log = new Logging();
    private User user;
    private Location location;
    private static final String TAG = LocationIntentServices.class.getSimpleName();

    public LocationIntentServices() {
        super("LocationIntentServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        log.logMessage(TAG,"location fetching...");
        user = User.getInstance();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");
                i.putExtra("coordinates",location.getLongitude()+" "+location.getLatitude());
                sendBroadcast(i);
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }
            @Override
            public void onProviderEnabled(String s) {

            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,300000,0,listener);

        //noinspection MissingPermission
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            user.setLatitude(location.getLatitude());
            user.setLongitude(location.getLongitude());
            log.logMessage(TAG, "Longitude is: " + user.getLongitude() + " Latitude is: " + user.getLatitude());
        }
    }

    @Override
    public boolean stopService(Intent name) {
        log.logMessage(TAG,"Stopping service");
        return super.stopService(name);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        log.logMessage(TAG,"LocationClass Listener Destroyed");
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
