package com.development.transejecutivosdrivers;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.misc.VolleyErrorHandler;
import com.mobapphome.mahandroidupdater.tools.MAHUpdaterController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResetpassActivity extends ActivityBase implements LoaderManager.LoaderCallbacks<Cursor> {

    String username = "";
    RecoverPasswordTask mAuthTask = null;

    // UI references.
    EditText mPassword2View;
    EditText mPassword1View;
    TextInputLayout inputLayoutPassword2;
    TextInputLayout inputLayoutPassword1;
    View mProgressView;
    View mFormView;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpass);

        Bundle t = getIntent().getExtras();
        if (t != null) {
            username = t.getString(JsonKeys.USER_USERNAME);
        }

        layout = findViewById(R.id.resetpass_layout);
        inputLayoutPassword1  = (TextInputLayout) findViewById(R.id.txt_input_layout_password1);
        inputLayoutPassword2  = (TextInputLayout) findViewById(R.id.txt_input_layout_password2);

        mPassword1View = (EditText) findViewById(R.id.password1);
        mPassword2View = (EditText) findViewById(R.id.password2);

        mFormView = findViewById(R.id.resetpass_form);
        mProgressView = findViewById(R.id.resetpass_progress);

        Button mRecoverPasswordButton = (Button) findViewById(R.id.btn_resetpassword);
        mRecoverPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        MAHUpdaterController.init(this, ApiConstants.URL_APP_VERSION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MAHUpdaterController.end();
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        inputLayoutPassword1.setError(null);
        inputLayoutPassword2.setError(null);

        // Store values at the time of the login attempt.
        String password1 = mPassword1View.getText().toString();
        String password2 = mPassword2View.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password1)) {
            inputLayoutPassword1.setError(getString(R.string.error_empty_password1));
            focusView = mPassword1View;
            cancel = true;
        }

        if (TextUtils.isEmpty(password2)) {
            inputLayoutPassword2.setError(getString(R.string.error_empty_password2));
            focusView = mPassword2View;
            cancel = true;
        }

        if (!password1.equals(password2)) {
            inputLayoutPassword2.setError(getString(R.string.error_not_equals_passwords));
            focusView = mPassword2View;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true, mFormView, mProgressView);
            mAuthTask = new RecoverPasswordTask(username, password1, password2);
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

    @Override
    public void onBackPressed() {

    }

    public class RecoverPasswordTask extends AsyncTask<Void, Void, Boolean> {
        private final String username;
        private final String password1;
        private final String password2;

        RecoverPasswordTask(String username, String password1, String password2) {
            this.username = username;
            this.password1 = password1;
            this.password2 = password2;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Cache cache = new DiskBasedCache(getApplicationContext().getCacheDir(), 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            RequestQueue mRequestQueue = new RequestQueue(cache, network);
            mRequestQueue.start();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    ApiConstants.URL_RESET_PASSWORD,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            validateResponse(response);
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
                            String message = (TextUtils.isEmpty(msg) ? getResources().getString(R.string.server_error) : msg);
                            setErrorSnackBar(layout, message);
                        }
                    }) {

                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    params.put(JsonKeys.USERNAME, username);
                    params.put(JsonKeys.PASSWORD, password1);

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

        protected void validateResponse(String response) {
            try {
                JSONObject resObj = new JSONObject(response);
                Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
                if (!error) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.success_reset_password),
                            Toast.LENGTH_LONG).show();

                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);

                    onPostExecute(true);

                    finish();
                }
                else {
                    setErrorSnackBar(layout, getResources().getString(R.string.error_reset_password));
                    onCancelled();
                }
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mAuthTask = null;
            showProgress(false, mFormView, mProgressView);
            super.onPostExecute(success);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false, mFormView, mProgressView);
        }
    }
}
