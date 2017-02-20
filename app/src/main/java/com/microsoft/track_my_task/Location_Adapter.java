package com.microsoft.track_my_task;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


class Location_Adapter extends BaseAdapter {
    My_Location ml = new My_Location();
    private ArrayList<String> result;
    private Context context;
    private ArrayList<LatLng> lat_lngs;
    private LatLng Lat_Lng;
    Database database;
    private static LayoutInflater inflater = null;
    public Location_Adapter(My_Location locations, ArrayList<String> my_loc, ArrayList<LatLng> lat_lng) {
        // TODO Auto-generated constructor stub
        result= my_loc;
        context = locations;
        lat_lngs = lat_lng;
        database = new Database(context);
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    private class Holder
    {
        TextView tv;
        ImageButton map;
        Button del_Location;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.customLocations, null);
        holder.tv=(TextView) rowView.findViewById(R.id.lw1);
        holder.del_Location = (Button)rowView.findViewById(R.id.del_loc);
        holder.map=(ImageButton) rowView.findViewById(R.id.map);
        holder.tv.setText(result.get(position));
        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Lat_Lng = lat_lngs.get(position);
                Intent intent = new Intent(context, View_Maps_Activity.class);
                Bundle args = new Bundle();
                args.putParcelable("Lat_Lng",Lat_Lng );
                String place_name = result.get(position);
                Log.i("place_name ", place_name);
                intent.putExtra("Place_name",place_name);

                intent.putExtra("bundle",args);
                context.startActivity(intent);
            }
        });
        holder.del_Location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("location_name = ",result.get(position));
                database.del_Location(result.get(position));
                Intent in = new Intent(context, My_Location.class);
                context.startActivity(in);

            }
        });


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "You Clicked "+result.get(position), Toast.LENGTH_LONG).show();
                Intent in = new Intent(context, Add_Task.class);
                Bundle args = new Bundle();
                in.putExtra("latitude", database.getLatitude(result.get(position)));
                in.putExtra("longitude", database.getLongitude(result.get(position)));

                String place = database.getPlace_name(result.get(position));
                Log.i("Place_name",place );
                in.putExtra("Place_name",place);
                context.startActivity(in);
            }
        });
        return rowView;
    }

}