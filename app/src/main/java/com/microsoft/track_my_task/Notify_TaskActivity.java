package com.microsoft.track_my_task;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class Notify_TaskActivity extends AppCompatActivity {

    ListView today_task;
    Database db = new Database(Notify_TaskActivity.this);
    Cursor cursor;
    String TAG = "TodayTask";
    ArrayList<String> tasks_today, tasks_pending;
    TextView  task_day;
    int hour, minutes;
    String time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_tasks);
        today_task = (ListView) findViewById(R.id.Notify_today);
        task_day = (TextView) findViewById(R.id.notify_task);


        Calendar calendar = GregorianCalendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        time = String.valueOf(hour) + ":" + String.valueOf(minutes);
        cursor = db.getSettings_sync();
        Log.i(TAG, "onReceive: mor time " + cursor.getString(2));
        Log.i(TAG, "onReceive: eve time " + cursor.getString(3));
        if(time.equals(cursor.getString(3))){
            tasks_pending = db.getPendingTasks();
            ArrayAdapter adapter1 = new ArrayAdapter(Notify_TaskActivity.this, android.R.layout.simple_list_item_1, tasks_pending);
            today_task.setAdapter(adapter1);
            task_day.setText(R.string.PendingS_Tasks);
        }else if(time.equals(cursor.getString(2))){
            tasks_today = db.getTodayTasks();
            ArrayAdapter adapter1 = new ArrayAdapter(Notify_TaskActivity.this, android.R.layout.simple_list_item_1, tasks_today);
            today_task.setAdapter(adapter1);
            task_day.setText(R.string.TodayS_Tasks);
        }

    }
}
