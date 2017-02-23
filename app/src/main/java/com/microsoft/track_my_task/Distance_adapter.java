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

import static com.google.android.gms.internal.zzs.TAG;


class Distance_adapter extends BaseAdapter {
    private int year, month, day;
    private Calendar calendar;
    private String dateView;
    private ArrayList<String> result;
    private Context context;
    private Database database;
    ArrayList<Float> distances;
    private static LayoutInflater inflater= null;
    private String[] sel_options = {"Delete"};

    public Distance_adapter(StartAllTasks startAllTasks, ArrayList<Float> distance, ArrayList<String> task_names) {
        // TODO Auto-generated constructor stub
        result = new ArrayList<>(task_names);
        context = startAllTasks;
        distances = new ArrayList<>(distance);
        database = new Database(context);
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TextView task_name, dist;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View rowView;
        rowView = inflater.inflate(R.layout.distance_adapter, null);

        Holder holder = new Holder();
        holder.task_name = (TextView)rowView.findViewById(R.id.task_name);
        holder.dist = (TextView)rowView.findViewById(R.id.distance);
        holder.task_name.setText(result.get(position));
        Log.i(TAG, "getView: " + position);
        //if(position < distances.size())
            holder.dist.setText(String.valueOf(distances.get(position)));
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(context);
                adb.setItems(sel_options, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seltd_loc = Arrays.asList(sel_options).get(which);
                        if(seltd_loc.equals("Delete")){
                            Log.i("task_name = ",result.get(position));
                            database.del_Task(result.get(position));
                            Intent intent = new Intent(context, StartAllTasks.class);
                            context.startActivity(intent);
                        }
                    }
                });
                android.app.AlertDialog dialog = adb.create();
                dialog.show();

            }
        });
        return rowView;
    }


}