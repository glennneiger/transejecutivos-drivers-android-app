package com.development.transejecutivosdrivers.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.MainActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.adapters.ServiceMenuAdapter;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.holders.ServiceHolder;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.ServiceMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceFragment extends FragmentBase implements AdapterView.OnItemClickListener{
    View fragmentContainer;
    View progressBar;
    GridView gridView;
    ServiceMenuAdapter adaptador;

    public ServiceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_fragment, container, false);

        fragmentContainer = view.findViewById(R.id.service_container);
        progressBar = view.findViewById(R.id.service_progress);

        //gridView = (GridView) view.findViewById(R.id.gridView_service_menu);
        //adaptador = new ServiceMenuAdapter(getActivity());
        //gridView.setAdapter(adaptador);

        //gridView.setOnItemClickListener(this);

        getService();

        return view;
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
                    setErrorSnackBar(getResources().getString(R.string.service_not_found_error));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ServiceMenu item = (ServiceMenu) parent.getItemAtPosition(position);
        switch (item.getId()) {
            case 0:

                break;

            case 1:

                break;

            default:
                break;
        }

    }
}
