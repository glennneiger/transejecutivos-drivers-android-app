package com.development.transejecutivosdrivers.background_services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by william.montiel on 04/04/2016.
 */
public abstract class IntentServiceBase extends IntentService implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest = null;
    protected boolean mRequestingLocationUpdates = false;
    protected final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    protected static int UPDATE_INTERVAL = 10000; // 10 sec
    protected static int FATEST_INTERVAL = 5000; // 5 sec
    protected static int DISPLACEMENT = 10; // 10 meters

    protected Context context;
    protected int idService;
    protected String apikey;
    protected String location;
    protected boolean isDataComplete = false;

    public IntentServiceBase() {
        super("IntentServiceBase");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        this.context = getApplicationContext();

        Log.d("LOCATION SERVICE", "ON CREATE");

        if (checkPlayServices()) {
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    */

    @Override
    protected void onHandleIntent(Intent intent) {
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        Log.i("IntentServiceBase", "Service running");

        Log.d("RECEIVE", "ONHANDLE");

        Bundle t = intent.getExtras();
        if (t != null) {
            idService = t.getInt(JsonKeys.SERVICE_ID);
            apikey = t.getString(JsonKeys.USER_APIKEY);
            location = t.getString(JsonKeys.LOCATION);

            this.isDataComplete = true;
        }
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        if (mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
        }
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        try {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
        catch (SecurityException e) {

        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        onConnectedBundle(connectionHint);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), R.string.error_general,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;
    }

    public abstract void getLocation();

    public void onConnectedBundle(Bundle connectionHint) {
        // Once connected with google api, get the location
        getLocation();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }
}
