package com.microsoft.track_my_task;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class SavedTasks extends AppCompatActivity {
    ListView saved_tasks;
    ArrayList<String> tasks = new ArrayList<String>();
    Database database = new Database(SavedTasks.this);
    String[] sel_options = {"schedule Task",  "Delete Task"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_tasks);

        saved_tasks = (ListView) findViewById(R.id.SavedTasks);
        tasks = database.getSavedTasks();
        ArrayAdapter adapter1 = new ArrayAdapter(SavedTasks.this, android.R.layout.simple_list_item_1, tasks);
        saved_tasks.setAdapter(adapter1);



        saved_tasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(SavedTasks.this);
                //.setTitle("Select an Option");
                adb.setItems(sel_options, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String seltd_loc = Arrays.asList(sel_options).get(which);
                        // Toast.makeText(Home_Page.this, seltd_loc, Toast.LENGTH_SHORT).show();
                        if(seltd_loc.equals("schedule Task")){
                            Intent intent = new Intent(SavedTasks.this, Reschedule_task.class);
                            intent.putExtra("task_name", parent.getItemAtPosition(position).toString());
                            startActivity(intent);
                        } else if(seltd_loc.equals("Delete Task")){
                            database.del_savedT(parent.getItemAtPosition(position).toString());
                            Toast.makeText(SavedTasks.this, "deleted task", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SavedTasks.this, SavedTasks.class);
                            startActivity(intent);
                        }
                    }
                });
                android.app.AlertDialog dialog = adb.create();
                dialog.show();
            }
        });


    }
}
