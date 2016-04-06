package com.development.transejecutivosdrivers.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.format.Time;
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
import com.development.transejecutivosdrivers.background_services.AlarmReceiver;
import com.development.transejecutivosdrivers.deserializers.Deserializer;
import com.development.transejecutivosdrivers.deserializers.ServiceDeserializer;
import com.development.transejecutivosdrivers.models.Date;
import com.development.transejecutivosdrivers.models.Passenger;
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
    Service service;
    Passenger passenger;
    Context context;

    public void setUser(User user) {
        this.user = user;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
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
                int old = (int) service.get(JsonKeys.SERVICE_OLD);
                if (idService != 0) {
                    showService(idService, old);
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

    public void showService(final int idService, final int old) {
        String msg = "";
        String button_prompt = "";

        if (old == 1) {
            msg = getResources().getString(R.string.old_service_message);
            button_prompt = getResources().getString(R.string.button_old_service_modal_prompt);
        }
        else {
            msg = getResources().getString(R.string.new_service_message);
            button_prompt = getResources().getString(R.string.button_new_service_modal_prompt);
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(msg);
        dialog.setPositiveButton(button_prompt, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(getActivity(), ServiceActivity.class);
                i.putExtra("idService", idService);
                i.putExtra("tab", 0);
                i.putExtra("old", old);
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

    public void setSuccesSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();

        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight));
        TextView txtv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        txtv.setGravity(Gravity.CENTER);
    }

    protected void  validateResponse(String response, String btn) {
        Log.d("START SERVICE", response);
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                Time today = new Time(Time.getCurrentTimezone());
                today.setToNow();

                String date = today.monthDay + "/" + (today.month+1) + "/" + today.year + " " + today.format("%k:%M:%S");

                if (btn.equals("b1ha")) {
                    setSuccesSnackBar(getResources().getString(R.string.on_way_message));
                    this.service.setB1ha(date);
                    scheduleAlarm(JsonKeys.PRELOCATION);
                }
                else if (btn.equals("bls")) {
                    setSuccesSnackBar(getResources().getString(R.string.on_source_message));
                    this.service.setBls(date);
                }
                else if (btn.equals("pab")) {
                    setSuccesSnackBar(getResources().getString(R.string.start_service_message));
                    this.service.setPab(date);
                    scheduleAlarm(JsonKeys.ONSERVICE);
                }
                else if (btn.equals("st")) {
                    setSuccesSnackBar(getResources().getString(R.string.finish_service_message));
                    this.service.setSt(date);
                }
                reload();
            }
            else {
                cancelAlarm();
                setErrorSnackBar(getResources().getString(R.string.error_general));
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void reload() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private void scheduleAlarm(String location) {
        Log.d("ALARM", location);
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(this.context, AlarmReceiver.class);

        intent.putExtra(JsonKeys.SERVICE_ID, this.service.getIdService());
        intent.putExtra(JsonKeys.USER_APIKEY, this.user.getApikey());
        intent.putExtra(JsonKeys.LOCATION, location);

        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup periodic alarm every 10 seconds

        long firstMillis = 10000; // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.context.getSystemService(this.context.ALARM_SERVICE);

        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, firstMillis, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(this.context, AlarmReceiver.class);

        final PendingIntent pIntent = PendingIntent.getBroadcast(this.context, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
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