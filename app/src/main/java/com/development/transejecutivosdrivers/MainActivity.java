package com.development.transejecutivosdrivers;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.fragments.ServicesListFragment;
import com.development.transejecutivosdrivers.misc.CacheManager;
import com.mobapphome.mahandroidupdater.tools.MAHUpdaterController;

public class MainActivity extends ActivityBase {
    public ServicesListFragment servicesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validateSession();
        setFragment();

        MAHUpdaterController.init(this, ApiConstants.URL_APP_VERSION);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent i = new Intent(getApplicationContext(), ServiceActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLocationServiceEnabled();
        checkGooglePlayServices();
        CacheManager cacheManager = new CacheManager(getApplicationContext(), JsonKeys.SERVICE_PREF, JsonKeys.SERVICE_KEY);
        cacheManager.cleanData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MAHUpdaterController.end();
    }

    protected void setFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        servicesListFragment = new ServicesListFragment();
        servicesListFragment.setUser(user);
        fragmentTransaction.add(R.id.fragment_container, servicesListFragment, "Services List Fragment");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            session.logoutUser();
            return true;
        }
        else if (id == R.id.action_profile) {
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
            return true;
        }
        else if (id == R.id.action_dashboard) {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (servicesListFragment.getIdFragment() == 0) {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            finish();
        }
    }
}