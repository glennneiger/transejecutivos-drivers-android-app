package com.development.transejecutivosdrivers.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.adapters.ServiceExpandableListAdapter;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.misc.CacheManager;
import com.development.transejecutivosdrivers.models.Date;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isAdded()){
            searchPendingServices();
        }
    }

    public void searchPendingServices() {
        showProgress(true, layout, progressBar);
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                ApiConstants.URL_SEARCH_PENDING_SERVICE,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        validateSearchPendingServiceResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setErrorSnackBar(getResources().getString(R.string.error_general));
                        showProgress(false, layout, progressBar);
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
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

    public void validateSearchPendingServiceResponse(String response) {
        showProgress(false, layout, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);

            //Log.d("LALALALALA", "" + resObj.get(JsonKeys.SERVICE));
            //String res = "" + resObj.get(JsonKeys.SERVICE);

            if (!error) {
                JSONObject service = (JSONObject) resObj.get(JsonKeys.SERVICE);
                int idService = (int) service.get(JsonKeys.SERVICE_ID);
                if (idService != 0) {
                    showService(idService);
                }
                else {
                    setupServicesList();
                }
            }
            else {
                setErrorSnackBar(getResources().getString(R.string.error_general));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    public void showService(final int idService) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(getResources().getString(R.string.service_message));
        dialog.setPositiveButton(getResources().getString(R.string.button_service_modal_prompt), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CacheManager cacheManager = new CacheManager(getActivity(), JsonKeys.SERVICE_PREF, JsonKeys.SERVICE_KEY);
                cacheManager.cleanData();
                cacheManager.setData(JsonKeys.SERVICE_ID, idService + "");
                Intent i = new Intent(getActivity(), ServiceActivity.class);
                startActivity(i);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }


    public void setupServicesList() {
        if(isAdded()){
            showProgress(true, layout, progressBar);
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET,
                    ApiConstants.URL_SERVICES_GROUPED,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            processDataForGroup(response);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setErrorSnackBar(getResources().getString(R.string.error_general));
                            showProgress(false, layout, progressBar);
                        }
                    }) {

                @Override
                public Map<String, String> getParams() {
                    Map<String,String> params = new HashMap<String, String>();
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
    }

    public void processDataForGroup(String response) {
        showProgress(false, layout, progressBar);
        try {

            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                JSONArray servicesJsonArray = resObj.getJSONArray(JsonKeys.SERVICES);
                JSONArray datesJsonArray = resObj.getJSONArray(JsonKeys.DATES);
                if (servicesJsonArray.length() <= 0) {
                    setErrorSnackBar(getResources().getString(R.string.no_services));
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
                setErrorSnackBar(getResources().getString(R.string.error_general));
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

        serviceExpandableListAdapter = new ServiceExpandableListAdapter(getActivity(), header, hashMap);

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
}
