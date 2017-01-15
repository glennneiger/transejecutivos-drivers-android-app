package com.development.transejecutivosdrivers.background_services;

import android.app.Service;
import android.os.Bundle;
import android.os.Process;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.development.transejecutivosdrivers.adapters.JsonKeys;

/**
 * Created by william.montiel on 12/01/2017.
 */

public class BackgroundServiceManager extends Service{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private int id;

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
            Toast.makeText(getApplicationContext(), "El proceso de envío de ubicación ha iniciado", Toast.LENGTH_SHORT).show();
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
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("LALA", "Service done");
        Toast.makeText(getApplicationContext(), "El proceso de envío de ubicación ha finalizado", Toast.LENGTH_SHORT).show();
    }
}
