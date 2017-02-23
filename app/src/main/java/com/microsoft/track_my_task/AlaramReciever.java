package com.microsoft.track_my_task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;


public class AlaramReciever extends BroadcastReceiver {
    int hour, am_pm, minutes;
    Database database ;
    Cursor cursor;
    String time;
    Notification notification;
    ArrayList<String> tasks_today, tasks_pending;
    private final int NOTIFICATION_ID = 237;
    private static int value = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        database = new Database(context);
        Intent notificationIntent = new Intent(context, Notify_TaskActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent  pending_intent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //to group notifications
      //  Notification.InboxStyle inboxStyle = new  Notification.InboxStyle();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        Calendar calendar = GregorianCalendar.getInstance();
        am_pm = calendar.get(Calendar.AM_PM);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        time = String.valueOf(hour) + ":" + String.valueOf(minutes);
        Log.i("hour in alarm", time);
        tasks_pending = database.getPendingTasks();
        tasks_today = database.getTodayTasks();

        cursor = database.getSettings_sync();
        Log.i(TAG, "onReceive: mor time " + cursor.getString(2));
        Log.i(TAG, "onReceive: eve time " + cursor.getString(3));

        if(!tasks_today.isEmpty()) {
            if (time.equals(cursor.getString(2))) {
                Log.i("In alarm Manager", "nofication is bein sent....");
               // inboxStyle.setBigContentTitle("Tasks to be completed..");
               // inboxStyle.addLine("hi events" + value);
                notification = builder
                        .setContentTitle("Track My Task")
                        .setContentText("Notifications from Track My Task")
                        .setTicker("Task Alert!")
                        .setSmallIcon(R.mipmap.track_my_task)
                        .setContentIntent(pending_intent)
                        .setAutoCancel(true)
                       // .setStyle(inboxStyle)
                        .build();



                sendAlert(context);
                notificationManager.notify("Track My task", NOTIFICATION_ID, notification );
            }
        }else  if(!tasks_pending.isEmpty()) {
            if (time.equals(cursor.getString(3))) {
                Log.i("In alarm Manager", "nofication is bein sent....");
                notification = builder
                        .setContentTitle("Track My Task")
                        .setContentText("Pending Tasks...")
                        .setTicker("Task Alert!")
                        .setSmallIcon(R.mipmap.track_my_task)
                        .setContentIntent(pending_intent)
                        .setAutoCancel(true)
                        .build();

                sendAlert(context);
                notificationManager.notify("Track My task", NOTIFICATION_ID, notification );

            }

        }
        database.close();

        // notificationManager.cancelAll();
    }
    void sendAlert(Context context){
        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, alert);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }





}
