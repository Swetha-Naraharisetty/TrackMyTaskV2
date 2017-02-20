package com.microsoft.track_my_task;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Ayshu on 18-Feb-17.
 * hgctgvhyv
 */
//for all the tasks to get started at once
public class Proximity_Alert_Activity extends Activity {
    long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
    long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds
    long POINT_RADIUS = 100000; // in Meters
    long PROX_ALERT_EXPIRATION = 2000;
    ArrayList<LatLng> latLngs;
    Cursor cursor;
    private LocationManager locationManager;
    String TAG = "info";
    Database db = new Database(Proximity_Alert_Activity.this);
    Location_represent lr = new Location_represent();
    private final String PROX_ALERT = "wise.microsoft.com.track_my_task.PROXIMITY_ALERT";

    //settings alert
    public Proximity_Alert_Activity() {
        //constructor

    }


    public void addProximityAlert(double latitude, double longitude, String Task_name) {

        Intent intent = new Intent(PROX_ALERT);
        intent.putExtra("task_name", Task_name);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.addProximityAlert(
                latitude, // the latitude of the central point of the alert region
                longitude, // the longitude of the central point of the alert region
                POINT_RADIUS, // the radius of the central point of the alert region, in meters
                PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
        );
        IntentFilter filter = new IntentFilter(PROX_ALERT);
        registerReceiver(new ProximityReceiver(), filter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
       // lr.initializeLocationManager();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,

                MINIMUM_TIME_BETWEEN_UPDATE,

                MINIMUM_DISTANCECHANGE_FOR_UPDATE,

                new MyLocationListener()

        );

        latLngs = db.getLatLngsT();
        cursor = db.getTask();
        cursor.moveToNext();
        for (LatLng latLng: latLngs) {
            addProximityAlert(latLng.latitude, latLng.longitude, cursor.getString(0));
            Log.i(TAG, "onCreate: " + cursor.getString(0));
            cursor.moveToNext();
        }

    }
    class MyLocationListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: location changed");
            Toast.makeText(getBaseContext(), "location changed", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
