package com.microsoft.track_my_task;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

//import android.support.v7.app.AppCompatActivity;

public class Add_Place extends Activity {
    EditText Place_name;
    Button map, save;
    GoogleApiClient mGoogleApiClient;
    Database database = new Database(Add_Place.this);
    LatLng latLng;
    SharedPreferences sp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_place);

        // initialistion of the widgets
        Place_name = (EditText)findViewById(R.id.task_name);
        map = (Button)findViewById(R.id.map);
        save = (Button)findViewById(R.id.save_place);
        sp  = this.getSharedPreferences("string", 0);


        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // locationChecker(mGoogleApiClient, Add_Place.this);
//                SharedPreferences.Editor editor = sp.edit();
//
//                String s1 = Place_name.getText().toString();
//                editor.putBoolean("string", true);
//                editor.putString("name",s1 );
//                editor.commit();
                Intent intent = new Intent(Add_Place.this, Maps_Place_Activity.class);
                Toast.makeText(Add_Place.this, "Maps", Toast.LENGTH_SHORT).show();
                Log.i("Place_name",String.valueOf(Place_name.getText()) );
                intent.putExtra("Place_name",(Place_name.getText()).toString());
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String s1 = sp.getString("name","my_place");
                //Place_name.setText(s1);
              /*  Bundle bundle = getIntent().getParcelableExtra("bundle");
                latLng = bundle.getParcelable("Lat_Lng");*/
                Double latitude = getIntent().getDoubleExtra("latitude", 0);
                Double longitude = getIntent().getDoubleExtra("longitude", 0);
                String Place = getIntent().getStringExtra("Place_name");
                latLng = new LatLng(latitude, longitude);

//                if(latLng == null){
//                    map.requestFocus();
//                    map.setError("no Map selected");
//                    Toast.makeText(Add_Place.this, "no Map selected", Toast.LENGTH_SHORT);
//                }else
                if(Place_name.getText().toString() == null){
                    Place_name.requestFocus();
                    Place_name.setError("Field Cannot be empty ");
                } else {
                    Log.i("Add_place lat lng", latLng.toString());
                    Log.i("Add_place place name ", Place_name.getText().toString());

                    // extracting latitude and longitude from LatLng object
                    //double latitude = latLng.latitude;
                    //double longitude = latLng.longitude;

                    //Inserting into the locations table
                    boolean isInsert = database.insert_location(Place_name.getText().toString(), latitude, longitude, Place);
                    Toast.makeText(Add_Place.this, Place, Toast.LENGTH_SHORT).show();

                    // verification for successful insertion
                    if (!isInsert) {
                        Toast.makeText(Add_Place.this, "location name  ALREADY EXISTS", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Add_Place.this, My_Location.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }






}
