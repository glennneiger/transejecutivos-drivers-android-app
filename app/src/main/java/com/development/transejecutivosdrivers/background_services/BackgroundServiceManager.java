package com.development.transejecutivosdrivers.background_services;

import android.app.Service;
import android.os.Process;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
            // Normally we would do some work here, like download a file.
            /*
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            */

            Log.d("LALA", "Sub started");
            locationManager = new LocationManager();
            locationManager.setContext(getApplicationContext());
            locationManager.setServiceHandler(this);
            locationManager.start();
        }

        public void stop() {
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            Log.d("LALA", "Sub ended");
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
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
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
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
