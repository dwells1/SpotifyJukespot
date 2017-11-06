package com.example.jukespot.spotifyjukespot;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;

import com.example.jukespot.spotifyjukespot.Classes.User;
import com.example.jukespot.spotifyjukespot.Logging.Logging;

public class LocationService extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private Logging log;
    private static final String TAG = LocationService.class.getSimpleName();
    private volatile HandlerThread mHandlerThread;
    private ServiceHandler mServiceHandler;
    private User user;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        // Define how to handle any incoming messages here
        @Override
        public void handleMessage(Message message) {
            // ...
            // When needed, stop the service with
            // stopSelf();
        }
    }

    public LocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        user = User.getInstance();
        log = new Logging();
        mHandlerThread = new HandlerThread("MyCustomService.HandlerThread");
        mHandlerThread.start();
        // An Android service handler is a handler running on a specific background thread.
        mServiceHandler = new ServiceHandler(mHandlerThread.getLooper());

    }

    @Override
    public int onStartCommand(final Intent intent, int flags,final int startId) {

        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                startLocationUpdate(intent,startId);
            }
        });
        return START_NOT_STICKY;
    }

    private void startLocationUpdate(final Intent intent, final int startId){

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                user.setLatitude(location.getLatitude());
                user.setLongitude(location.getLongitude());
                log.logMessage(TAG,"Longitude is: " +user.getLongitude() + " Latitude is: " + user.getLatitude());
                log.logMessage(TAG,Thread.currentThread().getName());
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
    }

    @Override
    public void onDestroy() {
        mHandlerThread.quit();
        super.onDestroy();
        log.logMessage(TAG,"LocationClass Listener Destroyed");
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
