package com.cbcb.chris.calendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        int id = getIntent().getIntExtra("ID", 0);
        showData(id);
    }

    private void showData(int id){
        DataBaseHelper db= new DataBaseHelper(this);
        Event e=db.getEvent(id);
        ((TextView)findViewById(R.id.event_label)).setText(e.getName());
        ArrayList<EventData> eds = db.getAllEventData(id);
        EventDataAdapter eda = new EventDataAdapter(this, R.layout.event_data_row, eds,e);
        ListView dataListView = (ListView) findViewById( R.id.list_view_data );
        dataListView.setAdapter( eda );
        db.close();

    }
}
