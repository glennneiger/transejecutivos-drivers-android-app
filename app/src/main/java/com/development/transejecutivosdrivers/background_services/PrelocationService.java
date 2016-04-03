package com.development.transejecutivosdrivers.background_services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by developer on 4/3/16.
 */
public class PrelocationService extends IntentService implements
        LocationListener {
    // Must create a default constructor
    public PrelocationService() {
        // Used to name the worker thread, important only for debugging.
        super("PrelocationService");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        Log.i("PrelocationService", "Service running");
        int idService = intent.getIntExtra("idService", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);
        double latitude = intent.getDoubleExtra("latitude", 0);

        Log.d("idService", "" + idService);
        Log.d("longitude", "" + longitude);
        Log.d("latitude", "" + latitude);
    }



    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}