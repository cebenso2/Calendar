package com.cbcb.chris.calendar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

public class EventActivity extends AppCompatActivity {
    private int minutes;
    private int hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
    }
    public void setTime(View view){
        TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                minutes=minute;
                hours=hourOfDay;
                TextView tc=(TextView)findViewById(R.id.textClock);
                tc.setText(createClockText(minute,hourOfDay));
            }
        };
        TimePickerDialog l=new TimePickerDialog(EventActivity.this,R.style.Theme_Dialog , t, 1, 1, false);
        l.show();
        Log.d("test","hello");
    }
    private String createClockText(int minute,int hourOfDay){
        String hour="12";
        String min="00";
        String meridiem="AM";
        if(minute>=10){
            min = String.valueOf(minute);
        }
        else {
            min = "0" + String.valueOf(minute);
        }

        if(hourOfDay>12) {
            hourOfDay -= 12;
            meridiem="PM";
        }
        hour=String.valueOf(hourOfDay);
        if(hourOfDay==0){
            hour="12";
            meridiem="AM";
        }
        return hour+":"+min+" "+meridiem;

    }
    public void addEventToDB(View view){
        EditText name=(EditText)findViewById(R.id.EventName);
        if(name.getText().toString().length()==0){
            Snackbar.make(view, "Enter a name for the Event", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        DataBaseHelper db = new DataBaseHelper(this);
        db.addEvent(new Event(1,name.getText().toString(),new Time(hours,minutes,0)));
        db.close();
        this.finish();
    }
}
