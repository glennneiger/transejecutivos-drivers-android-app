package com.development.transejecutivosdrivers.background_services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Random;

/**
 * Created by developer on 4/3/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, PrelocationService.class);

        Random r = new Random();
        int i1 = r.nextInt(80 - 65) + 65;

        i.putExtra("idService", 1);
        i.putExtra("longitude", 3.444 + i1);
        i.putExtra("latitude", 5.6666 + i1);

        startWakefulService(context, i);

        context.startService(i);
    }
}