package com.microsoft.track_my_task;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

public class SettingsPage extends AppCompatActivity {

    Button save_changes;
    TimePicker morning, evening;
    EditText proximity;
    Database database  = new Database(SettingsPage.this);
    Integer mor_time, eve_time;
    Double distance;
    int hours;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);

        save_changes = (Button) findViewById(R.id.settings_save);
        /*customize = (Button) findViewById(R.id.customize);*/

        proximity = (EditText) findViewById(R.id.distance);



       // simpleTimePicker = (TimePicker)findViewById(R.id.simpleTimePicker); // initiate a time
      //  hours =simpleTimePicker.getCurrentHour(); // before api level 23
        // hours =simpleTimePicker.getHour(); // after api level 23


        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance = Double.valueOf(proximity.getText().toString());
               // mor_time = Integer.valueOf(morning.getText().toString());
               // eve_time = Integer.valueOf(evening.getText().toString());
               // hours = simpleTimePicker.getCurrentHour();

                boolean isInsert = database.insertSettings(distance, mor_time, eve_time);
                if(!isInsert)
                    Toast.makeText(SettingsPage.this, "Failed to update changes", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(SettingsPage.this, "Updated Changes Succesfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingsPage.this, HomeActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}
