package com.microsoft.track_my_task;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.*;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


//Successsfully pushed code
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int hour, am_pm, minutes;

     GoogleApiClient mGoogleApiClient;
    String[] sel_add = {"New Location", "New Task"};
    Database db;
    Cursor cursor;
    Context context;
    private final String TAG= "HomeActivity ";
    private ArrayList<String> tasks_today, tasks_pending, upcoming_list;
    private ListView view_today, view_pending, view_upcoming;
    TextView today, pending, upcoming, userName;
    Switch  startTracking;
    private static HomeActivity mInstance;
    Firebase parent, childSetting, settingsData, savedtask, lat, lon, place;
    Firebase savedloc, lat_loc, lon_loc, place_loc;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        mInstance = this;
        db = new Database(HomeActivity.this);
        userName = (TextView)header.findViewById(R.id.user_name);

        Firebase.setAndroidContext(this);
        firebaseAuth = FirebaseAuth.getInstance();

        parent = new Firebase("https://trackmytask-218b6.firebaseio.com/"
                + firebaseAuth.getCurrentUser().getUid());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(HomeActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i(TAG, "onConnectionFailed: unable to connect");
                        Toast.makeText(getBaseContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "onNavigationItemSelected: username" + firebaseAuth.getCurrentUser());
                if (firebaseAuth.getCurrentUser() == null) {
                    Log.i(TAG, "onAuthStateChanged:  logging out");
                    userName.setText("you have been logged out !!! ");
                    Intent intent = new Intent(HomeActivity.this, Google_signin.class);
                    startActivity(intent);
                }



            }
        };






        //if(firebaseAuth.getCurrentUser() != null){Toast.makeText(context, "current " + firebaseAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();}

        if(firebaseAuth.getCurrentUser() == null){

        }else {
            String email = firebaseAuth.getCurrentUser().getEmail();
            Log.i(TAG, "onCreate:  user name " + email);
            userName.setText(email.toString());

        }
        if((db.getSettings_sync()).getCount() == 0){
            initializeSettings();
        }
        //to check gps
        check_gps();
        //check initernet connection
        check_internet();




        view_today = (ListView)findViewById(R.id.home_today_list);
        view_pending = (ListView)findViewById(R.id.home_Pending_list);
        view_upcoming = (ListView) findViewById(R.id.home_upcoming_list);



        startTracking = (Switch) findViewById(R.id.Tracking);

        today = (TextView) findViewById(R.id.home_today);
        pending = (TextView) findViewById(R.id.home_pending);
        upcoming = (TextView) findViewById(R.id.home_upcoming);

        context = HomeActivity.this;
        tasks_today = new ArrayList<>();
        tasks_pending = new ArrayList<>();
        upcoming_list = new ArrayList<>();

        //based on date arrange tasks
        sortTasks_OnDate();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton Add_Icon = (FloatingActionButton) findViewById(R.id.add_icon);
        Add_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(HomeActivity.this);
                //.setTitle("Select an Option");
                adb.setItems(sel_add, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seltd_loc = Arrays.asList(sel_add).get(which);
                        // Toast.makeText(Home_Page.this, seltd_loc, Toast.LENGTH_SHORT).show();

                        if(seltd_loc.equals("New Location")){
                            Intent intent = new Intent(HomeActivity.this, Add_Place.class);
                            startActivity(intent);
                        } else if(seltd_loc.equals("New Task")){
                            Intent intent = new Intent(HomeActivity.this, Add_Task.class);
                            intent.putExtra("mode", "add");
                            startActivity(intent);
                        }
                    }
                });
                android.app.AlertDialog dialog = adb.create();
                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,broadcast);
        Log.i("alarm", " notify");

        // to generatenotification

        startTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Intent intent = new Intent(HomeActivity.this, StartAllTasks.class);
                Log.i(TAG, "onCheckedChanged: hiiiiiiii");
                if(isChecked){
                    startActivity(intent);
                }
                else{
                    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    Log.i(TAG, "onCheckedChanged: checkded off");
                    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                        if ("StartAllTasks_service".equals(service.service.getClassName())) {
                            Log.i(TAG, "onCheckedChanged: service stopped");
                            Log.i(TAG, "onCheckedChanged: " + service.service.getClassName());
                            stopService(new Intent(HomeActivity.this, StartAllTasks_service.class));
                        }
                    }
                }
            }
        });





    }

    void allTasks_sync(){
        Firebase saved_tasks, lat, lon, date, place;
        Cursor cursor2 = db.getTasks_sync();
        Log.i(TAG, "savedTask_sync: " + cursor2.getCount());
        Log.i(TAG, "savedTask_sync: " + cursor2.getColumnCount());
        childSetting = parent.child("current_tasks");
        if(cursor2.getCount() == 0){
            return;
        }
        while(cursor2.moveToNext()){

            saved_tasks = childSetting.child(cursor2.getString(0));
            lat = saved_tasks.child("latitude");
            lon = saved_tasks.child("longitude");
            date = saved_tasks.child("due_date");
            place_loc = saved_tasks.child("placename");
            date.setValue(cursor2.getString(1));

            lat.setValue(cursor2.getString(2));
            lon.setValue(cursor2.getString(3));
            place_loc.setValue(cursor2.getString(4));

        }

    }

    void settings_sync(){
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(this, "Failed to Sync", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor1 = db.getSettings_sync();
        Log.i(TAG, "settings_sync: syncing settings" + cursor1.getCount());
        for(int i = 0 ; i < cursor1.getColumnCount(); i++ ) {
            childSetting = parent.child("settings");
            settingsData = childSetting.child(cursor1.getColumnName(i));
            settingsData.setValue(cursor1.getString(i));
        }
    }

    void savedLocations_sync(){

        Cursor cursor2 = db.getLocations_sync();
        Log.i(TAG, "savedTask_sync: " + cursor2.getCount());
        Log.i(TAG, "savedTask_sync: " + cursor2.getColumnCount());
        childSetting = parent.child("saved_Locations");
        if(cursor2.getCount() == 0){
            return;
        }
        while(cursor2.moveToNext()){
            savedloc = childSetting.child(cursor2.getString(0));
            lat_loc = savedloc.child("latitude");
            lon_loc = savedloc.child("longitude");
            place_loc = savedloc.child("placename");
            lat_loc.setValue(cursor2.getString(1));
            lon_loc.setValue(cursor2.getString(2));
            place_loc.setValue(cursor2.getString(3));

        }
    }

    void savedTask_sync(){
        Log.i(TAG, "savedTask_sync: hi i am in savd task get");
        Cursor cursor2 = db.getSavedTask_sync();
        Log.i(TAG, "savedTask_sync: " + cursor2.getCount());
        Log.i(TAG, "savedTask_sync: " + cursor2.getColumnCount());
        childSetting = parent.child("saved_tasks");
        if(cursor2.getCount() == 0){
            return;
        }
        while(cursor2.moveToNext()){
            savedtask = childSetting.child(cursor2.getString(0));
            lat = savedtask.child("lat");
            lon = savedtask.child("lon");
            place = savedtask.child("place");
            Log.i(TAG, "savedTask_sync: " + cursor2.getString(0));
            lat.setValue(cursor2.getString(1));
            lon.setValue(cursor2.getString(2));
            place.setValue(cursor2.getString(3));
        }
    }

    void sync_all(){
        parent = new Firebase("https://trackmytask-218b6.firebaseio.com/"
                + firebaseAuth.getCurrentUser().getUid());

        Toast.makeText(HomeActivity.this, "started sync", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "sync_all: " + firebaseAuth.getCurrentUser().getEmail());
        //del all
        parent.child("current_tasks").removeValue();
        parent.child("saved_Locations").removeValue();
        parent.child("saved_tasks").removeValue();
        parent.child("settings").removeValue();
        //sync all
        settings_sync();
        savedTask_sync();
        savedLocations_sync();
        allTasks_sync();
        Toast.makeText(HomeActivity.this, "completed sync", Toast.LENGTH_SHORT).show();

    }
    void check_gps(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Goto Settings Page To Enable GPS",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
        }
    }

    void check_internet(){
        if (!isNetworkAvailable(HomeActivity.this)) {
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Connection Alert")
                    .setMessage("You are not connected to internet")
                    .setCancelable(false)
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    void sortTasks_OnDate(){
        tasks_today = db.getTodayTasks();
        tasks_pending = db.getPendingTasks();
        upcoming_list = db.getUpcomingTasks();


        if(!tasks_today.isEmpty()){
            today.setVisibility(View.VISIBLE);
            Log.i(TAG, "onCreate: today visibility" + today.getVisibility());
            view_today.setVisibility(View.VISIBLE);
            view_today.setAdapter(new ToDoList_Adapter(this, tasks_today));
        }
        if(!tasks_pending.isEmpty()) {
            pending.setVisibility(View.VISIBLE);
            view_pending.setVisibility(View.VISIBLE);
            view_pending.setAdapter(new ToDoList_Adapter(this, tasks_pending));
        }
        if(!upcoming_list.isEmpty()) {
            upcoming.setVisibility(View.VISIBLE);
            view_upcoming.setVisibility(View.VISIBLE);
            view_upcoming.setAdapter(new ToDoList_Adapter(this, upcoming_list));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public static synchronized HomeActivity getInstance() {
        return mInstance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(HomeActivity.this, SettingsPage.class);
            startActivity(intent);
        }
        if (id == R.id.action_restore) {
            //restores the settings
            if (firebaseAuth.getCurrentUser() == null) {
                Toast.makeText(HomeActivity.this, "You not logged in", Toast.LENGTH_SHORT).show();
            }
            else {
                if (true) {
                    Toast.makeText(HomeActivity.this, "started restore", Toast.LENGTH_SHORT).show();
                    db.truncate_all_tables();
                    restore_tasks();
                    restore_savedtasks();
                    restore_locations();
                    restore_settings();
                    sortTasks_OnDate();
                    //startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                }
            }

        }
        if (id == R.id.action_sync) {
            // sync the settings with cloud
            if (firebaseAuth.getCurrentUser() != null) {
                Log.i(TAG, "onOptionsItemSelected: " + firebaseAuth.getCurrentUser());
                sync_all();
            } else {
                Toast.makeText(HomeActivity.this, "Failed to sync you are not logged in", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.i(TAG, "onNavigationItemSelected: id = " + id);

        if (id == R.id.My_loc) {
            Intent intent = new Intent(HomeActivity.this, My_Location.class);
            startActivity(intent);
        } else if (id == R.id.sTasks) {
            Intent intent = new Intent(HomeActivity.this, SavedTasks.class);
            startActivity(intent);

        } else if (id == R.id.about) {
            //about track my task
            Intent intent = new Intent(HomeActivity.this, About.class);
            startActivity(intent);
        } else if(id == R.id.log_out){
            userName.setText("you haven't logged in yet !!! ");
            Log.i(TAG, "onNavigationItemSelected:  logout");
            //firebaseAuth.signOut();
                Log.i(TAG, "onNavigationItemSelected:  user logged in");
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            userName.setText("you have been logged out !!! ");
            Intent intent = new Intent(HomeActivity.this, Google_signin.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }





    public void initializeSettings(){
        Log.i(TAG, "initializeSettings: " + "inserting....");
        db.insertSettings();
    }

    public void restore_tasks(){
        parent.child("current_tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getKey();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    final String task_name = child.getKey();
                    Toast.makeText(context, "task name"  + task_name, Toast.LENGTH_SHORT).show();
                    Firebase temp = parent.child("current_tasks").child(task_name);
                    temp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, String> properties = dataSnapshot.getValue(Map.class);

                            if (properties != null) {
                                Log.i(TAG, "onDataChange: data change of current tasks");
                                Toast.makeText(HomeActivity.this, "onDataChange: data change of current tasks", Toast.LENGTH_SHORT).show();
                                Iterator properties_iter = properties.keySet().iterator();
                                if (properties_iter.hasNext()) {
                                    Log.i(TAG, "onDataChange: iterator has next");
                                    String due_date = properties.get("due_date");
                                    String lon = properties.get("longitude");
                                    String place_name = properties.get("placename");
                                    String lat = properties.get("latitude");
                                    Log.i(TAG, "onDataChange: " + due_date + " " + place_name +" " + lat +" " + lon);
                                    Toast.makeText(HomeActivity.this, "added : " + task_name + due_date + lat + lon + place_name, Toast.LENGTH_SHORT).show();
                                    db.insertTask((task_name), due_date, Double.parseDouble(lat), Double.parseDouble(lon), place_name);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void restore_savedtasks(){
        parent.child("saved_tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getKey();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    final String task_name = child.getKey();
                    Firebase temp = parent.child("saved_tasks").child(task_name);
                    temp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, String> properties = dataSnapshot.getValue(Map.class);

                            if (properties != null) {
                                Log.i(TAG, "onDataChange: data change of saved tasks");
                                Iterator properties_iter = properties.keySet().iterator();
                                if (properties_iter.hasNext()) {
                                    Log.i(TAG, "onDataChange: iterator has next");
                                    String lon = properties.get("lon");
                                    String place_name = properties.get("place");
                                    String lat = properties.get("lat");
                                    Log.i(TAG, "onDataChange: " + " " + place_name +" " + lat +" " + lon);
                                    db.insert_savedTasks(task_name, Double.parseDouble(lat), Double.parseDouble(lon), place_name);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void restore_locations(){
        parent.child("saved_Locations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getKey();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final String loc_name = child.getKey();
                    Firebase temp = parent.child("saved_Locations").child(loc_name);
                    temp.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map<String, String> properties = dataSnapshot.getValue(Map.class);

                            if (properties != null) {
                                Log.i(TAG, "onDataChange: data change of saved tasks");
                                Iterator properties_iter = properties.keySet().iterator();
                                if (properties_iter.hasNext()) {
                                    Log.i(TAG, "onDataChange: iterator has next");
                                    String lon = properties.get("longitude");
                                    String place_name = properties.get("placename");
                                    String lat = properties.get("latitude");
                                    Log.i(TAG, "onDataChange: " + " " + place_name + " " + lat + " " + lon);
                                    db.insert_location(loc_name, Double.parseDouble(lat), Double.parseDouble(lon), place_name);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void restore_settings(){
        if(parent.child("settings") == null){
            initializeSettings();
        }
        else{
            parent.child("settings").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> properties = dataSnapshot.getValue(Map.class);

                    if (properties != null) {
                        Log.i(TAG, "onDataChange: data change of saved tasks");
                        Iterator properties_iter = properties.keySet().iterator();
                        if (properties_iter.hasNext()) {
                            Log.i(TAG, "onDataChange: iterator has next");
                            String dist = properties.get("distance");
                            String eve = properties.get("evening_time");
                            String mrng = properties.get("morning");
                            Log.i(TAG, "onDataChange: " + " " + dist + " " + eve + " " + mrng);
                            if(dist != null && eve != null && mrng != null){db.setSettings(Integer.parseInt(dist),eve, mrng);}
                        }

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

    }


}
