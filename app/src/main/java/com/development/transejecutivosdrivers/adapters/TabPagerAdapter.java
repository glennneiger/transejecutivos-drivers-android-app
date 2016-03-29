package com.development.transejecutivosdrivers.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.development.transejecutivosdrivers.fragments.ServiceFragment;
import com.development.transejecutivosdrivers.fragments.ServiceOptionsFragment;
import com.development.transejecutivosdrivers.models.User;

/**
 * Created by william.montiel on 29/03/2016.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {

    int tabCount;
    Context context;
    User user;
    int idService;

    public TabPagerAdapter(FragmentManager fm, int numberOfTabs, Context context, User user, int idService) {
        super(fm);
        this.tabCount = numberOfTabs;
        this.context = context;
        this.user = user;
        this.idService = idService;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ServiceFragment serviceFragment = ServiceFragment.newInstance(this.user, this.idService);
                return serviceFragment;
            case 1:
                ServiceOptionsFragment serviceOptionsFragment = ServiceOptionsFragment.newInstance(this.user, this.idService);
                return serviceOptionsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
