package com.development.transejecutivosdrivers.background_services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.development.transejecutivosdrivers.adapters.JsonKeys;

import java.util.Random;

/**
 * Created by developer on 4/3/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    private int idService;
    private String apikey;
    private String location;
    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle t = intent.getExtras();
        if (t != null) {
            idService = t.getInt(JsonKeys.SERVICE_ID);
            apikey = t.getString(JsonKeys.USER_APIKEY);
            location = t.getString(JsonKeys.LOCATION);

            if (location.equals(JsonKeys.PRELOCATION)) {
                Intent i = new Intent(context, PrelocationService.class);
                i.putExtra(JsonKeys.SERVICE_ID, idService);
                i.putExtra(JsonKeys.USER_APIKEY, apikey);

                startWakefulService(context, i);
                context.startService(i);
            }
            else if (location.equals(JsonKeys.ONSERVICE)) {
                Intent i = new Intent(context, LocationService.class);
                i.putExtra(JsonKeys.SERVICE_ID, idService);
                i.putExtra(JsonKeys.USER_APIKEY, apikey);

                startWakefulService(context, i);
                context.startService(i);
            }
        }
    }
}