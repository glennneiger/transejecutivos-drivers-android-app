package com.development.transejecutivosdrivers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.development.transejecutivosdrivers.apiconfig.ApiConstants;
import com.mobapphome.mahandroidupdater.tools.MAHUpdaterController;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MAHUpdaterController.init(this, ApiConstants.URL_APP_VERSION);

        Thread timerTread = new Thread(){
            public void run(){
                try{
                    sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerTread.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MAHUpdaterController.end();
    }
}
