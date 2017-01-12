package com.development.transejecutivosdrivers.background_services;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by william.montiel on 12/01/2017.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    protected int idService;
    protected String apikey;
    protected String location;

    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest = null;
    protected int UPDATE_INTERVAL = 7000; // 8 sec
    protected int FATEST_INTERVAL = 12000; // 4 sec
    protected int updates = 0;

    Context context;
    BackgroundServiceManager.ServiceHandler serviceHandler;

    public void setServiceHandler(BackgroundServiceManager.ServiceHandler serviceHandler) {
        this.serviceHandler = serviceHandler;
    }

    public void setData(int idService, String apikey, String location) {
        this.idService = idService;
        this.apikey = apikey;
        this.location = location;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void start() {
        if (checkGooglePlayServices()) {
            createLocationRequest();
            buildGoogleApiClient();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } else {

        }
    }

    public boolean checkGooglePlayServices(){
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this.context);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    public synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        try {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
        catch (SecurityException e) {

        }
    }

    public void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        sendLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.reconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Toast.makeText(getApplicationContext(), R.string.error_gps_disabled, Toast.LENGTH_LONG).show();
        //mGoogleApiClient.reconnect();
    }

    public void disconnectFromGoogleApi() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void sendLocation() {
        if (mLastLocation != null) {
            final String latitude = "" + mLastLocation.getLatitude();
            final String longitude = "" + mLastLocation.getLongitude();

            Log.d("LALA TIME", "" + updates);
            Log.d("LALA LAT", latitude);
            Log.d("LALA LON", longitude);

            if (this.updates == 5) {
                this.serviceHandler.stop();
            }

            this.updates++;
        }
    }

}
