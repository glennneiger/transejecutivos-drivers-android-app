package com.development.transejecutivosdrivers.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import com.development.transejecutivosdrivers.fragments.ExtrasFragment;
import com.development.transejecutivosdrivers.fragments.ServiceFragment;
import com.development.transejecutivosdrivers.fragments.ServiceOptionsFragment;
import com.development.transejecutivosdrivers.fragments.ServiceTracingFragment;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import java.util.ArrayList;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    int tabCount;
    Context context;
    User user;
    Service service;
    Passenger passenger;
    ArrayList<Passenger> passengers = new ArrayList<Passenger>();

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs, Context context, User user, Service service, ArrayList<Passenger> passengers) {
        super(fm);
        this.tabCount = numberOfTabs;
        this.context = context;
        this.user = user;
        this.service = service;
        this.passengers = passengers;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ServiceFragment serviceFragment = ServiceFragment.newInstance(this.user, this.service, this.passengers);
                return serviceFragment;
            case 1:
                if (this.service.getOld() == 1) {
                    ServiceTracingFragment fragment = ServiceTracingFragment.newInstance(this.user, this.service);
                    return fragment;
                }
                else {
                    ServiceOptionsFragment fragment = ServiceOptionsFragment.newInstance(this.user, this.service, this.passengers, this.context);
                    return fragment;
                }

            case 2:
                ExtrasFragment extrasFragment = ExtrasFragment.newInstance(this.user, this.service, this.passengers);
                return extrasFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }


}
