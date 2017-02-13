package com.microsoft.track_my_task;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class AlaramReciever extends BroadcastReceiver {
    int hour, am_pm, minutes;

    @Override
    public void onReceive(Context context, Intent intent) {
        Database database = new Database(context);
        Intent notificationIntent = new Intent(context, Notify_TaskActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       /* TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(notificationIntent);*/

        //PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent  pending_intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Calendar calendar = GregorianCalendar.getInstance();
        am_pm = calendar.get(Calendar.AM_PM);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        Log.i("hour in alarm", String.valueOf(hour));

        if(hour == 10 || hour == 18 ){


          /*  RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.activity_notification);

            builder = new NotificationCompat.Builder(context.getApplicationContext())
                    .setContent(rv)
                    .setTicker("IranGrammy playing")
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.track_my_task);*/



            //rv.setTextViewText(R.id.title, item.getTitle());
           // rv.setTextViewText(R.id.text, item.getSinger());
           // rv.setOnClickPendingIntent(R.id.close, pending_intent.builder.setAutoCancel(true));

           // Notification notif = builder.build();
            //notif.bigContentView = rv;



            Log.i("In alarm Manager", "nofication is bein sent....");
            Notification notification = builder
                    .setContentTitle("Track My Task")
                    .setContentText("New Notification From Track My Task..")
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
           // notificationManager.cancelAll();


        }




    }
}
