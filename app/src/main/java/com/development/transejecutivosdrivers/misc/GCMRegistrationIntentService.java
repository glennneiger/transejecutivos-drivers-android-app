package com.development.transejecutivosdrivers.misc;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GCMRegistrationIntentService extends IntentService {


    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent){registerGCM();}

    private void registerGCM() {

        Intent registrationComplete = null;
        String token = null;
        try{
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(Const.CONST_GCM_DEFAULTSENDERID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            //notify to UI that registration complete success
            registrationComplete = new Intent(Const.CONST_GCM_REGISTER_SUCCES);
            registrationComplete.putExtra(JsonKeys.TOKEN_GCM, token);

        }catch(Exception e){
            registrationComplete = new Intent(Const.CONST_GCM_REGISTER_ERROR);
        }

        //Send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }
}
