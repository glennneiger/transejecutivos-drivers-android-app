package com.development.transejecutivosdrivers.background_services;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Process;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.ServiceActivity;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by william.montiel on 12/01/2017.
 */

public class BackgroundServiceManager extends Service{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private int id;
    private static final String LOG_TAG = "LALA";
    public static boolean IS_SERVICE_RUNNING = false;
    private String ref = "NONE";

    // Handler that receives messages from the thread
    public final class ServiceHandler extends Handler {
        LocationManager locationManager;
        Message message;

        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            this.message = msg;

            Bundle bundle = this.message.getData();

            Log.d("LALA", "Subtask is started");
            Toast.makeText(getApplicationContext(), getString(R.string.get_location_prompt), Toast.LENGTH_SHORT).show();
            locationManager = new LocationManager();
            locationManager.setContext(getApplicationContext());
            locationManager.setData(bundle.getInt(JsonKeys.SERVICE_ID), bundle.getString(JsonKeys.USER_APIKEY), bundle.getString(JsonKeys.LOCATION));
            locationManager.setServiceHandler(this);
            locationManager.start();
        }

        public void stop() {
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            Log.d("LALA", "Subtask is finished");
            Log.d("LALA", "Subtask id: " + message.arg1);
            stopSelf(message.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.id = startId;
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // For each start request, send a message to start a job and deliver the
                // start ID so we know which request we're stopping when we finish the job
                Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = startId;
                msg.setData(bundle);
                mServiceHandler.sendMessage(msg);
                this.ref = bundle.getString(JsonKeys.SERVICE_REFERENCE);
            }

            //Log.d("LALA", "ACTION: " + intent.getAction());
            if (intent.getAction().equals(Const.ACTION.STARTFOREGROUND_ACTION)) {
                showNotification();
            } else if (intent.getAction().equals(Const.ACTION.STOPFOREGROUND_ACTION)) {
                stopForeground(true);
                stopSelf();
            }
        }

        return START_STICKY;
    }


    private void showNotification() {
        Intent notificationIntent = new Intent(this, ServiceActivity.class);
        notificationIntent.setAction(Const.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent stopIntent = new Intent(this, BackgroundServiceManager.class);
        stopIntent.setAction(Const.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent stopPintent = PendingIntent.getService(this, 0, stopIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setTicker(getString(R.string.app_name))
                .setContentText(getString(R.string.get_location_prompt) + " " + this.ref)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_transejecutivoslauncher))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_delete, getString(R.string.prompt_end_foreground_service), stopPintent).build();

        startForeground(Const.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LALA", "Service done");
        Toast.makeText(getApplicationContext(), getString(R.string.stop_location_prompt), Toast.LENGTH_SHORT).show();
    }
}
