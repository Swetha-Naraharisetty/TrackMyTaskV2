package com.microsoft.track_my_task;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class Reschedule_task extends AppCompatActivity {
    private static final String TAG =" Reschedule_task" ;
    TextView place_name, task_name;
   static   String RtaskName , mode, Place;
    Button set_Date, Update;
     double latitude, longitude;       ;
    int year, month, day;
    Calendar calendar;
    String dateView;
    Database database = new Database(Reschedule_task.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reschedule_task);

        place_name = (TextView) findViewById(R.id.Rplace_name);
        task_name = (TextView)findViewById(R.id.Rtask_name);
        set_Date = (Button) findViewById(R.id.Rset_date);
        Update = (Button) findViewById(R.id.update_task);
        RtaskName = getIntent().getStringExtra("task_name");
        mode = getIntent().getStringExtra("mode");
        task_name.setText(RtaskName);
        Log.i(TAG, "onCreate:  mode " + mode);
        calendar = Calendar.getInstance();

        // extracting the current date from calender object
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(day, month + 1, year);
        if(mode.equals("save")){
             Place = database.getSTaskPlace(task_name.getText().toString());
            Log.i(TAG, "onClick:  place name" + Place);
            place_name.setText(Place);
             latitude = database.getLatitudeST(task_name.getText().toString());
             longitude = database.getLongitudeST(task_name.getText().toString());

        }
        if(mode.equals("update")){

            Log.i("Rplace name", database.getTaskPlace(RtaskName));
            place_name.setText(database.getTaskPlace(RtaskName));
        }

        // date picker to  get gue date
        set_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mode.equals("save")) {
                    Log.i(TAG, "onClick:  in save mode" + mode);
                    if (new Date(dateView).before(new Date(database.getDate("today")))) {
                        set_Date.requestFocus();
                        set_Date.setError("Pick a valid date");
                        Toast.makeText(Reschedule_task.this, "Pick a Valid Date", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isInsert = database.insertTask(task_name.getText().toString(), dateView, latitude, longitude, Place);
                        if (isInsert == false) {
                            Toast.makeText(Reschedule_task.this, "Task Name  ALREADY EXISTS", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(Reschedule_task.this, HomeActivity.class);
                            startActivity(intent);
                        }

                    }
                }else if(mode.equals("update")) {
                    Log.i(TAG, "onClick:  in update" + mode);

                    if (database.UpdateTask(RtaskName, dateView)) {
                        Toast.makeText(Reschedule_task.this, "Successfully Updated ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Reschedule_task.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Reschedule_task.this, "Not Updated  ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    protected Dialog onCreateDialog(int id) {
        if(id == 999){
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            view.setMinDate(System.currentTimeMillis() - 1000);
            showDate(dayOfMonth, monthOfYear + 1, year);
        }

    };
    void showDate(int day, int month, int year){
        dateView = (new StringBuilder().append(day).append("/").append(month).append("/").append(year)).toString();
        set_Date.setText(dateView);
        Toast.makeText(this, dateView, Toast.LENGTH_SHORT).show();
    }
}
