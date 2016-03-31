package com.development.transejecutivosdrivers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.User;

/**
 * Created by william.montiel on 31/03/2016.
 */
public class ServiceTracingFragment extends FragmentBase  {
    View fragmentContainer;
    View progressBar;
    Button button_tracing;
    TimePicker timepicker_start;
    TimePicker timepicker_end;

    public ServiceTracingFragment() {

    }

    public static ServiceTracingFragment newInstance(User user, int idService) {
        ServiceTracingFragment fragment = new ServiceTracingFragment();
        fragment.setUser(user);
        fragment.setIdService(idService);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_tracing_fragment, container, false);
        button_tracing = (Button) view.findViewById(R.id.button_tracing);

        timepicker_start = (TimePicker) view.findViewById(R.id.timepicker_start);
        timepicker_end = (TimePicker) view.findViewById(R.id.timepicker_end);

        button_tracing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTracing();
            }
        });

        return view;
    }

    private void setTracing() {

    }
}
