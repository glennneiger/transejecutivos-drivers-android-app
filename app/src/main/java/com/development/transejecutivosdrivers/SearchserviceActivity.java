package com.development.transejecutivosdrivers;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.adapters.ServiceExpandableListAdapter;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.fragments.ServiceFragment;
import com.development.transejecutivosdrivers.fragments.ServicesListFragment;
import com.development.transejecutivosdrivers.models.Date;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SearchserviceActivity extends ActivityBase {
    View search_service_progress;
    View search_service_form;
    View search_service_activity;
    protected Service service;
    protected Passenger passenger;

    protected static ServiceExpandableListAdapter serviceExpandableListAdapter;
    protected static ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchservice);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validateSession();

        expandableListView = (ExpandableListView) findViewById(R.id.service_expandable_listview);

        // Setting group indicator null for custom indicator
        expandableListView.setGroupIndicator(null);


        Button btn_search_service = (Button) findViewById(R.id.btn_search_service);
        search_service_progress = findViewById(R.id.search_service_progress);
        search_service_form = findViewById(R.id.search_service_form);

        btn_search_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchService();
            }
        });
    }


    public void searchService() {
        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);

        final String date = String.format("%02d", (datePicker.getMonth() + 1)) + "/" + String.format("%02d", (datePicker.getDayOfMonth())) + "/" + datePicker.getYear();

        showProgress(true, search_service_form, search_service_progress);
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                ApiConstants.URL_SEARCH_SERVICE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(search_service_activity, getString(R.string.error_general));
                        showProgress(false, search_service_form, search_service_progress);
                    }
                }) {

            @Override
            public Map<String, String> getParams() {
                Map<String,String> params = new HashMap<String, String>();
                params.put(JsonKeys.DATE, date);
                return params;
            }

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


    protected void processResponse(String response) {
        showProgress(false, search_service_form, search_service_progress);
        try {

            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                JSONArray servicesJsonArray = resObj.getJSONArray(JsonKeys.SERVICES);
                JSONArray datesJsonArray = resObj.getJSONArray(JsonKeys.DATES);
                if (servicesJsonArray.length() <= 0) {
                    setErrorSnackBar(search_service_activity, getString(R.string.no_services));
                }
                else {
                    Deserializer deserializer = new Deserializer();
                    deserializer.setDatesJsonArray(datesJsonArray);
                    deserializer.setServicesJsonArray(servicesJsonArray);
                    deserializer.deserializeGroupedServices();

                    setItems(deserializer.getServicesArray(), deserializer.getDatesArray());
                }
            }
            else {
                setErrorSnackBar(search_service_activity, getString(R.string.error_general));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    // Setting headers and childs to expandable listview
    void setItems(ArrayList<ArrayList<Service>> services, List<Date> dates) {

        // Array list for header
        ArrayList<String> header = new ArrayList<>();

        // Hash map for both header and child
        HashMap<String, ArrayList<Service>> hashMap = new HashMap<>();

        // Adding headers to list
        for (int i = 0; i < dates.size(); i++) {
            header.add(dates.get(i).getDate());
            hashMap.put(dates.get(i).getDate(), services.get(i));
        }

        serviceExpandableListAdapter = new ServiceExpandableListAdapter(getApplication(), header, hashMap);

        // Setting adpater over expandablelistview
        expandableListView.setAdapter(serviceExpandableListAdapter);

        setListener();
    }

    // Setting different listeners to expandablelistview
    void setListener() {

        // This listener will show toast on group click
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView listview, View view,
                                        int group_pos, long id) {
                return false;
            }
        });

        // This listener will expand one group at one time
        // You can remove this listener for expanding all groups
        expandableListView
                .setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    // Default position
                    int previousGroup = -1;

                    @Override
                    public void onGroupExpand(int groupPosition) {
                        if (groupPosition != previousGroup)

                            // Collapse the expanded group
                            expandableListView.collapseGroup(previousGroup);
                        previousGroup = groupPosition;
                    }

                });

        // This listener will show toast on child click
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView listview, View view,
                                        int groupPos, int childPos, long id) {

                return false;
            }
        });
    }

    protected void setFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ServicesListFragment servicesListFragment = new ServicesListFragment();
        servicesListFragment.setUser(user);
        fragmentTransaction.add(R.id.fragment_container, servicesListFragment, "Services List Fragment");
        fragmentTransaction.commit();

        fragmentTransaction.add(R.id.fragment_container, servicesListFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLocationServiceEnabled();
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
