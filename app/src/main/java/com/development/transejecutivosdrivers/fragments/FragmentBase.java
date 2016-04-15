package com.development.transejecutivosdrivers.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.adapters.ServiceExpandableListAdapter;
import com.development.transejecutivosdrivers.background_services.AlarmReceiver;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;

public class FragmentBase extends Fragment {
    protected static ExpandableListView expandableListView;
    protected static ServiceExpandableListAdapter serviceExpandableListAdapter;
    protected static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    View view;
    View progressBar;
    View layout;

    ImageView imageView;
    Button button_take_photo;
    Button button_finish_tracing;
    String image;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                imageView.setImageBitmap(bitmap);

                button_finish_tracing.setVisibility(View.VISIBLE);
            }
        }
    }

    protected void  validateResponse(String response, String btn) {
        try {
            JSONObject resObj = new JSONObject(response);
            Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
            if (!error) {
                if (btn.equals("b1ha")) {
                    Toast.makeText(this.context, getResources().getString(R.string.on_way_message), Toast.LENGTH_SHORT).show();
                    //setSuccesSnackBar(getResources().getString(R.string.on_way_message));
                    scheduleAlarm(JsonKeys.PRELOCATION);
                }
                else if (btn.equals("bls")) {
                    Toast.makeText(this.context, getResources().getString(R.string.on_source_message), Toast.LENGTH_LONG).show();
                    //setSuccesSnackBar(getResources().getString(R.string.on_source_message));
                }
                else if (btn.equals("pab")) {
                    Toast.makeText(this.context, getResources().getString(R.string.start_service_message), Toast.LENGTH_LONG).show();
                    //setSuccesSnackBar(getResources().getString(R.string.start_service_message));
                    scheduleAlarm(JsonKeys.ONSERVICE);
                } else if (btn.equals("st")) {
                    Toast.makeText(this.context, getResources().getString(R.string.finish_service_message), Toast.LENGTH_LONG).show();
                    //setSuccesSnackBar(getResources().getString(R.string.finish_service_message));
                }
                reload();
            }
            else {
                cancelAlarm();
                String msg = resObj.getString(JsonKeys.MESSAGE);
                setErrorSnackBar(msg);
            }
        }
        catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    protected void refreshFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    protected void reload() {
        //FragmentTransaction ft = getFragmentManager().beginTransaction();
        //ft.detach(this).attach(this).commit();
        getActivity().finish();
        getActivity().startActivity(getActivity().getIntent());
    }

    private void scheduleAlarm(String location) {
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