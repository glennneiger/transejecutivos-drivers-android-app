package com.development.transejecutivosdrivers.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.models.User;

/**
 * Created by william.montiel on 28/03/2016.
 */
public class ServicesListFragment extends FragmentBase {
    public ServicesListFragment() {

    }

    public static ServicesListFragment newInstance(User user) {
        ServicesListFragment fragment = new ServicesListFragment();
        fragment.setUser(user);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.service_list_fragment, container, false);

        layout = view.findViewById(R.id.service_expandable_listview);

        expandableListView = (ExpandableListView) view.findViewById(R.id.service_expandable_listview);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);


        progressBar = view.findViewById(R.id.service_progress);

        searchPendingServices();

        return view;
    }
}
