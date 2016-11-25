package com.development.transejecutivosdrivers.misc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by usuario on 29/09/2015.
 */
public class DialogCreator {

    public Context context;

    public DialogCreator(Context context) {
        this.context = context;
    }

    public void createCustomDialog(String message, String btnTitle) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
        dialog.setMessage(message);
        dialog.setPositiveButton(btnTitle, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    public void createActivateGPSDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
        dialog.setMessage("Por favor active los servicios de ubicación");
        dialog.setPositiveButton("Activar servicios", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //this will navigate user to the device location settings screen
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    public void createRedirectCustomDialog(String message, String btnTitle, final Intent intent) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
        dialog.setMessage(message);
        dialog.setPositiveButton(btnTitle, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(intent);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }
}
