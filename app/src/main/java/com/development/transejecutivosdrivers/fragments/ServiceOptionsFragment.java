package com.development.transejecutivosdrivers.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.DashboardActivity;
import com.development.transejecutivosdrivers.Manifest;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.misc.CacheManager;
import com.development.transejecutivosdrivers.misc.DialogCreator;
import com.development.transejecutivosdrivers.misc.RequestHandler;
import com.development.transejecutivosdrivers.misc.VolleyErrorHandler;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class ServiceOptionsFragment extends FragmentBase  {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    View buttons_container;
    View finish_form;
    View progressBar;
    Button btn_call_passenger;
    Button btn_onmyway;
    Button btn_on_source;
    Button btn_start_service;
    Button btn_finish_service;
    Button btn_reset_service;
    EditText txtview_observations;

    View service_complete_container;
    TextView txt_service_start;
    TextView txt_service_end;
    TextView txt_service_trace_observations;
    ImageView imgview_service_map;

    FinishService finishService = null;

    RequestQueue mRequestQueue;

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
        this.id = 3;
        view = inflater.inflate(R.layout.service_options_fragment, container, false);

        progressBar = view.findViewById(R.id.service_option_progress);
        buttons_container = view.findViewById(R.id.options_container);
        finish_form = view.findViewById(R.id.finish_form);

        btn_call_passenger = (Button) view.findViewById(R.id.btn_call_passenger);

        service_complete_container = view.findViewById(R.id.service_complete_container);
        txt_service_start = (TextView) view.findViewById(R.id.txt_service_start);
        txt_service_end = (TextView) view.findViewById(R.id.txt_service_end);
        txt_service_trace_observations = (TextView) view.findViewById(R.id.txt_service_trace_observations);
        imgview_service_map = (ImageView) view.findViewById(R.id.imgview_service_map);

        btn_onmyway = (Button) view.findViewById(R.id.btn_onmyway);
        btn_on_source = (Button) view.findViewById(R.id.btn_on_source);
        btn_start_service = (Button) view.findViewById(R.id.btn_start_service);
        btn_finish_service = (Button) view.findViewById(R.id.btn_finish_service);

        btn_reset_service = (Button) view.findViewById(R.id.btn_reset_service);

        button_finish_tracing = (Button) view.findViewById(R.id.button_finish_tracing);
        button_take_photo = (Button) view.findViewById(R.id.button_take_photo);
        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);
        imageView = (ImageView) view.findViewById(R.id.imgview_photo);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setOnClickListeners();
        disableButtons();
    }

    private void setOnClickListeners() {
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

        btn_onmyway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmService();
            }
        });

        btn_on_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                showFinishForm();
            }
        });

        btn_reset_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetService();
            }
        });
    }

    protected void resetService() {
        showProgress(true, buttons_container, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_RESET_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, buttons_container, progressBar);
                        validateResponse(response, "reset");
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
                            showProgress(false, buttons_container, progressBar);
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

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void setServiceStatus(final String url, final String status) {
        showProgress(true, buttons_container, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, buttons_container, progressBar);
                        validateResponse(response, status);
                        disableButtons();
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
                            showProgress(false, buttons_container, progressBar);
                            cancelAlarm();
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
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private void confirmService() {
        setServiceStatus(ApiConstants.URL_CONFIRM_SERVICE + "/" + this.service.getIdService(), "b1ha");
    }

    private void setOnSource() {
        setServiceStatus(ApiConstants.URL_SET_ON_SOURCE + "/" + this.service.getIdService(), "bls");
    }

    private void startService() {
        setServiceStatus(ApiConstants.URL_START_SERVICE + "/" + this.service.getIdService(), "pab");
    }

    private void showFinishForm() {
        buttons_container.setVisibility(View.GONE);
        finish_form.setVisibility(View.VISIBLE);

        button_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        button_finish_tracing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                final String observations = txtview_observations.getText().toString();
                finishService = new FinishService(observations);
                finishService.execute();
                */
                finishService();
            }
        });
    }

    private void finishService() {
        showProgress(true, finish_form, progressBar);

        final String observations = txtview_observations.getText().toString();

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_FINISH_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, finish_form, progressBar);
                        finish_form.setVisibility(View.GONE);
                        validateResponse(response, "st");
                        disableButtons();
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
                            showProgress(false, finish_form, progressBar);
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
                params.put(JsonKeys.SERVICE_OBSERVATIONS, observations);
                params.put(JsonKeys.TRACING_IMAGE, image);
                params.put(JsonKeys.APP_VERSION, getString(R.string.prompt_app_version));

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(30),
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(stringRequest);
    }

    protected void  validateResponse(String response, String btn) {
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            String msg = resObj.getString(JsonKeys.MESSAGE);
            if (!error) {
                Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show();

                if (btn.equals("b1ha")) {
                    scheduleAlarm(JsonKeys.PRELOCATION);
                    String message = getResources().getString(R.string.confirm_service_sms_message);
                    message = message.replace("[ADDRESS]", this.service.getSource());
                    this.askForSendSMS(message);
                }
                else if (btn.equals("bls")) {
                    cancelAlarm();

                    String message = getResources().getString(R.string.on_source_service_sms_message);
                    message = message.replace("[DRIVER_NAME]", this.user.getName() + " " + this.user.getLastName());
                    message = message.replace("[PASSENGER_NAME]", this.passenger.getName() + " " + this.passenger.getLastName());
                    message = message.replace("[LICENSE_PLATE]", this.service.getLicensePlate());
                    message = message.replace("[DRIVER_PHONE1]", this.user.getPhone1());

                    this.askForSendSMS(message);
                }
                else if (btn.equals("pab")) {
                    scheduleAlarm(JsonKeys.ONSERVICE);
                    reload();
                }
                else if (btn.equals("st")) {
                    cancelAlarm();
                    reload();
                }
            }
            else {
                //cancelAlarm();
                setErrorSnackBar(msg);
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void askForSendSMS(final String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(getResources().getString(R.string.service_send_sms_message));
        dialog.setPositiveButton(getResources().getString(R.string.button_send_sms_service_modal_prompt), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            sendSMS(message);
            reload();
            }
        });
        dialog.setNegativeButton(getResources().getString(R.string.button_not_send_sms_service_modal_prompt),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        reload();
                    }
                }
        );
        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void sendSMS(String message) {
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(android.support.v4.app.ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                if (!TextUtils.isEmpty(this.passenger.getPhone())) {
                    SmsManager smsManager = SmsManager.getDefault();

                    message = Normalizer.normalize(message, Normalizer.Form.NFD)
                            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                    message = Normalizer.normalize(message, Normalizer.Form.NFD);
                    message = message.replaceAll("[^\\p{ASCII}]", "");

                    smsManager.sendTextMessage(this.passenger.getPhone(), null, message, null, null);

                    Toast.makeText(context, getResources().getString(R.string.sent_passenger_sms_message),Toast.LENGTH_LONG).show();
                }
            } else {
                android.support.v4.app.ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{android.Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            if (!TextUtils.isEmpty(this.passenger.getPhone())) {
                SmsManager smsManager = SmsManager.getDefault();

                message = Normalizer.normalize(message, Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

                message = Normalizer.normalize(message, Normalizer.Form.NFD);
                message = message.replaceAll("[^\\p{ASCII}]", "");

                smsManager.sendTextMessage(this.passenger.getPhone(), null, message, null, null);

                Toast.makeText(context, getResources().getString(R.string.sent_passenger_sms_message),Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //finish();
                    //startActivity(getIntent());
                } else {
                    DialogCreator dialogCreator = new DialogCreator(context);
                    dialogCreator.createCustomDialog(getString(R.string.cancel_permission_sms), "ACEPTAR");
                }
                break;
        }
    }

    private void disableButtons() {
        if (service == null) {
            hideAllButtons();
        }
        else if (service.getOld() == 1 || TextUtils.isEmpty(service.getCd()) || service.getB1haStatus() == 0) {

        }
        else if (TextUtils.isEmpty(service.getB1ha()) && service.getB1haStatus() == 1) {
            btn_onmyway.setEnabled(true);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (TextUtils.isEmpty(service.getBls())) {
            btn_on_source.setEnabled(true);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (TextUtils.isEmpty(service.getPab())) {
            btn_start_service.setEnabled(true);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (TextUtils.isEmpty(service.getSt())) {
            btn_finish_service.setEnabled(true);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.green));
        }
        else {
            hideAllButtons();
        }
    }

    private void hideAllButtons() {
        showServiceSummary();
        btn_reset_service.setVisibility(View.VISIBLE);

        btn_on_source.setVisibility(View.GONE);
        btn_onmyway.setVisibility(View.GONE);
        btn_start_service.setVisibility(View.GONE);
        btn_finish_service.setVisibility(View.GONE);
    }

    private void showServiceSummary() {
        txt_service_start.setText(service.getStartTime());
        txt_service_end.setText(service.getEndTime());
        txt_service_trace_observations.setText(service.getTraceObservations());

        String durl = service.getUrlMap();

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        ImageRequest drequest = new ImageRequest(durl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imgview_service_map.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imgview_service_map.setImageResource(R.drawable.image_not_found);
                    }
                });

        requestQueue.add(drequest);

        service_complete_container.setVisibility(View.VISIBLE);
    }

    private class FinishService extends AsyncTask<String, Void, Void> {
        public String data, message = Const.CONST_MSG_ERROR_SERVER;
        public boolean errorBackground = true;
        private String observations;

        public FinishService(String observations) {
            this.observations = observations;
        }

        protected void onPreExecute(){
            super.onPreExecute();
            showProgress(true, finish_form, progressBar);
        }
        @Override
        protected Void doInBackground(String... arg) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(JsonKeys.SERVICE_OBSERVATIONS, this.observations));
            params.add(new BasicNameValuePair(JsonKeys.TRACING_IMAGE, image));
            params.add(new BasicNameValuePair(JsonKeys.APP_VERSION, getString(R.string.prompt_app_version)));

            RequestHandler requestHandler = new RequestHandler();

            String response = requestHandler.makeServiceCall(ApiConstants.URL_FINISH_SERVICE + "/" + service.getIdService(),requestHandler.POST, params, user.getApikey());

            if(response != null && response != ""){
                this.validateResponse(response);
            } else {
                errorBackground = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            showProgress(false, finish_form, progressBar);
            disableButtons();

            Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

            if (!errorBackground) {
                reload();
            }
        }

        protected void validateResponse(String response) {
            try {
                JSONObject resObj = new JSONObject(response);
                Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
                String msg = resObj.getString(JsonKeys.MESSAGE);
                if (!error) {
                    errorBackground = false;
                    message = msg;
                }
                else {
                    errorBackground = true;
                }
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }
}
