package com.development.transejecutivosdrivers.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.MainActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 31/03/2016.
 */
public class ServiceTracingFragment extends FragmentBase  {

    View formContainer;
    View progressBar;
    TextInputLayout inputLayoutStart;
    TextInputLayout inputLayoutEnd;
    TimePicker timepicker_start;
    TimePicker timepicker_end;
    EditText txtview_observations;

    TextView txtview_start_time;
    TextView txtview_end_time;
    TextView txtview_service_reference;

    TextView prompt_start_time_service;
    TextView prompt_end_time_service;

    String start = "";
    String end = "";

    public ServiceTracingFragment() {

    }

    public static ServiceTracingFragment newInstance(User user, Service service) {
        ServiceTracingFragment fragment = new ServiceTracingFragment();
        fragment.setUser(user);
        fragment.setService(service);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_tracing_fragment, container, false);
        button_finish_tracing = (Button) view.findViewById(R.id.button_finish_tracing);

        formContainer = view.findViewById(R.id.tracing_form);
        progressBar = view.findViewById(R.id.tracing_progress);

        txtview_start_time = (TextView) view.findViewById(R.id.txtview_start_time);
        txtview_end_time = (TextView) view.findViewById(R.id.txtview_end_time);
        txtview_service_reference = (TextView) view.findViewById(R.id.txtview_service_reference);

        prompt_start_time_service = (TextView) view.findViewById(R.id.prompt_start_time_service);
        prompt_end_time_service = (TextView) view.findViewById(R.id.prompt_end_time_service);

        inputLayoutStart = (TextInputLayout) view.findViewById(R.id.txt_input_start);
        inputLayoutEnd = (TextInputLayout) view.findViewById(R.id.txt_input_end);

        timepicker_start = (TimePicker) view.findViewById(R.id.timepicker_start);
        timepicker_start.setIs24HourView(true);

        timepicker_end = (TimePicker) view.findViewById(R.id.timepicker_end);
        timepicker_end.setIs24HourView(true);

        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);

        imageView = (ImageView) view.findViewById(R.id.imgview_photo);

        button_take_photo = (Button) view.findViewById(R.id.button_take_photo);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ServiceActivity serviceActivity = (ServiceActivity) getActivity();
        setService(serviceActivity.getServiceData());
        setDataOnView();
        setOnClickListeners();
    }

    private void setDataOnView() {
        if (!TextUtils.isEmpty(this.service.getReference())) {
            txtview_service_reference.setText("Referencia: " + this.service.getReference());
        }

        if (!TextUtils.isEmpty(this.service.getStartTime())) {
            txtview_start_time.setText("Hora de Inicio: " + this.service.getStartTime());
            timepicker_start.setVisibility(View.GONE);
            prompt_start_time_service.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(this.service.getEndTime())) {
            txtview_end_time.setText("Hora de Fin: " + this.service.getEndTime());
            timepicker_start.setVisibility(View.GONE);
            prompt_end_time_service.setVisibility(View.GONE);
        }
    }

    private void setOnClickListeners() {
        button_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera(v);
            }
        });

        if ((this.service.getIdTrace() == 0 || TextUtils.isEmpty(this.service.getStartTime()) || TextUtils.isEmpty(this.service.getEndTime())) && this.service.getOld() == 1) {
            button_finish_tracing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTracing();
                }
            });
        }
        else {
            button_finish_tracing.setEnabled(false);
            button_finish_tracing.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void setTracing() {
        showProgress(true, formContainer, progressBar);

        // Reset errors.
        inputLayoutStart.setError(null);
        inputLayoutEnd.setError(null);

        // Store values at the time of the login attempt.
        if (TextUtils.isEmpty(this.service.getStartTime())) {
            start = timepicker_start.getCurrentHour() + ":" + timepicker_start.getCurrentMinute();
        }

        if (TextUtils.isEmpty(this.service.getEndTime())) {
            end = timepicker_end.getCurrentHour() + ":" + timepicker_end.getCurrentMinute();
        }

        final String observations = txtview_observations.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_TRACE_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validateTracingResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, formContainer, progressBar);
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
                params.put(JsonKeys.TRACING_START, start);
                params.put(JsonKeys.TRACING_END, end);
                params.put(JsonKeys.TRACING_OBSERVATIONS, observations);
                params.put(JsonKeys.TRACING_IMAGE, image);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        requestQueue.add(stringRequest);
    }

    public void validateTracingResponse(String response) {
        Log.d("lala", response);

        showProgress(false, formContainer, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                Toast.makeText(getActivity(), getResources().getString(R.string.success_tracing_message), Toast.LENGTH_SHORT).show();
                //setSuccesSnackBar(getResources().getString(R.string.success_tracing_message));
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
            else {
                String msg = resObj.getString(JsonKeys.MESSAGE);
                setErrorSnackBar(msg);
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
