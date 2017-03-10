package com.microsoft.track_my_task;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Ayshu on 22-Feb-17.
 * hgctgvhyv
 */

public class StartAllTasks extends Activity {

    Location_represent lr = new Location_represent();
    private LocationManager mLocationManager;
    Location mLastLocation;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;


    String TAG = "info";

    Database db = new Database(StartAllTasks.this);
    Cursor cursor;

    ArrayList<String> task_names;
    ArrayList<LatLng> latLngs;
    ArrayList<Float> distances;
    ArrayAdapter<Float> arrayAdapter;
    ProgressDialog progressDialog;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startalltasks);
        listView = (ListView)findViewById(R.id.list_dist);

        Log.i(TAG, "onCheckedChanged: going to service");
        initializeLocationManager();
        cursor = db.getTask();
        task_names = new ArrayList<>();
        int temp = cursor.getCount();
        temp--;
        while(temp != -1){
            task_names.add(cursor.getString(0));
            cursor.moveToNext();
            temp--;
        }
        latLngs = db.getLatLngsT();
        progressDialog = new ProgressDialog(StartAllTasks.this);
        progressDialog.setMessage("Getting Location updates");
        progressDialog.show();
    }

    float givedist(Location location1, LatLng latLng){
        float [] dist = new float[5];
        Location.distanceBetween(location1.getLatitude(), location1.getLongitude(), latLng.latitude, latLng.longitude, dist);
        dist[0] = dist[0] / 1000;
        Log.i(TAG, "onLocationChanged: " + String.valueOf(dist[0]));
        Log.i(TAG, "onLocationChanged: " + String.valueOf(dist[1]));
        Log.i(TAG, "onLocationChanged: " + String.valueOf(dist[2]));
        Log.i(TAG, "onLocationChanged: " + String.valueOf(dist[3]));
        return dist[0];
    }

    class LocationListener implements android.location.LocationListener{

        public LocationListener(String provider){
            mLastLocation = new Location(provider);
        }
        @Override
        public void onLocationChanged(Location location) {
            progressDialog.dismiss();
            Log.i(TAG, "onLocationChanged: location changed");
            Log.i(TAG, "onLocationChanged: " + location.getLongitude() + location.getLatitude());
            Toast.makeText(getBaseContext(), "location changed", Toast.LENGTH_LONG).show();
            distances = new ArrayList<Float>();
            mLastLocation.set(location);
            for(int i = 0; i < latLngs.size();i++){
                LatLng latLng = latLngs.get(i);
                float dist = (givedist(mLastLocation, latLng));
                Log.i(TAG, "onLocationChanged: " + latLng.longitude + latLng.latitude);

                distances.add(dist);
                if(dist < (db.getSettings_sync().getFloat(1))){
                    ProximityReceiver proximityReceiver = new ProximityReceiver();
                    proximityReceiver.generateNotification(StartAllTasks.this, task_names.get(i), i);
                }
            }
            arrayAdapter = new ArrayAdapter<Float>(StartAllTasks.this,android.R.layout.simple_list_item_1, distances);
            listView.setAdapter(new Distance_adapter(StartAllTasks.this, distances, task_names));
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

    LocationListener[] mLocationListeners = new StartAllTasks.LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
            Log.i(TAG, "onCreate: location requested");
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
            Log.i(TAG, "onCreate: location requested");

        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
    //startService(new Intent(StartAllTasks.this, StartAllTasks_service.class));
}
