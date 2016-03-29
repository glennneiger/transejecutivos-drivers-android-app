package com.development.transejecutivosdrivers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.User;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class ServiceOptionsFragment extends FragmentBase  {
    View fragmentContainer;
    View progressBar;

    public ServiceOptionsFragment() {

    }

    public static ServiceOptionsFragment newInstance(User user, int idService) {
        ServiceOptionsFragment fragment = new ServiceOptionsFragment();
        fragment.setUser(user);
        fragment.setIdService(idService);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_options_fragment, container, false);

        return view;
    }
}
