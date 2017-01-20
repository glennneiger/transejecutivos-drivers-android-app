package com.development.transejecutivosdrivers.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.adapters.ServiceExpandableListAdapter;
import com.development.transejecutivosdrivers.background_services.BackgroundService;
import com.development.transejecutivosdrivers.background_services.BackgroundServiceManager;
import com.development.transejecutivosdrivers.misc.CacheManager;
import com.development.transejecutivosdrivers.models.Passenger;
import com.development.transejecutivosdrivers.models.Service;
import com.development.transejecutivosdrivers.models.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static android.R.attr.button;

public class FragmentBase extends Fragment {
    long ALARM_START = 1;
    long ALARM_REPEAT = 20000;

    protected static ExpandableListView expandableListView;
    protected static ServiceExpandableListAdapter serviceExpandableListAdapter;

    int id;

    View view;
    View progressBar;
    View layout;

    ImageView imageView;
    Button button_take_photo;
    Button button_finish_tracing;
    String image = "";

    User user;
    Service service;
    Passenger passenger;
    Context context;

    //Take picture var
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";

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

    public int getIdFragment() {
        return this.id;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void onLaunchCamera(View view) {
        CacheManager cacheManager = new CacheManager(getActivity(), JsonKeys.TAKING_PHOTO_PREF, JsonKeys.TAKING_PHOTO_KEY);
        cacheManager.cleanData();
        cacheManager.setData(JsonKeys.TAKING_PHOTO, "1");
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());

                // Resize the bitmap to 1080x720 (width x height)
                Bitmap finalBitmap = Bitmap.createScaledBitmap(takenImage, 1080, 720, true);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);

                byte[] byteArray = stream.toByteArray();

                image = Base64.encodeToString(byteArray, Base64.DEFAULT);

                // Resize the bitmap to 150x100 (width x height)
                Bitmap bMapScaled = Bitmap.createScaledBitmap(takenImage, 300, 200, true);

                // Load the taken image into a preview
                imageView.setImageBitmap(bMapScaled);

                button_finish_tracing.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(getActivity(), getString(R.string.photo_wasnt_take_it), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    public void setErrorSnackBar(String message) {
        if(isAdded()){
            Snackbar snackbar = Snackbar
                    .make(view, message, Snackbar.LENGTH_LONG);

            snackbar.show();

            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(getResources().getColor(R.color.colorError));
            TextView txtv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER);
        }
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

    protected void reload() {
        //getActivity().finish();
        //getActivity().startActivity(getActivity().getIntent());
        Intent refresh = new Intent(getActivity(), ServiceActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(refresh);
    }

    protected void scheduleAlarm(String location) {
        /*
        Intent backgroundService = new Intent(getActivity(), BackgroundServiceManager.class);
        backgroundService.putExtra(JsonKeys.SERVICE_ID, this.service.getIdService());
        backgroundService.putExtra(JsonKeys.USER_APIKEY, this.user.getApikey());
        backgroundService.putExtra(JsonKeys.LOCATION, location);
        getActivity().startService(backgroundService);
        */

        Intent service = new Intent(getActivity(), BackgroundServiceManager.class);
        if (!BackgroundServiceManager.IS_SERVICE_RUNNING) {
            service.putExtra(JsonKeys.SERVICE_ID, this.service.getIdService());
            service.putExtra(JsonKeys.SERVICE_REFERENCE, this.service.getReference());
            service.putExtra(JsonKeys.USER_APIKEY, this.user.getApikey());
            service.putExtra(JsonKeys.LOCATION, location);
            service.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
            BackgroundServiceManager.IS_SERVICE_RUNNING = true;
            getActivity().startService(service);
        }
    }

    public void cancelAlarm() {
        Intent service = new Intent(getActivity(), BackgroundServiceManager.class);
        if (BackgroundServiceManager.IS_SERVICE_RUNNING) {
            service.setAction(Const.ACTION.STOPFOREGROUND_ACTION);
            BackgroundServiceManager.IS_SERVICE_RUNNING = false;
            getActivity().startService(service);
        } else {
            getActivity().stopService(new Intent(getActivity(), BackgroundServiceManager.class));
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View formView, final View progressView) {
        if(isAdded()){
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
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                formView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }
}