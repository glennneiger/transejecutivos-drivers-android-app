package com.development.transejecutivosdrivers.background_services;

import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.google.android.gms.location.LocationServices;
import java.util.HashMap;
import java.util.Map;


public class PrelocationService extends IntentServiceBase {

    public void getLocation() {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                Log.d("Lat", "" + mLastLocation.getLatitude());
                Log.d("Lon", "" + mLastLocation.getLongitude());

                final String latitude = "" + mLastLocation.getLatitude();
                final String longitude = "" + mLastLocation.getLongitude();
                String url = "";

                if (location != null && location.equals(JsonKeys.PRELOCATION)) {
                    url = ApiConstants.URL_SET_PRELOCATION;
                }
                else if (location != null && location.equals(JsonKeys.ONSERVICE)) {
                    url = ApiConstants.URL_SET_LOCATION;
                }

                Log.d("PRELOCATION", url);

                if (this.isDataComplete) {
                    RequestQueue requestQueue = Volley.newRequestQueue(this.context);

                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            url + "/" + idService,
                            new com.android.volley.Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    validateResponse(response);
                                }
                            },
                            new com.android.volley.Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), R.string.error_location,
                                            Toast.LENGTH_LONG).show();
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

                    requestQueue.add(stringRequest);
                }
            }
            else {
                mGoogleApiClient.connect();
            }
        }
        catch (SecurityException e) {

        }
    }

    public void validateResponse(String response) {

    }
}