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

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
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
    String taskname;
    private int NOTIFICATION_ID = 233;
    private static int value = 0;
    @Override
    public void onReceive(Context arg0, Intent intent) {
        //generateNotification(arg0);
        //give notification here
        taskname = intent.getStringExtra("task_name");
        generateNotification(arg0, taskname, NOTIFICATION_ID);
        Log.i(TAG, "onReceive: task nae = "+ taskname);

    }


    public void generateNotification( Context context, String taskname1, int id){
        NOTIFICATION_ID = id;
        Database database = new Database(context);
        Intent notificationIntent = new Intent(context, StartAllTasks.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Notification.InboxStyle inboxStyle = new  Notification.InboxStyle();
        PendingIntent pending_intent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
       // inboxStyle.setBigContentTitle("Tasks to be completed..");
       // inboxStyle.addLine("hi events" + value);
        value++;
        Notification notification;

             notification = builder
                    .setContentTitle("Track My Task")
                    .setContentText("You have a task here ..." + taskname1)
                    .setTicker("Task Alert!")
                    .setSmallIcon(R.mipmap.track_my_task)
                    .setContentIntent(pending_intent)
                    .setAutoCancel(true)

                    .build();
            sendNotification(context, notification);

    }
    void sendNotification(Context context, Notification notification){

        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, alert);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify("Track My task", NOTIFICATION_ID, notification );

    }
}
