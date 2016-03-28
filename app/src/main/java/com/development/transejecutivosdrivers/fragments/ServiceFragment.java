package com.development.transejecutivosdrivers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.development.transejecutivosdrivers.R;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServiceFragment extends FragmentBase {
    View fragmentContainer;
    View progressBar;

    public ServiceFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_fragment, container, false);

        fragmentContainer = view.findViewById(R.id.service_container);
        progressBar = view.findViewById(R.id.service_progress);

        getService();

        return view;
    }

    public void getService() {
        showProgress(true, fragmentContainer, progressBar);
    }
}
