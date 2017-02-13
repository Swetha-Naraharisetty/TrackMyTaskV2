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

public class Notify_TaskActivity extends AppCompatActivity {

    ListView today_task;
    Database db = new Database(Notify_TaskActivity.this);
    Cursor cursor;
    int mode;
    String TAG = "TodayTask";
    ArrayList<String> tasks_today, tasks_pending;
    TextView  task_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notify_tasks);
        today_task = (ListView) findViewById(R.id.Notify_today);
        task_day = (TextView) findViewById(R.id.notify_task);

        cursor = db.getTask();
        if(cursor.getCount() > 0) {
            Log.i(TAG, "count"+ String.valueOf(cursor.getCount()));
            tasks_today = new ArrayList<>();
            tasks_pending = new ArrayList<>();

            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "onCreate: db date " + cursor.getString(1));
                    Date date = new Date(cursor.getString(1));
                    if (date.before(new Date(db.getDate("today")))) {
                        tasks_pending.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: pending" + date);

                    } else if (date.equals(new Date(db.getDate("today")))) {
                        tasks_today.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: today " + date);

                    }
                } while (cursor.moveToNext());
                db.close();
            }
        }
        Calendar calendar = GregorianCalendar.getInstance();
        mode = calendar.get(Calendar.AM_PM);
        Log.i("hour in alarm", String.valueOf( calendar.get(Calendar.AM_PM)));
        if(mode == 1){

            ArrayAdapter adapter1 = new ArrayAdapter(Notify_TaskActivity.this, android.R.layout.simple_list_item_1, tasks_pending);
            today_task.setAdapter(adapter1);
            task_day.setText(R.string.PendingS_Tasks);
        }else {

            ArrayAdapter adapter1 = new ArrayAdapter(Notify_TaskActivity.this, android.R.layout.simple_list_item_1, tasks_today);
            today_task.setAdapter(adapter1);
            task_day.setText(R.string.TodayS_Tasks);
        }

    }
}
