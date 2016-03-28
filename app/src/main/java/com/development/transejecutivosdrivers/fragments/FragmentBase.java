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
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.ServiceExpandableListAdapter;
import com.development.transejecutivosdrivers.models.Date;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FragmentBase extends Fragment {
    protected static ExpandableListView expandableListView;
    protected static ServiceExpandableListAdapter serviceExpandableListAdapter;
    View view;
    View progressBar;
    View layout;
    User user;

    public void setUser(User user) {
        this.user = user;
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
        boolean pendings = false;

        Service service = new Service();
        service.setIdService(1);
        service.setStartDate(1 + "/03/2016 8:0" + 1);
        service.setDestiny("Bogotá cll " + 1);
        service.setSource("Cali cra " + 1);
        service.setPaxCant(5);
        service.setPax("");
        service.setObservations("There's no observations");
        service.setReference("U56" + 1);
        service.setStatus("orden");

        if (service != null) {
            pendings = true;
        }

        showService(service);

        if (!pendings) {
            setupServicesList();
        }
    }


    public void showService(final Service service) {
        if (service.getStatus().equals("orden")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage("La orden " + service.getReference() + ", se encuentra pendiente. Para poder aceptar nuevas ordenes debe completarla");
            dialog.setPositiveButton("Ir", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(getActivity(), ServiceActivity.class);
                    i.putExtra("idService", service.getIdService());
                    startActivity(i);
                }
            });
            AlertDialog alert = dialog.create();
            alert.show();
        }
    }


    public void setupServicesList() {
        ArrayList<Date> dates = new ArrayList<>();
        ArrayList<ArrayList<Service>> services = new ArrayList<ArrayList<Service>>();

        for (int i = 0; i < 5; i++) {
            int j = 28 + i;
            Date d = new Date(j + "/03/2016");
            dates.add(d);

            ArrayList<Service> currentServicesArray = new ArrayList<>();

            for (int m = 0; m < 4; m++) {
                Service service = new Service();
                service.setIdService(j);
                service.setStartDate(j + "/03/2016 8:0" + i);
                service.setDestiny("Bogotá cll " + i);
                service.setSource("Cali cra " + i);
                service.setPaxCant(5);
                service.setPax("");
                service.setObservations("There's no observations");
                service.setReference("U56" + i);
                service.setStatus("orden");

                currentServicesArray.add(service);
            }

            services.add(currentServicesArray);
        }

        setItems(services, dates);
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