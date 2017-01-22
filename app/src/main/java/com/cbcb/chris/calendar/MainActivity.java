package com.cbcb.chris.calendar;
//master
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    private ListView mainListView ;
    private CustomAdapter customAdapter ;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainListView = (ListView) findViewById( R.id.list_view_main );
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event e=(Event)parent.getAdapter().getItem(position);
                viewEventData(view,e);
            }
        });
        mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Event e=(Event)parent.getAdapter().getItem(position);
                Log.d("Delete Event Id", ""+e.getId());
                Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, e.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                pendingIntent.cancel();
                am.cancel(pendingIntent);

                DataBaseHelper db =new DataBaseHelper(getBaseContext());
                db.deleteEvent(e);
                showEvents();
                return true;
            }
        });
        showEvents();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onRestart(){
        super.onRestart();
        showEvents();
    }
    public void addEvent(View view) {
        Intent intent = new Intent(this, EventActivity.class);
        startActivity(intent);
    }
    public void editEvent(View view, Event e) {
        Intent intent = new Intent(this, EditEventActivity.class);
        intent.putExtra("ID",e.getId());
        startActivity(intent);
    }
    public void viewEventData(View view, Event e) {
        Intent intent = new Intent(this, DataActivity.class);
        intent.putExtra("ID",e.getId());
        startActivity(intent);
    }
    private void showEvents(){
        DataBaseHelper db = new DataBaseHelper(this);
        Log.d("Reading: ", "Reading all events..");
        ArrayList<Event> eventList = new ArrayList<Event>(db.getAllEvents());
        for (Event event : eventList) {
            Log.d("Event: ", event.toString());
        }
        customAdapter = new CustomAdapter(this, R.layout.simple_row, eventList);
        mainListView = (ListView) findViewById( R.id.list_view_main );
        mainListView.setAdapter( customAdapter );
        db.close();
    }




}
