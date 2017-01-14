package com.cbcb.chris.calendar;

import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;

public class CompletedEvent extends AppCompatActivity {
    private int start_hour;
    private int start_min;
    private int end_hour;
    private int end_min;
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_event);
        start_hour=12;
        start_min=0;
        end_hour=12;
        end_min=0;

        TextView tv =(TextView)findViewById(R.id.event_name);
        id=getIntent().getExtras().getInt("ID");
        DataBaseHelper db= new DataBaseHelper(this);
        Event e=db.getEvent(id);
        db.close();

        tv.setText(e.getName());

    }
    public void setStartTime(View view){
        TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                start_min=minute;
                start_hour=hourOfDay;
                TextView tc=(TextView)findViewById(R.id.startTextClock);
                tc.setText(CalendarUtil.createClockText(minute,hourOfDay));
            }
        };
        TimePickerDialog l=new TimePickerDialog(CompletedEvent.this,R.style.Theme_Dialog , t, start_hour, start_min, false);
        l.show();
    }
    public void setEndTime(View view){
        TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                end_min=minute;
                end_hour=hourOfDay;
                TextView tc=(TextView)findViewById(R.id.endTextClock);
                tc.setText(CalendarUtil.createClockText(minute,hourOfDay));
            }
        };
        TimePickerDialog l=new TimePickerDialog(CompletedEvent.this,R.style.Theme_Dialog , t, end_hour, end_min, false);
        l.show();
    }
    public void saveData(View view){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
        DataBaseHelper db= new DataBaseHelper(this);
        db.addEventData(id,new Time(start_hour,start_min,0),new Time(end_hour,end_min,0), 1);
        db.showAllEventData();
        db.close();
        this.finish();
    }

}
