package com.development.transejecutivosdrivers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.MainActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.holders.ServiceHolder;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceFragment extends FragmentBase {
    View fragmentContainer;
    View progressBar;

    public ServiceFragment() {

    }

    public static ServiceFragment newInstance(User user, int idService) {
        ServiceFragment fragment = new ServiceFragment();
        fragment.setUser(user);
        fragment.setIdService(idService);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_fragment, container, false);

        fragmentContainer = view.findViewById(R.id.service_container);
        progressBar = view.findViewById(R.id.service_progress);

        Button button_accept = (Button) view.findViewById(R.id.button_accept);
        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateService(1);
            }
        });

        Button button_decline = (Button) view.findViewById(R.id.button_decline);
        button_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateService(0);
            }
        });

        getService();

        return view;
    }

    public void updateService(final int status) {
        final String idService = "" + this.idService;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(
                Request.Method.PUT,
                ApiConstants.URL_UPDATE_STATUS_SERVICE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validateUpdateServiceResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, fragmentContainer, progressBar);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", user.getApikey());
                return headers;
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(JsonKeys.SERVICE_ID, idService);
                params.put(JsonKeys.SERVICE_STATUS, "" + status);

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void validateUpdateServiceResponse(String response) {
        showProgress(false, fragmentContainer, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
            else {
                setErrorSnackBar(getResources().getString(R.string.error_general));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void getService() {
        showProgress(true, fragmentContainer, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_GET_PENDING_SERVICE + "/" + this.idService,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validatePendingServiceResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, fragmentContainer, progressBar);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", user.getApikey());
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void validatePendingServiceResponse(String response) {
        showProgress(false, fragmentContainer, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                JSONObject data = (JSONObject) resObj.get(JsonKeys.SERVICE);
                int idService = (int) data.get(JsonKeys.SERVICE_ID);
                if (idService != 0) {
                    Deserializer deserializer = new Deserializer();
                    deserializer.setResponseJSONObject(data);
                    deserializer.deserializeOnePassengerAndService();

                    Service service = deserializer.getService();
                    Passenger passenger = deserializer.getPassenger();

                    ServiceHolder serviceHolder = new ServiceHolder(view, getActivity());
                    serviceHolder.setService(service);
                    serviceHolder.setPassenger(passenger);
                }
                else {
                    Toast.makeText(getActivity(), R.string.service_not_found_error, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                }
            }
            else {
                setErrorSnackBar(getResources().getString(R.string.error_general));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
