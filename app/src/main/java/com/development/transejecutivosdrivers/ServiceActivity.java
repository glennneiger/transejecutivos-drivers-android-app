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

import com.android.volley.DefaultRetryPolicy;
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
import java.util.concurrent.TimeUnit;

public class ServiceActivity extends ActivityBase {

    private TabLayout mainTabs;
    private ViewPager viewPager;
    private PagerAdapter adapter = null;

    int idService = 0;
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
        mainTabs = (TabLayout) findViewById(R.id.main_tabs);

        Bundle t = getIntent().getExtras();
        if (t != null) {
            idService = t.getInt(JsonKeys.SERVICE_ID);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLocationServiceEnabled();
        getService();
    }

    public Service getServiceData() {
        return this.service;
    }

    public void getService() {
        showProgress(true, main_pager, progressBar);

        final boolean refresh;

        if (adapter != null) {
            refresh = true;
        }
        else {
            refresh = false;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_GET_SERVICE + "/" + this.idService,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validateServiceResponse(response, refresh);
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

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void validateServiceResponse(String response, boolean refresh) {
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
                    if (!refresh) {
                        setTabs();
                    }
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
        mainTabs.addTab(mainTabs.newTab().setText(getResources().getString(R.string.main_tab)));

        if (service.getOld() == 1) {
            mainTabs.addTab(mainTabs.newTab().setText(getResources().getString(R.string.tracing_tab)));
        }
        else {
            mainTabs.addTab(mainTabs.newTab().setText(getResources().getString(R.string.options_tab)));
        }

        viewPager = (ViewPager) findViewById(R.id.main_pager);
        adapter = new TabPagerAdapter(getFragmentManager(),mainTabs.getTabCount(), getApplicationContext(), user, service, passenger);

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

        viewPager.setCurrentItem(0, false);
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
