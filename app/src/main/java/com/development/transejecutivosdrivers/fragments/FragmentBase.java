package com.development.transejecutivosdrivers.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

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
import com.development.transejecutivosdrivers.deserializers.ServiceDeserializer;
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

public class FragmentBase extends Fragment {
    protected static ExpandableListView expandableListView;
    protected static ServiceExpandableListAdapter serviceExpandableListAdapter;
    View view;
    View progressBar;
    View layout;
    User user;
    int idService;

    public void setUser(User user) {
        this.user = user;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null)  {
            savedInstanceState.remove ("android:support:fragments");
        }

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        return;
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
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Authorization", user.getApikey());
                return headers;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void validateSearchPendingServiceResponse(String response) {
        showProgress(false, layout, progressBar);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
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
        dialog.setMessage("Tiene una orden que se encuentra pendiente. Para poder aceptar nuevas ordenes debe completarla");
        dialog.setPositiveButton("Completar Orden", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), ServiceActivity.class);
                i.putExtra("idService", idService);
                i.putExtra("tab", 0);
                startActivity(i);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }


    public void setupServicesList() {
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

        requestQueue.add(stringRequest);
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
            hashMap.put(header.get(i), services.get(i));
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

    public void setErrorSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorError));
        TextView txtv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View formView, final View progressView) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}