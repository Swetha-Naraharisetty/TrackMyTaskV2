package com.microsoft.track_my_task;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class SettingsPage extends AppCompatActivity {

    Button save_changes, morning, evening;
    EditText proximity;
    Database database  = new Database(SettingsPage.this);
    int distance;
    String mor_time, eve_time;
    String timepicked;
    Cursor cursor;
    int mHour, mMinute;
    String TAG = "info";
    int db_dist;
    String db_mor;
    String db_eve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingspage);

        save_changes = (Button) findViewById(R.id.save);
        proximity = (EditText) findViewById(R.id.radius);
        morning = (Button) findViewById(R.id.mrng);
        evening = (Button) findViewById(R.id.eve);

        cursor = database.getSettings_sync();
        Log.i("info", "onCreate: "+ cursor.getInt(1)+cursor.getString(2)+cursor.getString(3));
        //proximity.setText(String.valueOf(cursor.getInt(1)));
        proximity.setText(String.valueOf(cursor.getInt(1)));
        morning.setText(cursor.getString(2));
        evening.setText(cursor.getString(3));

        mor_time = cursor.getString(2);
        eve_time = cursor.getString(3);

       morning.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Calendar c = Calendar.getInstance();
               mHour = c.get(Calendar.HOUR_OF_DAY);
               mMinute = c.get(Calendar.MINUTE);

               // Launch Time Picker Dialog
               TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsPage.this,
                       new TimePickerDialog.OnTimeSetListener() {

                           @Override
                           public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                               morning.setText(hourOfDay + ":" + minute);
                               eve_time = hourOfDay + ":" + minute;
                               Log.i(TAG, "onTimeSet: " + timepicked);
                           }
                       }, mHour, mMinute, false);

               timePickerDialog.show();
               Log.i("info", "onClick: "  + timepicked);
           }
       });
        evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsPage.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                                evening.setText(hourOfDay + ":" + minute);
                                mor_time = hourOfDay + ":" + minute;
                                Log.i(TAG, "onTimeSet: " + timepicked);
                            }
                        }, mHour, mMinute, false);

                timePickerDialog.show();
                Log.i("info", "onClick: " + timepicked);
            }
        });

        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    distance = Integer.valueOf(proximity.getText().toString());

                    Log.i(TAG, "onClick: " + distance + mor_time + eve_time);

                boolean changed = database.setSettings(distance, mor_time, eve_time);
                    if (!changed)
                        Toast.makeText(SettingsPage.this, "Failed to update changes", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(SettingsPage.this, "Updated Changes Succesfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingsPage.this, HomeActivity.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                }
        });

    }
    /*void showTimePicker(){
        // Get Current Time
        Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                        timepicked = (hourOfDay + ":" + minute);
                        Log.i(TAG, "onTimeSet: " + timepicked);
                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }*/
}

