package com.development.transejecutivosdrivers.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.MainActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.holders.ServiceHolder;
import com.development.transejecutivosdrivers.misc.VolleyErrorHandler;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceFragment extends FragmentBase {
    View fragmentContainer;
    View progressBar;
    boolean for_search = false;
    Button button_call_passenger;
    Button button_sms_passenger;

    public ServiceFragment() {

    }

    public static ServiceFragment newInstance(User user, Service service, Passenger passenger) {
        ServiceFragment fragment = new ServiceFragment();
        fragment.setUser(user);
        fragment.setService(service);
        fragment.setPassenger(passenger);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.id = 1;
        view = inflater.inflate(R.layout.service_fragment, container, false);

        fragmentContainer = view.findViewById(R.id.service_container);
        progressBar = view.findViewById(R.id.service_progress);
        button_call_passenger = (Button) view.findViewById(R.id.button_call_passenger);
        button_sms_passenger = (Button) view.findViewById(R.id.button_sms_passenger);

        button_sms_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tels = passenger.getPhone().split(",");
                String tel1 = tels[0];

                if (!TextUtils.isEmpty(tel1)) {
                    /*
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", tel1);
                    startActivity(smsIntent);
                    */
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + tel1));
                    //intent.putExtra("sms_body", "");
                    startActivity(intent);
                }
            }
        });

        button_call_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tels = passenger.getPhone().split(",");
                String tel1 = tels[0];

                if (!TextUtils.isEmpty(tel1)) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + tel1));
                    startActivity(callIntent);
                }
            }
        });

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

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        if (!for_search) {
            ServiceActivity serviceActivity = (ServiceActivity) getActivity();
            setService(serviceActivity.getServiceData());
        }

        if (service != null && passenger != null) {
            ServiceHolder serviceHolder = new ServiceHolder(view, getActivity());
            serviceHolder.setService(service);
            serviceHolder.setPassenger(passenger);
        }

    }

    public void updateService(final int status) {
        if (this.service != null) {
            showProgress(true, fragmentContainer, progressBar);
            final String idService = "" + this.service.getIdService();
            Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();
            StringRequest stringRequest = new StringRequest(
                    Request.Method.PUT,
                    ApiConstants.URL_ACCEPT_SERVICE + "/" + idService,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            validateUpdateServiceResponse(response, status);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (isAdded()) {
                                VolleyErrorHandler voleyErrorHandler = new VolleyErrorHandler();
                                voleyErrorHandler.setVolleyError(error);
                                voleyErrorHandler.process();
                                String msg = voleyErrorHandler.getMessage();
                                String message = (TextUtils.isEmpty(msg) ? getResources().getString(R.string.server_error) : msg);

                                setErrorSnackBar(message);
                                showProgress(false, fragmentContainer, progressBar);
                            }
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
                    params.put(JsonKeys.SERVICE_STATUS, "" + status);

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mRequestQueue.add(stringRequest);
        }
    }

    public void validateUpdateServiceResponse(String response, int status) {
        showProgress(false, fragmentContainer, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                if (status == 0) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.service_decline_message), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.service_accept_message), Toast.LENGTH_SHORT).show();
                }

                reload();
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
