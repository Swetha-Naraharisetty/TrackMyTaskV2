package com.microsoft.track_my_task;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Ayshu on 23-Feb-17.
 * hgctgvhyv
 */

public class StartAllTasks_service extends Service {
    private static final String TAG = "info";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    Database db = new Database(StartAllTasks_service.this);
    Cursor cursor;

    ArrayList<String> task_names;
    ArrayList<LatLng> latLngs;
    ArrayList<Float> distances;

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

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Toast.makeText(getBaseContext(), "location changed", Toast.LENGTH_LONG).show();
            distances = new ArrayList<Float>();
            mLastLocation.set(location);
            //int i = 0;
            for(int i = 0; i < latLngs.size();i++){
                LatLng latLng = latLngs.get(i);
                float dist = (givedist(mLastLocation, latLng));
                Log.i(TAG, "onLocationChanged: " + latLng.longitude + latLng.latitude);

                distances.add(dist);
                if(dist < (db.getSettings_sync().getFloat(1))){
                    ProximityReceiver proximityReceiver = new ProximityReceiver();
                    proximityReceiver.generateNotification(StartAllTasks_service.this, task_names.get(i),i);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };
    @Override
    public void onCreate() {
        super.onCreate();
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
        initializeLocationManager();

    }

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
