package com.development.transejecutivosdrivers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.adapters.TabPagerAdapter;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.holders.ServiceHolder;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServiceActivity extends ActivityBase {

    private TabLayout mainTabs;
    int idService = 0;
    int tab = 0;
    int old = 0;
    Service service;
    Passenger passenger;
    View main_pager;
    View progressBar;
    View service_activity_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validateSession();

        service_activity_layout = findViewById(R.id.service_activity_layout);
        main_pager = findViewById(R.id.main_pager);
        progressBar = findViewById(R.id.service_progress);

        Bundle t = getIntent().getExtras();
        if (t != null) {
            idService = t.getInt("idService");
            tab = t.getInt("tab");
            old = t.getInt("old");
        }

        getService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLocationServiceEnabled();
    }

    public void getService() {
        showProgress(true, main_pager, progressBar);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_GET_SERVICE + "/" + this.idService,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validateServiceResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(service_activity_layout, getResources().getString(R.string.error_general));
                        showProgress(false, main_pager, progressBar);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", user.getApikey());
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    private void validateServiceResponse(String response) {
        showProgress(false, main_pager, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                JSONObject data = (JSONObject) resObj.get(JsonKeys.SERVICE);
                int idService = (int) data.get(JsonKeys.SERVICE_ID);
                if (idService != 0) {
                    Deserializer deserializer = new Deserializer();
                    deserializer.setResponseJSONObject(data);
                    deserializer.deserializeOnePassengerAndService();

                    service = deserializer.getService();
                    passenger = deserializer.getPassenger();

                    setTabs();
                }
                else {
                    Toast.makeText(this, R.string.service_not_found_error, Toast.LENGTH_LONG).show();
                    Intent i = new Intent(this, MainActivity.class);
                    startActivity(i);
                }
            }
            else {
                setErrorSnackBar(service_activity_layout, getResources().getString(R.string.error_general));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void setTabs() {
        mainTabs = (TabLayout) findViewById(R.id.main_tabs);

        mainTabs.addTab(mainTabs.newTab().setText(getResources().getString(R.string.main_tab)));

        //if (old == 1 ) {
            mainTabs.addTab(mainTabs.newTab().setText(getResources().getString(R.string.options_tab)));
            mainTabs.addTab(mainTabs.newTab().setText(getResources().getString(R.string.tracing_tab)));
        //}

        mainTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mainTabs.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        final PagerAdapter adapter = new TabPagerAdapter(getFragmentManager(),mainTabs.getTabCount(), getApplicationContext(), user, service, passenger);

        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mainTabs));

        mainTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setCurrentItem(tab, false);
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
}
