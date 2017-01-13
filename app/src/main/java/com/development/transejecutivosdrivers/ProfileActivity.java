package com.development.transejecutivosdrivers;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.development.transejecutivosdrivers.models.User;
import com.mobapphome.mahandroidupdater.tools.MAHUpdaterController;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ProfileActivity extends ActivityBase implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProfileTask mAuthTask = null;

    // UI references.
    private View mProfileProgress;
    private View mProfileForm;
    private View mProfileLayout;

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutLastname;
    private TextInputLayout inputLayoutEmail1;
    private TextInputLayout inputLayoutEmail2;
    private TextInputLayout inputLayoutPhone1;
    private TextInputLayout inputLayoutPhone2;
    private TextInputLayout inputLayoutPassword;

    private EditText txtName;
    private EditText txtLastName;
    private EditText txtEmail1;
    private EditText txtEmail2;
    private EditText txtPhone1;
    private EditText txtPhone2;
    private EditText txtPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validateSession();
        setViews();
        setProfileValues();

        Button mProfileButton = (Button) findViewById(R.id.button_profile);
        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempt();
            }
        });

        MAHUpdaterController.init(this, ApiConstants.URL_APP_VERSION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MAHUpdaterController.end();
    }

    private void setViews() {
        mProfileProgress = findViewById(R.id.profile_progress);
        mProfileForm = findViewById(R.id.profile_form);
        mProfileLayout = findViewById(R.id.profile_layout);

        inputLayoutName  = (TextInputLayout) findViewById(R.id.txtinput_layout_name);
        inputLayoutLastname  = (TextInputLayout) findViewById(R.id.txtinput_layout_lastname);
        inputLayoutEmail1  = (TextInputLayout) findViewById(R.id.txtinput_layout_email1);
        inputLayoutEmail2  = (TextInputLayout) findViewById(R.id.txtinput_layout_email2);
        inputLayoutPhone1  = (TextInputLayout) findViewById(R.id.txtinput_layout_phone1);
        inputLayoutPhone2  = (TextInputLayout) findViewById(R.id.txtinput_layout_phone2);
        inputLayoutPassword  = (TextInputLayout) findViewById(R.id.txtinput_layout_password);

        txtName = (EditText) findViewById(R.id.name);
        txtLastName = (EditText) findViewById(R.id.lastname);
        txtEmail1 = (EditText) findViewById(R.id.email1);
        txtEmail2 = (EditText) findViewById(R.id.email2);
        txtPhone1 = (EditText) findViewById(R.id.phone1);
        txtPhone2 = (EditText) findViewById(R.id.phone2);
        txtPassword = (EditText) findViewById(R.id.password);
    }

    private void setProfileValues() {
        txtName.setText(user.getName());
        txtLastName.setText(user.getLastName());
        txtEmail1.setText(user.getEmail1());
        txtEmail2.setText(user.getEmail2());
        txtPhone1.setText(user.getPhone1());
        txtPhone2.setText(user.getPhone2());
    }

    private void attempt() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        inputLayoutName.setError(null);
        inputLayoutLastname.setError(null);
        inputLayoutEmail1.setError(null);
        inputLayoutEmail2.setError(null);
        inputLayoutPhone1.setError(null);
        inputLayoutPhone2.setError(null);
        inputLayoutPassword.setError(null);

        // Store values at the time of the login attempt.
        String name = txtName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String email1 = txtEmail1.getText().toString();
        String email2 = txtEmail2.getText().toString();
        String phone1 = txtPhone1.getText().toString();
        String phone2 = txtPhone2.getText().toString();
        String password = txtPassword.getText().toString();

        boolean cancel;

        cancel = validateField(name, inputLayoutName, txtName, getString(R.string.error_empty_name));
        cancel = validateField(lastName, inputLayoutLastname, txtLastName, getString(R.string.error_empty_lastname));
        cancel = isValidEmail(email1, inputLayoutEmail1, txtEmail1, getString(R.string.error_invalid_email));
        cancel = validateField(phone1, inputLayoutPhone1, txtPhone1, getString(R.string.error_empty_phone1));

        if (!TextUtils.isEmpty(email2)) {
            cancel = isValidEmail(email2, inputLayoutEmail2, txtEmail2, getString(R.string.error_invalid_email));
        }

        if (!cancel) {
            showProgress(true, mProfileForm, mProfileProgress);
            mAuthTask = new ProfileTask(name, lastName, email1, email2, phone1, phone2, password);
            mAuthTask.execute((Void) null);
        }
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
            return true;
        }
        else if (id == R.id.action_dashboard) {
            Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public class ProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String name;
        private final String lastName;
        private final String email1;
        private final String email2;
        private final String phone1;
        private final String phone2;
        private final String password;

        public ProfileTask(String name, String lastName, String email1, String email2, String phone1, String phone2, String password) {
            this.name = name;
            this.lastName = lastName;
            this.email1 = email1;
            this.email2 = email2;
            this.phone1 = phone1;
            this.phone2 = phone2;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            StringRequest stringRequest = new StringRequest(
                    Request.Method.PUT,
                    ApiConstants.URL_UPDATE_PROFILE,
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
                            setErrorSnackBar(mProfileLayout, getResources().getString(R.string.error_general));
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
                    params.put(JsonKeys.USER_NAME, name);
                    params.put(JsonKeys.USER_LASTNAMEP, lastName);
                    params.put(JsonKeys.USER_EMAIL1, email1);
                    params.put(JsonKeys.USER_EMAIL2, email2);
                    params.put(JsonKeys.USER_PHONE1, phone1);
                    params.put(JsonKeys.USER_PHONE2, phone2);
                    params.put(JsonKeys.PASSWORD, password);
                    params.put(JsonKeys.USER_NOTIFICATIONS, "1");

                    return params;
                }
            };


            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    (int) TimeUnit.SECONDS.toMillis(10),//time out in 10second
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//DEFAULT_MAX_RETRIES = 1;
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);


            return true;
        }

        protected void validateResponse(String response) {
            try {
                JSONObject resObj = new JSONObject(response);
                Boolean error = (Boolean) resObj.get(JsonKeys.ERROR);
                if (!error) {
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

                    session.createUserLoginSession(user);

                    onPostExecute(true);
                    setSnackBarWithAction(mProfileLayout, getResources().getString(R.string.profile_update_success));
                }
                else {
                    setErrorSnackBar(mProfileLayout, getResources().getString(R.string.error_invalid_profile_update));
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
            showProgress(false, mProfileForm, mProfileProgress);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false, mProfileForm, mProfileProgress);
        }
    }
}