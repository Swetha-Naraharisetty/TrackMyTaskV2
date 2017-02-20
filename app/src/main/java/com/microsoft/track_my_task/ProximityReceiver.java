package com.microsoft.track_my_task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Ayshu on 18-Feb-17.
 * hgctgvhyv
 */
//when a place is in proximity
public class ProximityReceiver extends BroadcastReceiver {
    private static final String TAG = "info";
    Location_represent lr = new Location_represent();
    @Override
    public void onReceive(Context arg0, Intent intent) {
            generateNotification(arg0);
        if (intent.getData() != null) {
            Log.i(TAG, "onReceive: "+ intent.getData().toString());
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            //give notification here
            Toast.makeText(arg0, "here in receier", Toast.LENGTH_LONG).show();

            Log.i(TAG, "onReceive: "+ "khvtgvyugvkjbhlufvhjnjvlyiihb");
            Log.i(TAG, "onReceive: "+ extras.getString("task_name"));
        }
    }


    void generateNotification( Context context){
        Database database = new Database(context);
        Intent notificationIntent = new Intent(context, Notify_TaskActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           /* TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(HomeActivity.class);
            stackBuilder.addNextIntent(notificationIntent);*/

        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pending_intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);


            Log.i(TAG, "generateNotification: ");
            Notification notification = builder
                    .setContentTitle("Track My Task")
                    .setContentText("You have a task here ...")
                    .setTicker("Task Alert!")
                    .setSmallIcon(R.mipmap.track_my_task )
                    .setContentIntent(pending_intent)
                    .setAutoCancel(true)
                    .build();

            try {
                Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, alert);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
}
