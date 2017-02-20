package com.microsoft.track_my_task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


class ToDoList_Adapter extends BaseAdapter {
    private int year, month, day;
    private Calendar calendar;
    private String dateView;
    private ArrayList<String> result;
    private Context context;
    private Database database;
    private String[] sel_options = {"Reschedule", "Start Task", "Save Task"};
    private static LayoutInflater inflater=null;
    public ToDoList_Adapter(HomeActivity to_do_list, ArrayList<String> tasks) {
        // TODO Auto-generated constructor stub
        result=tasks;
        context= to_do_list;
        database = new Database(context);
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(day, month + 1, year);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        Button del, edit;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.customTasks, null);
        holder.tv=(TextView) rowView.findViewById(R.id.lw1);
        holder.del=(Button) rowView.findViewById(R.id.del);
        holder.edit=(Button) rowView.findViewById(R.id.edit);
        holder.tv.setText(result.get(position));
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("task_name = ",result.get(position));

                database.del_Task(result.get(position));
                Intent in = new Intent(context,HomeActivity.class);
                context.startActivity(in);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Date Picker", "shown");
                ((Activity)ToDoList_Adapter.this.context).showDialog(999);
                LatLng latLng = database.getLatLngTask(result.get(position));
                Double lat = latLng.latitude;
                Double logn = latLng.longitude;
                Log.i("updated date : ", dateView);
                boolean isUpdate = database.UpdateTask(result.get(position),dateView );
                if(isUpdate)
                    Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(context, "Sorry , not updated", Toast.LENGTH_LONG).show();
                Intent in = new Intent(context, HomeActivity.class);
                context.startActivity(in);



            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result.get(position), Toast.LENGTH_LONG).show();
                    android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(context);
                    //.setTitle("Select an Option");
                    adb.setItems(sel_options, new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String seltd_loc = Arrays.asList(sel_options).get(which);
                            // Toast.makeText(Home_Page.this, seltd_loc, Toast.LENGTH_SHORT).show();


                            switch (seltd_loc){
                                case "Start Task" :{
                                                        String task = result.get(position);
                                                        Intent intent = new Intent(context, Location_represent.class);
                                                        intent.putExtra("task_name", task);
                                                        Log.i("Task onclick", task);
                                                        context.startActivity(intent);
                                                    }
                                case "Reschedule" : {
                                                        Intent intent = new Intent(context, Reschedule_task.class);
                                                        intent.putExtra("task_name", result.get(position));
                                                        context.startActivity(intent);
                                                    }
                                case "Save Task" : {
                                                        Log.i("in adapter", "before saving");
                                                        database.save_Task(result.get(position));
                                                   /* if(database.save_Task(result.get(position)) == false)
                                                        Toast.makeText(context, "Not Saved", Toast.LENGTH_SHORT).show();
                                                     else {
                                                        Intent intent = new Intent(context, SavedTasks.class);
                                                        context.startActivity(intent);
                                                    }*/
                                                     }

                            }

//                            if(seltd_loc.equals("Start Task")){
//                                String task = result.get(position);
//                                Intent intent = new Intent(context, Location_represent.class);
//                                intent.putExtra("task_name", task);
//
//                                Log.i("Task onclick", task);
//                                context.startActivity(intent);
//                            } else if(seltd_loc.equals("Reschedule")){
//                                Intent intent = new Intent(context, Reschedule_task.class);
//                                intent.putExtra("task_name", result.get(position));
//                                context.startActivity(intent);
//                        } else if(seltd_loc.equals("Save Task")){
//                                Log.i("in adapter", "before saving");
//                                database.save_Task(result.get(position));
//                               /* if(database.save_Task(result.get(position)) == false)
//                                    Toast.makeText(context, "Not Saved", Toast.LENGTH_SHORT).show();
//                                 else {
//                                    Intent intent = new Intent(context, SavedTasks.class);
//                                    context.startActivity(intent);
//                                }*/
//                        }
                    }
                });
                android.app.AlertDialog dialog = adb.create();
                dialog.show();
            }
        });
        return rowView;
    }

    protected Dialog onCreateDialog(int id) {
        if(id == 999){
            return new DatePickerDialog(context, myDateListener, year, month, day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            showDate(dayOfMonth, monthOfYear + 1, year);
        }
    };
    private void showDate(int year, int month, int day){
        dateView = (new StringBuilder().append(year).append("/").append(month).append("/").append(day)).toString();
        Toast.makeText(context, dateView, Toast.LENGTH_SHORT).show();
    }

}