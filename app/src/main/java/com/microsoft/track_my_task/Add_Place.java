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
    String Place = "map";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_place);

        // initialistion of the widgets
        Place_name = (EditText)findViewById(R.id.task_name);
        map = (Button)findViewById(R.id.map);
        save = (Button)findViewById(R.id.save_place);
        sp  = this.getSharedPreferences("string", 0);

        Place = getIntent().getStringExtra("Place_name");
        if(Place != null)
            map.setText(Place);


        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Add_Place.this, Maps_Place_Activity.class);
                Toast.makeText(Add_Place.this, "Loading Maps..", Toast.LENGTH_SHORT).show();
                Log.i("Place_name",String.valueOf(Place_name.getText()) );
                intent.putExtra("Place_name",(Place_name.getText()).toString());
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double latitude = getIntent().getDoubleExtra("latitude", 0);
                Double longitude = getIntent().getDoubleExtra("longitude", 0);
                 Place = getIntent().getStringExtra("Place_name");
                map.setText(Place);
                latLng = new LatLng(latitude, longitude);

                if(Place_name.getText().toString() == null){
                    Place_name.requestFocus();
                    Place_name.setError("Field Cannot be empty ");
                } else {
                    Log.i("Add_place lat lng", latLng.toString());
                    Log.i("Add_place place name ", Place_name.getText().toString());

                    //Inserting into the locations table
                    boolean isInsert = database.insert_location(Place_name.getText().toString(), latitude, longitude, Place);
                    Toast.makeText(Add_Place.this, Place, Toast.LENGTH_SHORT).show();

                    // verification for successful insertion
                    if (!isInsert) {
                        Toast.makeText(Add_Place.this, "location name  ALREADY EXISTS", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(Add_Place.this, My_Location.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();

                        startActivity(intent);
                    }
                }
            }
        });
    }






}
