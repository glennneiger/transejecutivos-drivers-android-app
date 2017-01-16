package com.development.transejecutivosdrivers;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.misc.GCMRegistrationIntentService;
import com.development.transejecutivosdrivers.misc.UserSessionManager;
import com.development.transejecutivosdrivers.misc.VolleyErrorHandler;
import com.development.transejecutivosdrivers.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mobapphome.mahandroidupdater.tools.MAHUpdaterController;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ActivityBase implements LoaderCallbacks<Cursor> {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private TextView recoverPass;
    private TextInputLayout inputLayoutUsername;
    private TextInputLayout inputLayoutPassword;
    private View mProgressView;
    private View mLoginFormView;
    private View loginLayout;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    //GCM
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    public String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new UserSessionManager(getApplicationContext());

        loginLayout = findViewById(R.id.login_layout);
        inputLayoutUsername  = (TextInputLayout) findViewById(R.id.txt_input_layout_username);
        inputLayoutPassword  = (TextInputLayout) findViewById(R.id.txt_input_layout_password);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        recoverPass = (TextView) findViewById(R.id.txt_recoverpass);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLoginButton = (Button) findViewById(R.id.button_login);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        recoverPass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting RecoverpassActivity
                Intent i = new Intent(getApplicationContext(), RecoverpassActivity.class);
                startActivity(i);
                finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        showProgress(true, mLoginFormView, mProgressView);

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS != result) {
            if (googleAPI.isUserResolvableError(result)) {
                Toast.makeText(getApplicationContext(), Const.CONST_GOOGLE_PLAY_NOT_INSTALL, Toast.LENGTH_LONG).show();
                googleAPI.showErrorNotification(getApplicationContext(), result);
                googleAPI.getErrorDialog(this, result, 200).show();
            } else {
                Toast.makeText(getApplicationContext(), Const.CONST_GOOGLE_PLAY_NOT_SUPPORT, Toast.LENGTH_LONG).show();
            }
            showProgress(false, mLoginFormView, mProgressView);
        } else if(result == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED){
            googleAPI.getErrorDialog(this, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
        } else {
            //showProgress(false, mLoginFormView, mProgressView);
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Const.CONST_GCM_REGISTER_SUCCES)){
                    token = intent.getStringExtra(JsonKeys.TOKEN_GCM);
                }else if(intent.getAction().equals(Const.CONST_GCM_REGISTER_ERROR)){
                    Toast.makeText(getApplicationContext(), Const.CONST_GMC_MESSAGE_REGISTER_ERROR, Toast.LENGTH_LONG).show();
                }

                showProgress(false, mLoginFormView, mProgressView);
            }
        };

        MAHUpdaterController.init(this, ApiConstants.URL_APP_VERSION);
    }

    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.CONST_GCM_REGISTER_SUCCES));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Const.CONST_GCM_REGISTER_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MAHUpdaterController.end();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //moveTaskToBack(true);
        //finish();
        this.finishAffinity();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        inputLayoutUsername.setError(null);
        inputLayoutPassword.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(username)) {
            inputLayoutUsername.setError(getString(R.string.error_empty_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            inputLayoutPassword.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true, mLoginFormView, mProgressView);
            mAuthTask = new UserLoginTask(username, password);
            mAuthTask.execute((Void) null);
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;

        UserLoginTask(String username, String password) {
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    ApiConstants.URL_LOGIN,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            validateResponseLogin(response);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            onCancelled();
                            VolleyErrorHandler voleyErrorHandler = new VolleyErrorHandler();
                            voleyErrorHandler.setVolleyError(error);
                            voleyErrorHandler.process();
                            String msg = voleyErrorHandler.getMessage();
                            String message = (TextUtils.isEmpty(msg) ? getResources().getString(R.string.error_general) : msg);
                            setErrorSnackBar(loginLayout, message);
                        }
                    }) {

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put(JsonKeys.USERNAME, mUsername);
                    params.put(JsonKeys.PASSWORD, mPassword);

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mRequestQueue.add(stringRequest);
            return true;
        }

        protected void validateResponseLogin(String response) {
            try {
                JSONObject resObj = new JSONObject(response);
                Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
                if (!error) {
                    int first_time = (int) resObj.get(JsonKeys.USER_FIRSTIME);

                    if (first_time == 1) {
                        Intent i = new Intent(getApplicationContext(), ResetpassActivity.class);
                        i.putExtra(JsonKeys.USER_USERNAME, resObj.getString(JsonKeys.USER_USERNAME));
                        startActivity(i);
                    }
                    else {
                        User user = new User();
                        int idUser = (int) resObj.get(JsonKeys.USER_ID);
                        user.setIdUser(idUser);
                        user.setUsername(resObj.getString(JsonKeys.USER_USERNAME));
                        user.setName(resObj.getString(JsonKeys.USER_NAME));
                        user.setLastName(resObj.getString(JsonKeys.USER_LASTNAME));
                        user.setEmail1(resObj.getString(JsonKeys.USER_EMAIL1));
                        user.setEmail2(resObj.getString(JsonKeys.USER_EMAIL2));
                        user.setPhone1(resObj.getString(JsonKeys.USER_PHONE1));
                        user.setPhone2(resObj.getString(JsonKeys.USER_PHONE2));
                        user.setApikey(resObj.getString(JsonKeys.USER_APIKEY));
                        user.setCode(resObj.getString(JsonKeys.USER_CODE));

                        user.setToken(token);
                        updateUserToken(user);

                        session.createUserLoginSession(user);
                    }
                }
                else {
                    setErrorSnackBar(loginLayout, getResources().getString(R.string.error_invalid_login));
                    onCancelled();
                }
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        protected void updateUserToken(final User user) {
            Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.PUT,
                    ApiConstants.URL_UPDATE_PROFILE,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            onPostExecute(true);

                            // Starting MainActivity
                            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            // Add new Flag to start new Activity
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            showProgress(false, mLoginFormView, mProgressView);

                            finish();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            onCancelled();
                            VolleyErrorHandler voleyErrorHandler = new VolleyErrorHandler();
                            voleyErrorHandler.setVolleyError(error);
                            voleyErrorHandler.process();
                            String msg = voleyErrorHandler.getMessage();
                            String message = (TextUtils.isEmpty(msg) ? getResources().getString(R.string.error_general) : msg);
                            setErrorSnackBar(loginLayout, message);
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/x-www-form-urlencoded");
                    headers.put("Authorization", user.getApikey());
                    return headers;
                }

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(JsonKeys.USER_NAME, user.getName());
                    params.put(JsonKeys.USER_LASTNAMEP, user.getLastName());
                    params.put(JsonKeys.USER_EMAIL1, user.getEmail1());
                    params.put(JsonKeys.USER_EMAIL2, user.getEmail2());
                    params.put(JsonKeys.USER_PHONE1, user.getPhone1());
                    params.put(JsonKeys.USER_PHONE2, user.getPhone2());
                    params.put(JsonKeys.USER_TOKEN, user.getToken());
                    params.put(JsonKeys.PASSWORD, "");
                    params.put(JsonKeys.USER_NOTIFICATIONS, "1");

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            mRequestQueue.add(stringRequest);
        }


        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;

            super.onPostExecute(success);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false, mLoginFormView, mProgressView);
        }
    }
}

