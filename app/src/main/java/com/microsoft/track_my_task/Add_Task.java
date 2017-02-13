package com.microsoft.track_my_task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Add_Task extends Activity {
    Database database = new Database(this);
    double latitude, longitude ;
    LatLng TlatLng;
    int year, month, day;
    Calendar calendar;
    EditText task_name;
    Button set_date, place_pick, save;
    String Place;
    String dateView;
    String[] sel_location = {"My Locations", "Add new"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        // Iniatialisation of widgets
        set_date = (Button) findViewById(R.id.set_date);
        place_pick = (Button) findViewById(R.id.place_pick);
        save = (Button)findViewById(R.id.save_task);
        task_name = (EditText)findViewById(R.id.task_name);
        calendar = Calendar.getInstance();

        // extracting the current date from calender object
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);


        showDate(day, month + 1, year);

        // date picker to  get gue date
        set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(999);
            }
        });

        // allocates location for the task
        place_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(Add_Task.this);
                adb.setItems(sel_location, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seltd_loc = Arrays.asList(sel_location).get(which);
                        Toast.makeText(Add_Task.this, seltd_loc, Toast.LENGTH_SHORT).show();
                        if(seltd_loc.equals("My Locations")){
                            Intent intent = new Intent(Add_Task.this, My_Location.class);
                            intent.putExtra("task_name", task_name.getText().toString());
                            startActivity(intent);
                        }else if(seltd_loc.equals("Add new")){
                            Intent intent = new Intent(Add_Task.this, MapsActivityNewPlace.class);
                            intent.putExtra("task_name", task_name.getText().toString());
                            startActivity(intent);
                        }
                    }
                });
                AlertDialog dialog = adb.create();
                dialog.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             // Bundle bundle = getIntent().getParcelableExtra("bundle");

                latitude = getIntent().getDoubleExtra("latitude", 0);
                longitude = getIntent().getDoubleExtra("longitude", 0);
                Place = getIntent().getStringExtra("Place_name");

                Toast.makeText(Add_Task.this, Place, Toast.LENGTH_LONG).show();
                TlatLng = new LatLng(latitude, longitude);
                if(task_name == null){
                    task_name.requestFocus();
                    task_name.setError("Task name cannot be empty");
                }else if(TlatLng == null){
                    place_pick.requestFocus();
                    place_pick.setError("select a place");
                } if(new Date(dateView).before(new Date(database.getDate("today")))){
                    set_date.requestFocus();
                    set_date.setError("Pick a valid date");
                    Toast.makeText(Add_Task.this, "Pick a Valid Date", Toast.LENGTH_LONG).show();

                }else {
                    // inserting the task into task table
                    boolean isInsert = database.insertTask(task_name.getText().toString(), dateView, latitude, longitude, Place);
                    if (isInsert == false) {
                        Toast.makeText(Add_Task.this, "Task Name  ALREADY EXISTS", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent = new Intent(Add_Task.this, HomeActivity.class);
                        startActivity(intent);
                    }
                }

            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == 999){
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
          //  view.setMinDate(System.currentTimeMillis() - 1000);
            showDate(dayOfMonth, monthOfYear + 1, year);
        }

    };
    void showDate(int day, int month, int year){
        dateView = (new StringBuilder().append(day).append("/").append(month).append("/").append(year)).toString();
        set_date.setText(dateView);
        Toast.makeText(this, dateView, Toast.LENGTH_SHORT).show();
    }

   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                task_name.setText(data.getStringExtra("Place_name"));
                latitude = data.getDoubleExtra("latitude", 0);
                longitude = data.getDoubleExtra("longitude", 0);
                TlatLng = new LatLng(latitude, longitude);
                Log.i("lat_lng", TlatLng.toString());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult*/
}
