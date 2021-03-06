package com.cbcb.chris.calendar;
//master
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView mainListView ;
    private ArrayAdapter<String> listAdapter ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        showEvents();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRestart(){
        super.onRestart();
        showEvents();
    }
    /** Called when the user clicks the Send button */
    public void addEvent(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }
    private void showEvents(){
        DataBaseHelper db = new DataBaseHelper(this);
        Log.d("Reading: ", "Reading all events..");
        List<Event> events = db.getAllEvents();
        ArrayList<String> eventList = new ArrayList<String>();
        for (Event event : events) {
            String log = "Id: " + event.getId() + " ,Name: " + event.getName() + " ,Time: " + event.getTime().toString();
            Log.d("Event: : ", log);
            eventList.add(event.getName());
        }
        listAdapter = new ArrayAdapter<String>(this, R.layout.simple_row, eventList);
        mainListView = (ListView) findViewById( R.id.list_view_main );
        mainListView.setAdapter( listAdapter );
        db.close();
    }


}
