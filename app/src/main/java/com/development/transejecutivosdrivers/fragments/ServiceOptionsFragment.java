package com.development.transejecutivosdrivers.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.background_services.AlarmReceiver;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class ServiceOptionsFragment extends FragmentBase  {
    View buttons_container;
    View finish_form;
    View progressBar;
    Button btn_call_passenger;
    Button btn_onmyway;
    Button btn_on_source;
    Button btn_start_service;
    Button btn_finish_service;
    Button button_finish_tracing;
    EditText txtview_observations;

    public ServiceOptionsFragment() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static ServiceOptionsFragment newInstance(User user, Service service, Passenger passenger, Context context) {
        ServiceOptionsFragment fragment = new ServiceOptionsFragment();
        fragment.setUser(user);
        fragment.setService(service);
        fragment.setPassenger(passenger);
        fragment.setContext(context);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_options_fragment, container, false);

        progressBar = view.findViewById(R.id.service_option_progress);
        buttons_container = view.findViewById(R.id.options_container);
        finish_form = view.findViewById(R.id.finish_form);

        btn_call_passenger = (Button) view.findViewById(R.id.btn_call_passenger);
        btn_onmyway = (Button) view.findViewById(R.id.btn_onmyway);
        btn_on_source = (Button) view.findViewById(R.id.btn_on_source);
        btn_start_service = (Button) view.findViewById(R.id.btn_start_service);
        btn_finish_service = (Button) view.findViewById(R.id.btn_finish_service);

        button_finish_tracing = (Button) view.findViewById(R.id.button_tracing);
        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);

        setOnClickListeners();
        disableButtons();
        return view;
    }

    private void setOnClickListeners() {
        btn_call_passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] tels = passenger.getPhone().split(",");
                String tel1 = tels[0];
                String tel2 = tels[1];

                if (!TextUtils.isEmpty(tel1)) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + tel1));
                    startActivity(callIntent);
                }
            }
        });

        btn_onmyway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmService();
            }
        });

        btn_on_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
                setOnSource();
            }
        });

        btn_start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });

        btn_finish_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlarm();
                showFinishForm();
            }
        });

    }

    private void confirmService() {
        showProgress(true, buttons_container, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_CONFIRM_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, buttons_container, progressBar);
                        validateResponse(response, "b1ha");
                        disableButtons();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, buttons_container, progressBar);
                        cancelAlarm();
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

    private void setOnSource() {
        showProgress(true, buttons_container, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_SET_ON_SOURCE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, buttons_container, progressBar);
                        validateResponse(response, "bls");
                        disableButtons();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, buttons_container, progressBar);
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

    private void startService() {
        showProgress(true, buttons_container, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_START_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, buttons_container, progressBar);
                        validateResponse(response, "pab");
                        disableButtons();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, buttons_container, progressBar);
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

    private void showFinishForm() {
        buttons_container.setVisibility(View.GONE);
        finish_form.setVisibility(View.VISIBLE);
        button_finish_tracing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finishService();
            }
        });
    }

    private void finishService() {
        showProgress(true, buttons_container, progressBar);

        final String observations = txtview_observations.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_FINISH_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, buttons_container, progressBar);
                        validateResponse(response, "st");
                        disableButtons();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, buttons_container, progressBar);
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
                params.put(JsonKeys.SERVICE_OBSERVATIONS, observations);

                return params;
            }

        };

        requestQueue.add(stringRequest);
    }


    private void disableButtons() {
        if (service.getOld() == 1 || TextUtils.isEmpty(service.getCd())) {
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (TextUtils.isEmpty(service.getB1ha())) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (TextUtils.isEmpty(service.getBls())) {
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (TextUtils.isEmpty(service.getPab())) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (TextUtils.isEmpty(service.getSt())) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public String subtractHourToStartDate(String date) {
        String[] time = date.split(" ");
        String[] parts = time[1].split(":");

        int hour = Integer.parseInt(parts[0]) - 1;

        String d = time[0] + " " + hour + ":" + parts[1];

        return d;
    }

    public boolean validateDates(String d1, String d2, String d3) {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            Date date1 = formatter.parse(d1);
            Date date2 = formatter.parse(d2);
            Date date3 = formatter.parse(d3);

            if (date1.compareTo(date2) > 0 && date1.compareTo(date3) < 0) {
                //date1 is Greater than date2 AND date1 is Less than date3
                return true;
            }
            return false;
        }
        catch (ParseException e1){
            e1.printStackTrace();
            return false;
        }
    }
}
