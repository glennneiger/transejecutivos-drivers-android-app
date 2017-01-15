package com.development.transejecutivosdrivers.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.misc.CacheManager;
import com.development.transejecutivosdrivers.misc.RequestHandler;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    Button btn_reset_service;
    EditText txtview_observations;

    View service_complete_container;
    TextView txt_service_start;
    TextView txt_service_end;
    ImageView imgview_service_map;

    FinishService finishService = null;

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
                final String observations = txtview_observations.getText().toString();
                finishService = new FinishService(observations);
                finishService.execute();
            }
        });
    }

    private void finishService() {
        showProgress(true, finish_form, progressBar);

        final String observations = txtview_observations.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

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
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, finish_form, progressBar);
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
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
                }
                else if (btn.equals("bls")) {
                    //cancelAlarm();
                }
                else if (btn.equals("pab")) {
                    //cancelAlarm();
                    scheduleAlarm(JsonKeys.ONSERVICE);
                }
                else if (btn.equals("st")) {
                    cancelAlarm();
                }

                reload();
            }
            else {
                cancelAlarm();
                setErrorSnackBar(msg);
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void disableButtons() {
        if (service!= null && (service != null && service.getOld() == 1) || TextUtils.isEmpty(service.getCd())) {
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (service != null &&service.getB1haStatus() == 0) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (service != null && TextUtils.isEmpty(service.getB1ha()) && service.getB1haStatus() == 1) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (service != null && TextUtils.isEmpty(service.getBls())) {
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (service != null && TextUtils.isEmpty(service.getPab())) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else if (service != null && TextUtils.isEmpty(service.getSt())) {
            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else {
            showServiceResume();
            btn_reset_service.setVisibility(View.VISIBLE);

            btn_on_source.setEnabled(false);
            btn_on_source.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_onmyway.setEnabled(false);
            btn_onmyway.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_start_service.setEnabled(false);
            btn_start_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            btn_finish_service.setEnabled(false);
            btn_finish_service.setBackgroundColor(getResources().getColor(R.color.colorAccent));

            btn_on_source.setVisibility(View.GONE);
            btn_onmyway.setVisibility(View.GONE);
            btn_start_service.setVisibility(View.GONE);
            btn_finish_service.setVisibility(View.GONE);
        }
    }

    private void showServiceResume() {
        txt_service_start.setText(getResources().getString(R.string.pab_time) + " " + service.getPabTime());
        txt_service_end.setText(getResources().getString(R.string.st_time) + " " + service.getStTime());

        String durl = ApiConstants.URL_IMAGE_MAP + service.getReference() + ".dd";

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

            Log.d("LALA RES", response);

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
