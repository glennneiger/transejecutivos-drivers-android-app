package com.development.transejecutivosdrivers.misc;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.development.transejecutivosdrivers.DashboardActivity;
import com.development.transejecutivosdrivers.R;
import com.development.transejecutivosdrivers.adapters.Const;
import com.development.transejecutivosdrivers.adapters.JsonKeys;
import com.google.android.gms.gcm.GcmListenerService;
import org.json.JSONObject;

public class GCMPushReceiverService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {String message = "";
        String messageObj = data.getString(Const.CONST_NOTIFICATION_PUSH_BODY);
        try {
            JSONObject obj = new JSONObject(messageObj);
            message = obj.getString(Const.CONST_NOTIFICATION_PUSH_BODY);
        } catch (Throwable t) {
            Log.e("My App", "Could not parse malformed JSON: \"" + messageObj + "\"");
        }
        String title = data.getString(Const.CONST_NOTIFICATION_PUSH_TITLE);
        String database = data.getString(Const.CONST_NOTIFICACION_PUSH_SERVICE);
        sendNotification(message,title,database);
    }

    private void sendNotification(String message,String title,String database) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(JsonKeys.NOTIFICACION_PUTEXTRA, database);
        int requestCode = 0;//Your request code
        PendingIntent pendingIntent = PendingIntent.getActivity(this.getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(sound)
                .setVibrate(new long[] {0, 1000})
                .setLights(Color.WHITE, 1000, 3000)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_transejecutivoslauncher))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title)
                        .bigText(message));

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
    }
}
