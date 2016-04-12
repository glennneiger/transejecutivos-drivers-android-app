package com.development.transejecutivosdrivers.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.DashboardActivity;
import com.development.transejecutivosdrivers.MainActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 31/03/2016.
 */
public class ServiceTracingFragment extends FragmentBase  {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;

    View formContainer;
    View progressBar;
    Button button_tracing;
    TextInputLayout inputLayoutStart;
    TextInputLayout inputLayoutEnd;
    TimePicker timepicker_start;
    TimePicker timepicker_end;
    EditText txtview_observations;
    ImageView imageView;
    Button button_take_photo;
    String image;

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
        button_tracing = (Button) view.findViewById(R.id.button_finish_tracing);

        formContainer = view.findViewById(R.id.tracing_form);
        progressBar = view.findViewById(R.id.tracing_progress);

        inputLayoutStart = (TextInputLayout) view.findViewById(R.id.txt_input_start);
        inputLayoutEnd = (TextInputLayout) view.findViewById(R.id.txt_input_end);

        timepicker_start = (TimePicker) view.findViewById(R.id.timepicker_start);
        timepicker_start.setIs24HourView(true);

        timepicker_end = (TimePicker) view.findViewById(R.id.timepicker_end);
        timepicker_end.setIs24HourView(true);

        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);

        imageView = (ImageView) view.findViewById(R.id.imgview_photo);

        button_take_photo = (Button) view.findViewById(R.id.button_take_photo);


        setOnClickListeners();

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap

                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);

                image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                imageView.setImageBitmap(bitmap);

                button_tracing.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setOnClickListeners() {
        button_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        if (this.service.getIdTrace() == 0 && this.service.getOld() == 1) {
            button_tracing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setTracing();
                }
            });
        }
        else {
            button_tracing.setEnabled(false);
            button_tracing.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void setTracing() {
        showProgress(true, formContainer, progressBar);
        // Reset errors.
        inputLayoutStart.setError(null);
        inputLayoutEnd.setError(null);

        // Store values at the time of the login attempt.
        final String start = timepicker_start.getCurrentHour() + ":" + timepicker_start.getCurrentMinute();
        final String end = timepicker_end.getCurrentHour() + ":" + timepicker_end.getCurrentMinute();
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

        requestQueue.add(stringRequest);
    }

    public void validateTracingResponse(String response) {
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                setSuccesSnackBar(getResources().getString(R.string.success_tracing_message));
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
            else {
                setErrorSnackBar(getResources().getString(R.string.error_tracing_message));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
}
