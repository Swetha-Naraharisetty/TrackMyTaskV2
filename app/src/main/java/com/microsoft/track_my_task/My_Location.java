package com.microsoft.track_my_task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

//import android.support.v7.app.AppCompatActivity;

public class My_Location extends Activity {
    ListView lv1;
    Context context;
    Cursor cursor;
    ArrayList<LatLng> latLngs = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    Database database = new Database(My_Location.this);
    Button back, addLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_locations);
        lv1 = (ListView)findViewById(R.id.location_list);
        back = (Button) findViewById(R.id.back_home);
        addLocation = (Button) findViewById(R.id.add_loc);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(My_Location.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(My_Location.this, Add_Place.class);
                startActivity(intent);
            }
        });
        cursor = database.getLocation();
        if(cursor.getCount() > 0) {
            Log.i( "count", String.valueOf(cursor.getCount()));

            if (cursor.moveToFirst()) {
                do {
                    list.add(cursor.getString(0));
                    Log.i("String ", list.toString());

                } while (cursor.moveToNext());
                database.close();
            }
        }



       // Toast.makeText(My_Location.this, "retrived", Toast.LENGTH_SHORT).show();
        latLngs = database.getLatLngsLocations();
        context=this;

        lv1.setAdapter(new Location_Adapter(this, list ,latLngs ));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String location_name  = parent.getItemAtPosition(position).toString();
                Log.i("location", location_name);
                Toast.makeText(My_Location.this, location_name,Toast.LENGTH_LONG).show();

            }

        });
    }
}
