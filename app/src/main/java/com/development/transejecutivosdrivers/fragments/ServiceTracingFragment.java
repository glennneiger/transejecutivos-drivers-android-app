package com.development.transejecutivosdrivers.fragments;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 31/03/2016.
 */
public class ServiceTracingFragment extends FragmentBase  {

    View formContainer;
    View progressBar;
    EditText txtview_observations;

    TextView txtview_start_time;
    TextView txtview_end_time;
    TextView txtview_service_reference;

    TextView txt_service_complete;
    TextView set_time_instructions;
    TextView service_instruction1;
    TextView service_instruction2;

    Button button_set_start_time;
    Button button_set_end_time;
    Button btn_reset_service;

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

        set_time_instructions = (TextView) view.findViewById(R.id.set_time_instructions);
        service_instruction1 = (TextView) view.findViewById(R.id.service_instruction1);
        service_instruction2 = (TextView) view.findViewById(R.id.service_instruction2);

        txt_service_complete = (TextView) view.findViewById(R.id.txt_service_complete);

        button_set_start_time = (Button) view.findViewById(R.id.button_set_start_time);
        button_set_end_time = (Button) view.findViewById(R.id.button_set_end_time);

        btn_reset_service = (Button) view.findViewById(R.id.btn_reset_service);

        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);

        imageView = (ImageView) view.findViewById(R.id.imgview_photo);

        button_take_photo = (Button) view.findViewById(R.id.button_take_photo);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setDataOnView();
        setOnClickListeners();
    }

    private void setDataOnView() {
        if (this.service != null && !TextUtils.isEmpty(this.service.getReference())) {
            txtview_service_reference.setText(this.service.getReference());
        }

        if (this.service != null && !TextUtils.isEmpty(this.service.getStartTime())) {
            txtview_start_time.setText(this.service.getStartTime());
            button_set_start_time.setVisibility(View.GONE);
        }

        if (this.service != null && !TextUtils.isEmpty(this.service.getEndTime())) {
            txtview_end_time.setText(this.service.getEndTime());
            button_set_end_time.setVisibility(View.GONE);
        }

        if (this.service != null && !TextUtils.isEmpty(this.service.getEndTime()) && !TextUtils.isEmpty(this.service.getStartTime())) {
            set_time_instructions.setVisibility(View.GONE);
            txt_service_complete.setVisibility(View.VISIBLE);
            btn_reset_service.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListeners() {
        final FragmentBase f = this;
        button_set_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(f.getActivity(), 2,  new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtview_start_time.setText(formatTime(hourOfDay) + ":" + formatTime(minute));
                        showTakePhotoButton();
                    }
                }, mHour, mMinute, true);

                tpd.show();
            }
        });

        button_set_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog tpd = new TimePickerDialog(f.getActivity(), 2,  new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        txtview_end_time.setText(formatTime(hourOfDay) + ":" + formatTime(minute));
                        showTakePhotoButton();
                    }
                }, mHour, mMinute, true);

                tpd.show();
            }
        });

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

        btn_reset_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetService();
            }
        });
    }

    private String formatTime(int number) {
        String h;
        if (number < 10) {
            h = "0" + number;
        }
        else {
            h = "" + number;
        }

        return h;
    }

    private void showTakePhotoButton() {
        if (!txtview_start_time.getText().equals("Establecer") && !txtview_end_time.getText().equals("Establecer")) {
            set_time_instructions.setVisibility(View.GONE);
            service_instruction1.setVisibility(View.VISIBLE);
            service_instruction2.setVisibility(View.VISIBLE);
            txtview_observations.setVisibility(View.VISIBLE);
            button_take_photo.setVisibility(View.VISIBLE);
        }
    }

    protected void resetService() {
        showProgress(true, formContainer, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_RESET_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, formContainer, progressBar);
                        validateResponse(response);
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

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    protected void  validateResponse(String response) {
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

    private void setTracing() {
        showProgress(true, formContainer, progressBar);

        start = (String) txtview_start_time.getText();
        end = (String) txtview_end_time.getText();

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
                params.put(JsonKeys.APP_VERSION, getString(R.string.prompt_app_version));

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    public void validateTracingResponse(String response) {
        showProgress(false, formContainer, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                Toast.makeText(getActivity(), getResources().getString(R.string.success_tracing_message), Toast.LENGTH_SHORT).show();
                reload();
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
