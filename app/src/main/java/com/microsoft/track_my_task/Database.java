package com.microsoft.track_my_task;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ayshu on 05-Dec-16.
 * hgctgvhyv
 */
public class Database extends SQLiteOpenHelper {
    private static String TAG = "info";

    public Database(Context context) {
        super(context, "Database.db", null, 1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table task" + "( task_name TEXT PRIMARY KEY, due_date TEXT, latitude DOUBLE, longitude DOUBLE, Place_name TEXT)");
        //date fromat dd-mm-yyyy
        Log.i(TAG, "onCreate: table tasks created");
        db.execSQL("create table locations" + "(location_name TEXT PRIMARY KEY, latitude DOUBLE, longitude DOUBLE, Place_name TEXT)");
        Log.i(TAG, "onCreate: table location created");

        db.execSQL("create table Saved_tasks" + "( task_name TEXT PRIMARY KEY,  latitude DOUBLE, longitude DOUBLE, Place_name TEXT)");
        //date fromat dd-mm-yyyy
        Log.i(TAG, "onCreate: table Saved_tasks created");
        db.execSQL("create table settings" + "(key INTEGER,  distance INTEGER, morning_time Text , evening_time Text)");
        Log.i(TAG, "onCreate: settings table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists task");
        db.execSQL("drop table if exists locations");
        db.execSQL("drop table if exists Saved_tasks");
        db.execSQL("drop table if exists settings");
        onCreate(db);
    }

    // Inserting the TASK into table
    public boolean insertTask(String task_name, String due_date, Double latitude, Double longitude, String Place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.i(TAG, "inserValues: " + task_name + " date : " + due_date + "latitude: " + latitude + "longitude : " + longitude + "place " + Place);

        if (task_name == null || due_date == null || latitude == null || longitude == null)
            return false;
        else {

            contentValues.put("task_name", task_name);
            contentValues.put("due_date", due_date);
            contentValues.put("latitude", latitude);
            contentValues.put("longitude", longitude);
            contentValues.put("Place_name", Place);


            Log.i(TAG, "insertValues: inserting");
            Log.i(TAG, "inserValues: " + task_name + " date : " + due_date + "latitude: " + latitude + "longitude : " + longitude);

            long result = db.insert("task", null, contentValues);

            Log.i(TAG, "insertValues: inserted");
            db.close();
            if (result == -1)
                return false;
            else
                return true;

        }

    }

    // to delete a task
    public void del_Task(String task_name) {
        Log.i("task_name db = ", task_name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from  task where task_name = ?", new String[]{"" + task_name});
        db.close();

    }

    // to get task and due date from table
    public Cursor getTask() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> tasks = new ArrayList<>();
        String query = "select task_name, due_date from task";
        Cursor cursor = db.rawQuery(query, null);
        Log.i(TAG, "getTasks: " + cursor.getCount());
        cursor.moveToNext();
        db.close();
        return cursor;

    }

    public Cursor getTasks_sync(){
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from task";
        Cursor cursor =  db.rawQuery(query, null);
        cursor.moveToNext();
        return cursor;
    }

    // to reschedule the task
    public boolean UpdateTask(String task_name, String due_date) {
        ContentValues contentValues = new ContentValues();
        LatLng lat_lng = getLatLngTask(task_name);
        Double latitude = lat_lng.latitude;
        Double longitude = lat_lng.longitude;
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        String placename= getTaskPlace(task_name);
        Log.i(TAG, "UpdateTask: " + placename);
        contentValues.put("Place_name", placename);

        SQLiteDatabase db1 = this.getWritableDatabase();
        //String  query = "UPDATE task SET due_date =  "+ due_date +"  WHERE " + "task_name=  " + task_name ;
        Log.i(TAG, "UpdateTask: "+ task_name);
        //if(IsTask(task_name)){
        Log.i(TAG, "UpdateTask: " + "updatinggg");
        contentValues.put("task_name", task_name);
        contentValues.put("due_date", due_date);
        int result = db1.update("task", contentValues, "task_name = ?", new String[]{task_name});
        Log.i("update result", String.valueOf(result));
        db1.close();
        if(result > 0)
            return true;
         else
            return false;
    }

    // to get date whether to know that duration of the task
    public String getDate(String day){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        if(day.equals("today")){
            Log.i(TAG, "onCreate: today " +  dateFormat.format(date).toString());
            return dateFormat.format(date).toString();
        }

        return null;
    }

    // get latLng of a task
    LatLng getLatLngTask(String Task_name){
        Log.i(TAG, "getLatLngTask: " + Task_name);
        SQLiteDatabase db = this.getReadableDatabase();
        LatLng latLng = new LatLng(0, 0);
        //if(IsTask(Task_name)){
        double lat = getLatitudeT(Task_name);
        double log = getLongitudeT(Task_name);
        latLng = new LatLng(lat, log);

        //}
        db.close();
        return latLng;
    }

    //Check whether the task exists or not
    boolean IsTask(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i("Is task", Task_name);
        Cursor cursor = db.rawQuery("SELECT * from task where task_name = ? ",new String[] {"" +Task_name} );
        boolean isAvail = Boolean.getBoolean(String.valueOf(cursor.getCount()));
        Log.i("ccount = ", String.valueOf(cursor.getCount()));
        db.close();
        if(!isAvail)
            return  false;
        else
            return  true;
    }

    // to get latitude from task
    private Double getLatitudeT(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT latitude from task where task_name = ? ",new String[] {"" +Task_name} );
        cursor.moveToLast();
        double lat = cursor.getDouble(0);
        Log.i(TAG, "getLatitudeT: " + lat);
        db.close();
        return lat;

    }

    // to get longitude from task
    private Double getLongitudeT(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT longitude from task where task_name = ? ",new String[] {"" +Task_name} );
        cursor.moveToLast();
        double longitude = cursor.getDouble(0);
        Log.i(TAG, "getLongitudeT: " + longitude);
        db.close();
        return longitude;

    }

    //TO GET LATLNGS FROM tasks table
    ArrayList<LatLng> getLatLngsT(){
        SQLiteDatabase db = this.getReadableDatabase();
        LatLng latLng;
        ArrayList<LatLng> lngArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM task", null);
        if(cursor.moveToFirst()){
            do{
                double lat = cursor.getDouble(0);
                Log.i(TAG, "getLatLngs: "+ String.valueOf(lat));
                double logn = cursor.getDouble(1);
                Log.i(TAG, "getLatLngs: "+ String.valueOf(logn));
                latLng = new LatLng(lat, logn);
                lngArrayList.add(latLng);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return lngArrayList;
    }

    // Inserting into LOCATION TABle
    public boolean insert_location(String location_name, Double latitude , Double longitude, String Place_name ){
        SQLiteDatabase db = this.getWritableDatabase();
        if(location_name == null || latitude == null || longitude == null || Place_name == null  ){
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("location_name", location_name);
        contentValues.put("latitude", latitude );
        contentValues.put("longitude", longitude );
        contentValues.put("Place_name", Place_name);
        Log.i(TAG, "inserValues: " + location_name + "latitude: " + latitude + "longitude : "+ longitude + "Place : " + Place_name);

        long result =  db.insert("locations", null, contentValues);
        Log.i(TAG, "insertValues_location: inserted");
        db.close();
        if(result == -1)
            return  false;
        else
            return true;

    }

    // deelete locations from the table
    public void del_Location(String loc_name) {
        Log.i("location name  db = ", loc_name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from  locations where location_name = ?", new String[] {"" + loc_name});
        db.close();
    }

    //TO GET LOCATIONS FROM TABLE
    public Cursor getLocation(){

        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "select * from location";
        ArrayList<String> list = new ArrayList<>();
        Log.i("In getLocations()", "before query");

        Cursor cursor = db.rawQuery("SELECT location_name FROM locations", null);
        Log.i("locations count", String.valueOf(cursor.getCount()));

        db.close();
        return cursor;
    }

    Cursor getLocations_sync(){
        SQLiteDatabase db = this.getReadableDatabase();
        //String query = "select * from location";
        ArrayList<String> list = new ArrayList<>();
        Log.i("In getLocations()", "before query");
        Cursor cursor = db.rawQuery("SELECT * FROM locations", null);
        Log.i("locations count", String.valueOf(cursor.getCount()));
        cursor.moveToNext();
        return cursor;
    }
    ArrayList<LatLng> getLatLngsLocations(){
        SQLiteDatabase db = this.getReadableDatabase();
        LatLng latLng;
        ArrayList<LatLng> lngArrayList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT latitude, longitude FROM locations", null);
        if(cursor.moveToFirst()){
            do{
                double lat = cursor.getDouble(0);
                Log.i(TAG, "getLatLngs: "+ String.valueOf(lat));
                double logn = cursor.getDouble(1);
                Log.i(TAG, "getLatLngs: "+ String.valueOf(logn));
                latLng = new LatLng(lat, logn);
                lngArrayList.add(latLng);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return lngArrayList;
    }

    // to get latlng for a specific location
    LatLng getLatLng(String location_name){
        SQLiteDatabase db = this.getReadableDatabase();
        LatLng latLng;
        if(IsLocation(location_name)){
            double lat = getLatitude(location_name);
            double log = getLongitude(location_name);
            latLng = new LatLng(lat, log);

        }else{
            latLng = new LatLng(0, 0);
        }
        db.close();
        return latLng;
    }

    // TO check if the location exists or not
    private boolean IsLocation(String location_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i("islocation", location_name);
        Cursor cursor = db.rawQuery("SELECT * from locations where location_name = ? ",new String[] {"" +location_name} );
        boolean isAvail = Boolean.valueOf(String.valueOf(cursor.getCount()));
        Log.i("is avail", String.valueOf(cursor.getCount()));
        db.close();
        if(!isAvail)
            return  false;
        else
            return  true;
    }

    // to get latitude from Locations
    Double getLatitude(String location_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT latitude from locations where location_name = ? ",new String[] {"" +location_name} );
        cursor.moveToLast();
        double lat = cursor.getDouble(0);
        db.close();
        return lat;

    }
    // to get longitude from Locations
    Double getLongitude(String location_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT longitude from locations where location_name = ? ",new String[] {"" +location_name} );
        cursor.moveToLast();
        double longitude = cursor.getDouble(0);
        db.close();
        return longitude;

    }

    String  getPlace_name(String location_name){
        SQLiteDatabase db = this.getReadableDatabase();
        String Place = null;
        Log.i("Location Place name", location_name);
        //if(IsLocation(location_name)){
        // Log.i("Location Place", Place);
        Cursor cursor = db.rawQuery("SELECT Place_name from locations where location_name = ? ",new String[] {"" +location_name} );
        if(cursor.moveToLast()){

            Place = cursor.getString(0);
            Log.i("lat = ", String.valueOf(Place));


            Log.i("R count = ", String.valueOf(cursor.getCount()));
            // Place = cursor.getString(4);
            Log.i("Task Place name", cursor.getString(0));
        }
        //}
        db.close();
        return Place;
    }

    String getTaskPlace(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i("Task Place name", Task_name);
        String Place = "not available";
        // if(IsTask(Task_name)){
        Cursor cursor = db.rawQuery("SELECT Place_name from task where task_name = ? ",new String[] {"" +Task_name} );
        if(cursor.moveToLast()){

            Place = cursor.getString(0);
            Log.i("lat = ", String.valueOf(Place));


            Log.i("R count = ", String.valueOf(cursor.getCount()));
            // Place = cursor.getString(4);
            Log.i("Task Place name", cursor.getString(0));
        }
        db.close();
        return Place;
    }

    // to save a task
    public boolean save_Task(String task_name){
        Double lat, log;
        String Place;
        lat = getLatitudeT(task_name);
        log = getLongitudeT(task_name);
        Place = getTaskPlace(task_name);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.i("in savee task", "trying to save");

        Log.i(TAG, "inserValues: " + task_name + " latitude: " + lat + " longitude : "+ log + " place : " + Place);
        if(task_name == null  || lat == null || log == null || Place == null)
            return false;
        else
        {
            contentValues.put("task_name", task_name);
            contentValues.put("latitude",lat);
            contentValues.put("longitude",log);
            contentValues.put("Place_name", Place);
            Log.i(TAG, "Saved tasks: inserting");
            Log.i(TAG, "inserValues: " + task_name + " latitude: " + lat + " longitude : "+ log + " place : " + Place);
            try {
                //long result = db.insert("Saved_tasks", null, contentValues);
                long result = db.insertOrThrow("Saved_tasks", null, contentValues);
                db.close();
                if (result == -1)
                    return false;
                else
                    return true;
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }


        }


    }

    //del saved task
    public void del_savedT(String task_name){
        Log.i("task_name db = ", task_name);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE from  Saved_tasks where task_name = ?", new String[] {"" +task_name});
        db.close();
    }

    //get all saved tasks
    public ArrayList<String> getSavedTasks(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> sTasks = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT task_name FROM Saved_tasks", null);
        if(cursor.moveToFirst()){
            do{

                String task = cursor.getString(0);
                sTasks.add(task);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return sTasks;
    }

    Double getLatitudeST(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT latitude from Saved_tasks where task_name = ? ",new String[] {"" +Task_name} );
        cursor.moveToLast();
        double lat = cursor.getDouble(0);
        Log.i(TAG, "getLatitudeT: " + lat);
        db.close();
        return lat;

    }
    // to get longitude from task
     Double getLongitudeST(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT longitude from Saved_tasks where task_name = ? ",new String[] {"" +Task_name} );
        cursor.moveToLast();
        double longitude = cursor.getDouble(0);
        Log.i(TAG, "getLongitudeT: " + longitude);
        db.close();
        return longitude;

    }

    Cursor getSavedTask_sync(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> sTasks = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM Saved_tasks", null);
        cursor.moveToNext();
        return cursor;
    }

    String getSTaskPlace(String Task_name){
        SQLiteDatabase db = this.getReadableDatabase();
        Log.i(TAG, "getSTaskPlace: 6" + Task_name
        );
        String Place = "";
        // if(IsTask(Task_name)){
        Cursor cursor = db.rawQuery("SELECT Place_name from Saved_tasks where task_name = ? ",new String[] {"" +Task_name} );
        if(cursor.moveToLast()){

              Place = cursor.getString(0);
            Log.i(TAG, "getSTaskPlace: " + cursor.getString(0));


            Log.i(TAG, "getSTaskPlace: " + cursor.getCount());
            // Place = cursor.getString(4);
            Log.i("Task Place name", cursor.getString(0));
        }
        db.close();
        return Place;
    }


    public boolean setSettings(int distance, String morning_time, String  evening_time  ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.i(TAG, "inserValues:  distance =  " + distance + "morning time  = " + morning_time + "evening time  = " + evening_time   );

        if(distance < 0 && morning_time == null && evening_time == null  )
            return false;
        else {
            Cursor cursor = getSettings_sync();

            contentValues.put("key", 1);
            contentValues.put("distance", distance);
            contentValues.put("morning_time", (morning_time));
            contentValues.put("evening_time", (evening_time));


            Log.i(TAG, "insertValues: inserting");
            Log.i(TAG, "inserValues:  distance =  " + distance + "morning time string = " + String.valueOf(morning_time) + "evening time  = " + evening_time);

            long result = db.update("settings", contentValues, "key = ?",new String[] {"1"});

            Log.i(TAG, "insertValues: inserted");
            db.close();
            if (result == -1)
                return false;
            else
                return true;

        }


    }

    public boolean insertSettings(){
            SQLiteDatabase db = getWritableDatabase();
            Log.i(TAG, "setSettings: inserting");
            String query = "insert into settings values(1,1000,'7:00am','8:00pm')";
            db.execSQL(query);
            return true;
        }

    Cursor getSettings_sync(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from settings";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToNext();
        Log.i(TAG, "settings: returining from cursor" +cursor.getColumnCount() + cursor.getCount());// cursor.getString(2) + cursor.getString(3) + cursor.getInt(0) + cursor.getInt(1) );
        return cursor;
    }

    ArrayList<String> getTodayTasks(){
        Log.i(TAG, "getTodayTasks: ");
        ArrayList<String> tasks_today = new ArrayList<>();
        Cursor cursor = getTask();
        Log.i(TAG, "getTodayTasks: count" + cursor.getCount());
        if(cursor.getCount() > 0) {
            Log.i(TAG, "count" + String.valueOf(cursor.getCount()));
            tasks_today = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "onCreate: database date " + cursor.getString(1));
                    Date date = new Date(cursor.getString(1));
                    if (date.equals(new Date(getDate("today")))) {
                        Log.i(TAG, "getTodayTasks: tasks" + cursor.getString(0));
                        tasks_today.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: today " + date);

                    }
                } while (cursor.moveToNext());

            }
        }
        return tasks_today;
    }



    ArrayList<String> getPendingTasks(){
        Log.i(TAG, "getPendingTasks: ");
        ArrayList<String> tasks_pending = new ArrayList<>();
        Cursor cursor = getTask();
        Log.i(TAG, "getPendingTasks:  count" + cursor.getCount());
        if(cursor.getCount() > 0) {
            Log.i(TAG, "count" + String.valueOf(cursor.getCount()));

            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "onCreate: database date " + cursor.getString(1));
                    Date date = new Date(cursor.getString(1));
                    if (date.before(new Date(getDate("today")))) {
                        Log.i(TAG, "getPendingTasks: tasks" + cursor.getString(0));
                        tasks_pending.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: pending" + date);
                    }
                } while (cursor.moveToNext());

            }
        }
        return tasks_pending;
    }



    ArrayList<String> getUpcomingTasks(){
        Log.i(TAG, "getUpcomingTasks: ");
        ArrayList<String> tasks_upcoming = new ArrayList<>();
        Cursor cursor = getTask();
        Log.i(TAG, "getUpcomingTasks: count" + cursor.getCount());
        if(cursor.getCount() > 0) {
            Log.i(TAG, "count" + String.valueOf(cursor.getCount()));

            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "onCreate: database date " + cursor.getString(1));
                    Date date = new Date(cursor.getString(1));
                    if (date.after(new Date(getDate("today")))) {
                        Log.i(TAG, "getUpcomingTasks: tasks" + cursor.getString(0));
                        tasks_upcoming.add(cursor.getString(0));
                        Log.i(TAG, "onCreate: pending" + date);
                    }
                } while (cursor.moveToNext());

            }
        }
        return tasks_upcoming;
    }

    boolean truncate_all_tables(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from task");
        db.execSQL("delete from locations");
        db.execSQL("delete from saved_tasks");
        db.execSQL("delete from settings");
        return true;
    }

    public boolean insert_savedTasks(String task_name, Double latitude , Double longitude, String Place_name ){
        SQLiteDatabase db = this.getWritableDatabase();
        if(task_name == null || latitude == null || longitude == null || Place_name == null  ){
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("task_name", task_name);
        contentValues.put("latitude", latitude );
        contentValues.put("longitude", longitude );
        contentValues.put("Place_name", Place_name);
        Log.i(TAG, "inserValues: " + task_name + "latitude: " + latitude + "longitude : "+ longitude + "Place : " + Place_name);

        long result =  db.insert("saved_tasks", null, contentValues);
        Log.i(TAG, "insertValues_location: inserted");
        db.close();
        if(result == -1)
            return  false;
        else
            return true;

    }




}
