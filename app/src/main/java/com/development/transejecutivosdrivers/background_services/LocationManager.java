package com.development.transejecutivosdrivers.background_services;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.misc.RequestHandler;
import com.development.transejecutivosdrivers.models.Service;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.message;

/**
 * Created by william.montiel on 12/01/2017.
 */

public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private LocationSender sender = null;

    protected int idService;
    protected String apikey;
    protected String location;

    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest = null;
    protected int UPDATE_INTERVAL = 10000; // 10 sec
    protected int FATEST_INTERVAL = 8000; // 8000 sec
    protected int TOTAL_UPDATES = 10000;
    protected int DISPLACEMENT_METERS = 5;
    protected int UPDATES = 1;


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
        mLocationRequest.setNumUpdates(TOTAL_UPDATES);
        //mLocationRequest.setSmallestDisplacement(DISPLACEMENT_METERS);
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
        process();
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


    private void process() {
        if (mLastLocation != null) {
            final String latitude = "" + mLastLocation.getLatitude();
            final String longitude = "" + mLastLocation.getLongitude();

            Log.d("LALA TIME", "" + UPDATES);
            Log.d("LALA LAT", latitude);
            Log.d("LALA LON", longitude);

            sender = new LocationSender(latitude, longitude);
            sender.execute();

            if (UPDATES >= TOTAL_UPDATES) {
                this.stopLocationUpdates();
                this.disconnectFromGoogleApi();
                this.serviceHandler.stop();
            }

            UPDATES++;
        }
    }

    private class LocationSender extends AsyncTask<String, Void, Void> {
        //Handle error
        public String data, message = Const.CONST_MSG_ERROR_SERVER;
        public boolean errorBackground = true;
        private String lat, lng;

        public LocationSender(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... arg) {
            //Valiable de error
            Boolean error = false;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //Se tiene que Arreglar cuando se arregle el api
            params.add(new BasicNameValuePair(JsonKeys.LATITUDE, this.lat));
            params.add(new BasicNameValuePair(JsonKeys.LONGITUDE, this.lng));

            RequestHandler requestHandler = new RequestHandler();

            String response = requestHandler.makeServiceCall(getUrl(),requestHandler.POST, params, apikey);

            Log.d("LALA RES", response);

            if(response != null && response != ""){
                try {
                    JSONObject resObj = new JSONObject(response);
                    error = (Boolean) resObj.get(JsonKeys.ERROR);
                    String msg = resObj.getString(JsonKeys.MESSAGE);
                    int ms = resObj.getInt(JsonKeys.MESSAGE);
                    if (!error) {
                        if (msg.equals("0") || ms == 0) {
                            stopLocationUpdates();
                            disconnectFromGoogleApi();
                            serviceHandler.stop();
                        }
                    }
                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }else{
                errorBackground = true;
                message = Const.CONST_MSG_ERROR_SERVER;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            if(errorBackground) {

            } else {

            }
        }

        protected String getUrl() {
            String url = "";
            if (location != null && location.equals(JsonKeys.PRELOCATION)) {
                url = ApiConstants.URL_SET_PRELOCATION;
            }
            else if (location != null && location.equals(JsonKeys.ONSERVICE)) {
                url = ApiConstants.URL_SET_LOCATION;
            }
            url = url + "/" + idService;

            Log.d("LALA URL", url);

            return  url;
        }
    }

}
