package com.development.transejecutivosdrivers.background_services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 04/04/2016.
 */
public class BackgroundService extends Service {
    protected Context context;

    LocationManager locationManager = null;

    public BackgroundService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle t = intent.getExtras();
        if (t != null) {
            locationManager = new LocationManager();
            locationManager.setContext(this);
            locationManager.setBackgroundService(this);
            locationManager.setData(t.getInt(JsonKeys.SERVICE_ID), t.getString(JsonKeys.USER_APIKEY), t.getString(JsonKeys.LOCATION));
            locationManager.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void stopBackgroundProcess() {
        this.stopSelf();
    }

    @Override
    public void onDestroy() {
        if (locationManager != null) {
            locationManager.stopLocationUpdates();
            locationManager.disconnectFromGoogleApi();
        }
        super.onDestroy();
    }

    public class LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
        protected int idService;
        protected String apikey;
        protected String location;

        protected GoogleApiClient mGoogleApiClient = null;
        protected Location mLastLocation;
        protected LocationRequest mLocationRequest = null;
        private int REQUEST_CODE_RECOVER_PLAY_SERVICES = 200;
        protected int UPDATE_INTERVAL = 10000; // 10 sec
        protected int FATEST_INTERVAL = 5000; // 5 sec
        protected int DISPLACEMENT = 5;
        protected int NUMBER_UPDATES = 3;

        public int updates = 0;

        Context context;
        BackgroundService backgroundService;

        public void setData(int idService, String apikey, String location) {
            this.idService = idService;
            this.apikey = apikey;
            this.location = location;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public void setBackgroundService(BackgroundService backgroundService) {
            this.backgroundService = backgroundService;
        }

        public void start() {
            if (checkGooglePlayServices()) {
                createLocationRequest();
                buildGoogleApiClient();
                if (mGoogleApiClient != null) {
                    mGoogleApiClient.connect();
                }

            }
        }

        public boolean checkGooglePlayServices(){
            int checkGooglePlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context);
            if (checkGooglePlayServices != ConnectionResult.SUCCESS) {
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
            if (mLocationRequest == null) {
                mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(UPDATE_INTERVAL);
                mLocationRequest.setFastestInterval(FATEST_INTERVAL);
                mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
                //mLocationRequest.setNumUpdates(NUMBER_UPDATES);
            }
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                try {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    sendLocation();
                    startLocationUpdates();
                } catch (SecurityException e) {

                }
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getApplicationContext(), R.string.error_general, Toast.LENGTH_LONG).show();
        }


        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            sendLocation();
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

        public void disconnectFromGoogleApi() {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
        }

        public void stopLocationUpdates() {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }

        public void sendLocation() {
            try {
                if (mLastLocation != null) {
                    final String latitude = "" + mLastLocation.getLatitude();
                    final String longitude = "" + mLastLocation.getLongitude();
                    String url = "";

                    if (location != null && location.equals(JsonKeys.PRELOCATION)) {
                        url = ApiConstants.URL_SET_PRELOCATION;
                    }
                    else if (location != null && location.equals(JsonKeys.ONSERVICE)) {
                        url = ApiConstants.URL_SET_LOCATION;
                    }

                    RequestQueue requestQueue = Volley.newRequestQueue(this.context);

                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            url + "/" + idService,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    validateResponse(response, latitude, longitude);
                                }
                            },
                            new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), R.string.error_location,Toast.LENGTH_LONG).show();
                                    stopLocationUpdates();
                                    disconnectFromGoogleApi();
                                    backgroundService.stopBackgroundProcess();
                                }
                            }) {

                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String,String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/x-www-form-urlencoded");
                            headers.put("Authorization", apikey);
                            return headers;
                        }

                        @Override
                        protected Map<String,String> getParams(){
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("Content-Type", "application/x-www-form-urlencoded");
                            params.put(JsonKeys.LATITUDE, latitude);
                            params.put(JsonKeys.LONGITUDE, longitude);

                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                    requestQueue.add(stringRequest);
                }
                else {
                    mGoogleApiClient.connect();
                }
            }
            catch (SecurityException e) {

            }
        }

        public void validateResponse(String response, String la, String lo) {
            //Toast.makeText(getApplicationContext(), "Location sent: " + la + " " + lo,Toast.LENGTH_SHORT).show();
            try {
                JSONObject resObj = new JSONObject(response);
                Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
                String msg = resObj.getString(JsonKeys.MESSAGE);
                int ms = resObj.getInt(JsonKeys.MESSAGE);
                if (!error) {
                    if (msg.equals("0") || ms == 0) {
                        this.backgroundService.stopBackgroundProcess();
                    }
                }

            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }

            updates++;
        }
    }
}
