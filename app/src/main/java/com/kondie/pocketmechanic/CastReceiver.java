package com.kondie.pocketmechanic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by kondie on 2018/02/12.
 */

public class CastReceiver extends BroadcastReceiver {

    private static final String ACTION_UPDATE_LOCATION_SERVICE = "ACTION_UPDATE_LOCATION_SERVICE";
    public static final String ACTION_START_BACKGROUND_CHECK = "ACTION_START_BACKGROUND_CHECK";
    private static final int UPDATE_LOC_REQ_CODE = 0;
    private static final int CHECK_RESP_REQ_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            Intent intentService = null;

            if (intent.getAction().equals(ACTION_UPDATE_LOCATION_SERVICE)) {
                intentService = UpdateLocationService.createUpdateIntent(context);
            }
            else if (intent.getAction().equals(ACTION_START_BACKGROUND_CHECK)){
                intentService = CheckResponseService.createCheckIntent(context);
            }

            if (intentService != null) {
                context.startService(intentService);
            }
        }catch (Exception e){
        }
    }

    public static void setAlarm(Context context, String purpose){

        AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent;
        if (purpose.equals("location")) {
            pendingIntent = getUpdateLocPendingIntent(context);
            alarmMan.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2000, 15000, pendingIntent);
        }
        else if (purpose.equals("response")){
            pendingIntent = getCheckPendingIntent(context);
            alarmMan.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2000, 10000, pendingIntent);
        }
    }

    public static void stopAlarm(Context context, String purpose){
        AlarmManager alarmMan = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent;
        if (purpose.equals("location")) {
            pendingIntent = getStopUpdateLocPendingIntent(context);
            alarmMan.cancel(pendingIntent);
        }
        else if (purpose.equals("response")){
            pendingIntent = getStopCheckPendingIntent(context);
            alarmMan.cancel(pendingIntent);
        }
    }

    private static PendingIntent getUpdateLocPendingIntent(Context context){

        Intent intent = new Intent(context, CastReceiver.class);
        intent.setAction(ACTION_UPDATE_LOCATION_SERVICE);
        return (PendingIntent.getBroadcast(context, UPDATE_LOC_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private static PendingIntent getCheckPendingIntent(Context context){
        Intent intent = new Intent(context, CastReceiver.class);
        intent.setAction(ACTION_START_BACKGROUND_CHECK);
        return (PendingIntent.getBroadcast(context, CHECK_RESP_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private static PendingIntent getStopUpdateLocPendingIntent(Context context){

        Intent intent = new Intent(context, CastReceiver.class);
//        intent.setAction(ACTION_UPDATE_LOCATION_SERVICE);
        return (PendingIntent.getBroadcast(context, UPDATE_LOC_REQ_CODE, intent, 0));
    }

    private static PendingIntent getStopCheckPendingIntent(Context context){
        Intent intent = new Intent(context, CastReceiver.class);
//        intent.setAction(ACTION_START_BACKGROUND_CHECK);
        return (PendingIntent.getBroadcast(context, CHECK_RESP_REQ_CODE, intent, 0));
    }
}