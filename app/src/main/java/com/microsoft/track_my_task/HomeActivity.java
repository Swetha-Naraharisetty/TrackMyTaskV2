package com.microsoft.track_my_task;

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
import android.widget.ToggleButton;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

//Successsfully pushed code
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int hour, am_pm, minutes;
    String[] sel_add = {"New Location", "New Task"};
    Database db;
    Cursor cursor;
    Context context;
    private final String TAG= "HomeActivity ";
    private ArrayList<String> tasks_today, tasks_pending, upcoming_list;
    private ListView view_today, view_pending, view_upcoming;
    TextView today, pending, upcoming;
    Switch  startTracking;
    private static HomeActivity mInstance;
    Firebase parent, childSetting, settingsData, savedtask, lat, lon, place;
    Firebase savedloc, lat_loc, lon_loc, place_loc;
    FirebaseAuth firebaseAuth;


    //initialize
    public HomeActivity(){
        }

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mInstance = this;
       Firebase.setAndroidContext(this);
       firebaseAuth = FirebaseAuth.getInstance();
       if(firebaseAuth.getCurrentUser() == null){
           Toast.makeText(this, "Failed to Sync", Toast.LENGTH_SHORT).show();
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

       db = new Database(HomeActivity.this);
       context = HomeActivity.this;
       tasks_today = new ArrayList<>();
       tasks_pending = new ArrayList<>();
       upcoming_list = new ArrayList<>();
       initializeSettings();

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // to get notification



        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Calendar calendar = GregorianCalendar.getInstance();
        am_pm = calendar.get(Calendar.AM_PM);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        Log.i("hour", String.valueOf(hour));
        Calendar cal = Calendar.getInstance();
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,broadcast);
        Log.i("alarm", " notify");


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
        parent = new Firebase("https://trackmytask-218b6.firebaseio.com/"
                + firebaseAuth.getCurrentUser().getUid());
        Cursor cursor2 = db.getTasks_sync();
        Log.i(TAG, "savedTask_sync: " + cursor2.getCount());
        Log.i(TAG, "savedTask_sync: " + cursor2.getColumnCount());
        childSetting = parent.child("saved_tasks");
        if(cursor2 == null){
            return;
        }
        do{
            saved_tasks = childSetting.child(cursor2.getString(0));
            lat = saved_tasks.child("latitude");
            lon = saved_tasks.child("longitude");
            date = saved_tasks.child("due_date");
            place_loc = saved_tasks.child("placename");

            lat.setValue(cursor2.getString(1));
            lon.setValue(cursor2.getString(2));
            date.setValue(cursor2.getString(3));
            place_loc.setValue(cursor2.getString(4));

        }while(cursor2.moveToNext());

    }

    void settings_sync(){
        if(firebaseAuth.getCurrentUser() == null){
            Toast.makeText(this, "Failed to Sync", Toast.LENGTH_SHORT).show();
        }
        Cursor cursor1 = db.getSettings_sync();
        parent = new Firebase("https://trackmytask-218b6.firebaseio.com/"
                + firebaseAuth.getCurrentUser().getUid());

        for(int i = 0 ; i < cursor1.getColumnCount(); i++ ) {
            childSetting = parent.child("settings");
            settingsData = childSetting.child(cursor1.getColumnName(i));
            settingsData.setValue(cursor1.getString(i));
        }
    }

    void savedLocations_sync(){
        parent = new Firebase("https://trackmytask-218b6.firebaseio.com/"
                + firebaseAuth.getCurrentUser().getUid());
        Cursor cursor2 = db.getLocations_sync();
        Log.i(TAG, "savedTask_sync: " + cursor2.getCount());
        Log.i(TAG, "savedTask_sync: " + cursor2.getColumnCount());
        childSetting = parent.child("saved_Locations");
        if(cursor2 == null){
            return;
        }
        do{
            savedloc = childSetting.child(cursor2.getString(0));
            lat_loc = savedloc.child("latitude");
            lon_loc = savedloc.child("longitude");
            place_loc = savedloc.child("placename");
            lat_loc.setValue(cursor2.getString(1));
            lon_loc.setValue(cursor2.getString(2));
            place_loc.setValue(cursor2.getString(3));

        }while(cursor2.moveToNext());
    }

    void savedTask_sync(){
        Log.i(TAG, "savedTask_sync: hi i am in savd task get");

        parent = new Firebase("https://trackmytask-218b6.firebaseio.com/"
                + firebaseAuth.getCurrentUser().getUid());
        Cursor cursor2 = db.getSavedTask_sync();
        Log.i(TAG, "savedTask_sync: " + cursor2.getCount());
        Log.i(TAG, "savedTask_sync: " + cursor2.getColumnCount());
        childSetting = parent.child("savedtask");
        if(cursor2.getCount() == 0 ){
            return;
        }
        do{
            savedtask = childSetting.child(cursor2.getString(0));
            lat = savedtask.child("lat");
            lon = savedtask.child("lon");
            place = savedtask.child("place");
            Log.i(TAG, "savedTask_sync: " + cursor2.getString(0));
            lat.setValue(cursor2.getString(1));
            lon.setValue(cursor2.getString(2));
            place.setValue(cursor2.getString(3));
        }while(cursor2.moveToNext());

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
        if(id == R.id.action_restore){
            //restores the settings
        }
        if(id == R.id.action_sync){
            // sync the settings with cloud
             settings_sync();
             savedTask_sync();
             savedLocations_sync();
             allTasks_sync();
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.My_loc) {
            Intent intent = new Intent(HomeActivity.this, My_Location.class);
            startActivity(intent);
        } else if (id == R.id.sTasks) {
            Intent intent = new Intent(HomeActivity.this, SavedTasks.class);
            startActivity(intent);

        } else if (id == R.id.about) {
            //about track my task

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initializeSettings(){
        Cursor cursor1 = db.getSettings_sync();
        if(cursor1.getCount() != 0){
            return;
        }
        else{
            Log.i(TAG, "initializeSettings: " + "inserting....");
            db.insertSettings();

        }
    }

}
