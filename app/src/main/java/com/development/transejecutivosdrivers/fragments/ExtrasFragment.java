package com.development.transejecutivosdrivers.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

/**
 * Created by william.montiel on 27/07/2016.
 */
public class ExtrasFragment extends FragmentBase {View fragmentContainer;
    View progressBar;
    boolean for_search = false;
    Button button_call_passenger;
    Button button_sms_passenger;

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
