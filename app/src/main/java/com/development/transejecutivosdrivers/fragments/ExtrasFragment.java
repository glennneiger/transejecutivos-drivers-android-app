package com.development.transejecutivosdrivers.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.misc.VolleyErrorHandler;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;
import com.google.android.gms.location.internal.LocationRequestUpdateData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by william.montiel on 27/07/2016.
 */
public class ExtrasFragment extends FragmentBase {
    View service_option_progress;
    LinearLayout extra_options_container;
    View no_show_form;
    Button btn_call_passenger;
    Button btn_no_show;
    Button button_take_photo;
    TextInputLayout inputLayoutObservations;
    EditText txtview_observations;

    public ExtrasFragment() {

    }

    public static ExtrasFragment newInstance(User user, Service service, Passenger passenger) {
        ExtrasFragment fragment = new ExtrasFragment();
        fragment.setUser(user);
        fragment.setService(service);
        fragment.setPassenger(passenger);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.id = 4;
        view = inflater.inflate(R.layout.extras_fragment, container, false);

        service_option_progress = view.findViewById(R.id.extra_service_progress);
        extra_options_container = (LinearLayout) view.findViewById(R.id.extra_options_container);
        no_show_form = view.findViewById(R.id.no_show_form);
        btn_call_passenger = (Button) view.findViewById(R.id.btn_call_passenger);
        btn_no_show = (Button) view.findViewById(R.id.btn_no_show);
        button_take_photo = (Button) view.findViewById(R.id.button_take_photo);
        button_finish_tracing = (Button) view.findViewById(R.id.button_finish_no_show);
        inputLayoutObservations  = (TextInputLayout) view.findViewById(R.id.txt_input_layout_observations);
        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);
        imageView = (ImageView) view.findViewById(R.id.imgview_photo);

        setSupportPhones();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setOnClickListeners();
    }

    public void setOnClickListeners() {
        btn_call_passenger.setOnClickListener(new View.OnClickListener() {
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

        btn_no_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertDialog();
                //showNoShowForm();
            }
        });

        if (!TextUtils.isEmpty(service.getB1ha()) && !TextUtils.isEmpty(service.getBls()) && !TextUtils.isEmpty(service.getPab()) && !TextUtils.isEmpty(service.getSt())) {
            btn_no_show.setEnabled(false);
            btn_no_show.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void showNoShowForm() {
        extra_options_container.setVisibility(View.GONE);
        no_show_form.setVisibility(View.VISIBLE);

        button_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        button_finish_tracing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noShow();
            }
        });
    }

    private void noShow() {
        showProgress(true, no_show_form, service_option_progress);

        inputLayoutObservations.setError(null);

        final String observations = txtview_observations.getText().toString();

        if (TextUtils.isEmpty(observations)) {
            showProgress(false, no_show_form, service_option_progress);
            inputLayoutObservations.setError(getString(R.string.error_empty_observations));
            inputLayoutObservations.requestFocus();
            setErrorSnackBar(getResources().getString(R.string.error_empty_observations));
        } else {
            Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    ApiConstants.URL_TRACE_SERVICE + "/" + this.service.getIdService(),
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            validateResponse(response);
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
                                showProgress(false, layout, progressBar);
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
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put(JsonKeys.TRACING_START, "0");
                    params.put(JsonKeys.TRACING_END, "0");
                    params.put(JsonKeys.TRACING_OBSERVATIONS, observations);
                    params.put(JsonKeys.TRACING_IMAGE, image);
                    params.put(JsonKeys.APP_VERSION, getString(R.string.prompt_app_version));

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mRequestQueue.add(stringRequest);
        }
    }

    private void setSupportPhones() {
        showProgress(true, extra_options_container, service_option_progress);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_SUPPORT_PHONE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validate(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, extra_options_container, service_option_progress);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", user.getApikey());
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void validate(String response) {
        showProgress(false, extra_options_container, service_option_progress);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 5, 0, 5);

        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                JSONArray res = resObj.getJSONArray(JsonKeys.RESPONSE);
                for (int i = 0; i < res.length(); i++) {
                    final String phone = res.getString(i);

                    if (!TextUtils.isEmpty(phone)) {
                        int b = i+1;
                        Button button = new Button(getActivity());
                        button.setId(i);
                        button.setBackgroundColor(getResources().getColor(R.color.green));
                        button.setText(getString(R.string.prompt_call_to_support) + " " + b);
                        button.setTextColor(getResources().getColor(R.color.colorPrimaryTextLight));
                        //button.setAllCaps(false);
                        button.setTextSize(13);
                        button.setLayoutParams(params);

                        extra_options_container.addView(button);

                        final int id_ = button.getId();
                        Button btn = ((Button) view.findViewById(id_));
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + phone));
                                startActivity(callIntent);
                            }
                        });
                    }
                }
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void validateResponse(String response) {
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            String msg = resObj.getString(JsonKeys.MESSAGE);
            if (!error) {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                reload();
            }
            else {
                setErrorSnackBar(msg);
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Crea un diálogo de alerta sencillo
     * @return Nuevo diálogo
     */
    public void createAlertDialog () {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(getString(R.string.prompt_no_show_confirm));

        alertDialogBuilder.setPositiveButton(getString(R.string.prompt_no_show_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                showNoShowForm();
            }
        });

        alertDialogBuilder.setNegativeButton(R.string.prompt_no_show_no,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
