package com.microsoft.track_my_task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapsActivityNewPlace extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PLACE_PICKER_REQUEST = 1;

    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    private static LatLng lat_lng ;
  //  Bundle extras;
    //Intent in = getIntent().getExtras();
//    String place_name = in.getStringExtra("Place_name");
    String place_name = null;
    private Marker place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_new_place);


       // extras = getIntent().getExtras();        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        // Log.i("place_name",in.getStringExtra("Place_name") );
       // place_name = extras.getString("Place_name");
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                Log.i("place", String.valueOf(place.getName()));
                String toastMsg = String.format("Place: %s", place.getName());
                Log.i("place LatLng", String.valueOf(place.getLatLng()));
                lat_lng = place.getLatLng();
                String address = place.getName().toString();
                Log.i("Add_place 12", "in  place Activity");

                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                //  onMapReady(mMap);

                Intent intent = new Intent(MapsActivityNewPlace.this, Add_Task.class);
                Toast.makeText(MapsActivityNewPlace.this, "Maps", Toast.LENGTH_SHORT);
                Log.i("Place_name",String.valueOf(lat_lng) );
                intent.putExtra("latitude", lat_lng.latitude);
                intent.putExtra("longitude", lat_lng.longitude);
                intent.putExtra("Place_name", address);
                startActivity(intent);
               // setResult(Activity.RESULT_OK,intent);
               // finish();



            }
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }
}
