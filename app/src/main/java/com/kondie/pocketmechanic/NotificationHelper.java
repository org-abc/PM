package com.kondie.pocketmechanic;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {


    public static void sendNotif(Context context, String tittle, String contentText){
        try {
            Intent toMainIntent = new Intent(context, MainActivity.class);
            toMainIntent.putExtra("track", "yes");
            PendingIntent toMainPIntent = PendingIntent.getActivity(context, MainActivity.NOTIF_ID, toMainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notif = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID);
            notif.setContentTitle(tittle)
                    .setContentText(contentText)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                    .setContentIntent(toMainPIntent)
                    .setDeleteIntent(getDeleteIntent(context));

            NotificationManagerCompat notifMan = NotificationManagerCompat.from(context);
            notifMan.notify(MainActivity.NOTIF_ID, notif.build());
        }catch (Exception e){
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static PendingIntent getDeleteIntent(Context context){

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(MainActivity.ACTION_DELETE_NOTIFICATION);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
