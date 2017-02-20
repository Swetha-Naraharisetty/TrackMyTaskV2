package com.microsoft.track_my_task;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
//Successsfully pushed code
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int hour, am_pm, minutes;
    String[] sel_add = {"New Location", "New Task"};
    Database db;
    Cursor cursor;
    Context context;
    private final String TAG= "HomeActivity ";
    ArrayList<String> tasks_today, tasks_pending, upcoming_list;
    ListView view_today, view_pending, view_upcoming;
    TextView today, pending, upcoming;
    Switch  startTracking;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        view_today = (ListView)findViewById(R.id.task_list_today);
        view_pending = (ListView)findViewById(R.id.task_list_pending);
        view_upcoming = (ListView) findViewById(R.id.task_list_upcoming);


        startTracking = (Switch) findViewById(R.id.Tracking);

        today = (TextView) findViewById(R.id.today);
        pending = (TextView) findViewById(R.id.pending);
        upcoming = (TextView) findViewById(R.id.upcoming);

        db = new Database(HomeActivity.this);
        context = HomeActivity.this;
        tasks_today = new ArrayList<>();
        tasks_pending = new ArrayList<>();
        upcoming_list = new ArrayList<>();

        cursor = db.getTask();
        if(cursor.getCount() > 0) {
            Log.i(TAG, "count"+ String.valueOf(cursor.getCount()));

            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "onCreate: db date " + cursor.getString(1));
                    Date date = new Date(cursor.getString(1));
                    if (date.before(new Date(db.getDate("today")))) {
                        tasks_pending.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: pending" + date);
                        pending.setVisibility(View.VISIBLE);


                    } else
                    if (date.equals(new Date(db.getDate("today")))) {

                        tasks_today.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: today " + date);
                        today.setVisibility(View.VISIBLE);

                    } else {
                        upcoming_list.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: upcoming" + date);
                        upcoming.setVisibility(View.VISIBLE);
                    }
                } while (cursor.moveToNext());
                db.close();
            }
        }

        view_today.setAdapter(new ToDoList_Adapter(this, tasks_today  ));
        view_pending.setAdapter(new ToDoList_Adapter(this, tasks_pending));
        view_upcoming.setAdapter(new ToDoList_Adapter(this, upcoming_list ));

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
            //cal.set(Calendar.HOUR_OF_DAY, 9);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),AlarmManager.INTERVAL_DAY,broadcast);
            Log.i("alarm", " notify");


        startTracking.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if(isChecked){
                    Intent intent = new Intent(HomeActivity.this, Proximity_Alert_Activity.class);
                    startActivity(intent);
                }else{

                }

            }
        });


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

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
