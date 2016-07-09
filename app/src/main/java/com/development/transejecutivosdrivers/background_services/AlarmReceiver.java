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

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle t = intent.getExtras();
        if (t != null) {
            Intent i = new Intent(context, PrelocationService.class);

            i.putExtra(JsonKeys.SERVICE_ID, t.getInt(JsonKeys.SERVICE_ID));
            i.putExtra(JsonKeys.USER_APIKEY, t.getString(JsonKeys.USER_APIKEY));
            i.putExtra(JsonKeys.LOCATION, t.getString(JsonKeys.LOCATION));

            startWakefulService(context, i);

            context.startService(i);
        }
    }
}