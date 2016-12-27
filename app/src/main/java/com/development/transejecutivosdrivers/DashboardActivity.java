package com.development.transejecutivosdrivers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.DashboardMenuAdapter;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.development.transejecutivosdrivers.misc.CacheManager;
import com.development.transejecutivosdrivers.misc.DialogCreator;
import com.development.transejecutivosdrivers.misc.GCMRegistrationIntentService;
import com.development.transejecutivosdrivers.models.DashboardMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class DashboardActivity extends ActivityBase implements AdapterView.OnItemClickListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private GridView gridView;
    private DashboardMenuAdapter adaptador;
    Context context;

    //GCM
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        validateSession();

        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if(!checkPermissions()){
                requestPermissions();
            }
        }

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        Log.e("GOOGLE SERVICES","> resulcode: "+resultCode+" <-> codesuces" + ConnectionResult.SUCCESS);
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), Const.CONST_GOOGLE_PLAY_NOT_INSTALL, Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
                GooglePlayServicesUtil.getErrorDialog(resultCode,this, 200).show();
            } else {
                Toast.makeText(getApplicationContext(), Const.CONST_GOOGLE_PLAY_NOT_SUPPORT, Toast.LENGTH_LONG).show();
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Const.CONST_GCM_REGISTER_SUCCES)){
                    token = intent.getStringExtra(JsonKeys.TOKEN_GCM);
                    setToken();
                }else if(intent.getAction().equals(Const.CONST_GCM_REGISTER_ERROR)){
                    Toast.makeText(getApplicationContext(), Const.CONST_GMC_MESSAGE_REGISTER_ERROR, Toast.LENGTH_LONG).show();
                }
            }
        };

        context = getApplicationContext();

        gridView = (GridView) findViewById(R.id.gridView_menu);
        adaptador = new DashboardMenuAdapter(this);
        gridView.setAdapter(adaptador);

        gridView.setOnItemClickListener(this);
    }

    public void setToken(){
        Log.d("LALA", token);
        user.setToken(token);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isLocationServiceEnabled();
        checkGooglePlayServices();
        CacheManager cacheManager = new CacheManager(getApplicationContext(), JsonKeys.SERVICE_PREF, JsonKeys.SERVICE_KEY);
        cacheManager.cleanData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //moveTaskToBack(true);
        //finish();
        this.finishAffinity();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DashboardMenu item = (DashboardMenu) parent.getItemAtPosition(position);
        Intent i;
        switch (item.getId()) {
            case 0:
                i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
                break;

            case 1:
                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                break;

            case 2:
                i = new Intent(getApplicationContext(), SearchserviceActivity.class);
                startActivity(i);
                break;

            case 3:
                session.logoutUser();
                break;

            default:
                break;
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }


    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.WAKE_LOCK,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //finish();
                    //startActivity(getIntent());
                } else {
                    DialogCreator dialogCreator = new DialogCreator(this);
                    dialogCreator.createCustomDialog(getString(R.string.cancel_permission_location), "ACEPTAR");
                }
                break;
        }
    }
}

