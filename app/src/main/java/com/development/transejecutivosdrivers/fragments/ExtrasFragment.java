package com.development.transejecutivosdrivers.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by william.montiel on 27/07/2016.
 */
public class ExtrasFragment extends FragmentBase {
    View service_option_progress;
    View extra_options_container;
    View no_show_form;
    Button btn_call_passenger;
    Button btn_no_show;
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
        view = inflater.inflate(R.layout.extras_fragment, container, false);

        service_option_progress = view.findViewById(R.id.extra_service_progress);
        extra_options_container = view.findViewById(R.id.extra_options_container);
        no_show_form = view.findViewById(R.id.extra_options_container);
        btn_call_passenger = (Button) view.findViewById(R.id.btn_call_passenger);
        btn_no_show = (Button) view.findViewById(R.id.btn_no_show);
        txtview_observations = (EditText) view.findViewById(R.id.txtview_observations);

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
                String tel2 = tels[1];

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
                showFinishForm();
            }
        });
    }

    private void showFinishForm() {
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

        final String observations = txtview_observations.getText().toString();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_FINISH_SERVICE + "/" + this.service.getIdService(),
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false, no_show_form, service_option_progress);
                        no_show_form.setVisibility(View.GONE);
                        validateResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, no_show_form, service_option_progress);
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

                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void validateResponse(String response) {

    }
}
