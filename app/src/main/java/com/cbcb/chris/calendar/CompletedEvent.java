package com.cbcb.chris.calendar;

import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.ArrayList;

public class CompletedEvent extends AppCompatActivity {
    private int start_hour;
    private int start_min;
    private int end_hour;
    private int end_min;
    private Event event;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_event);
        start_hour=12;
        start_min=0;
        end_hour=12;
        end_min=0;

        TextView tv =(TextView)findViewById(R.id.event_name);
        int id=getIntent().getExtras().getInt("ID");
        DataBaseHelper db= new DataBaseHelper(this);
        event=db.getEvent(id);

        db.close();
        tv.setText(event.getName());
        displayQauntFields();

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
        notificationManager.cancel(event.getId());
        DataBaseHelper db= new DataBaseHelper(this);
        db.addEventData(event.getId(),new Time(start_hour,start_min,0),new Time(end_hour,end_min,0), getQuantFieldValues());
        db.showAllEvents();
        db.showEventCompletionData(event.getId());
        db.close();
        this.finish();
    }
    private ArrayList<Double> getQuantFieldValues(){
        ArrayList<Double> values=new ArrayList<Double>();
        LinearLayout insert_quant= (LinearLayout)findViewById(R.id.insert);
        for(int i=0; i<insert_quant.getChildCount();i++){
            EditText qi =(EditText)findViewById(i);
            values.add(Double.valueOf(qi.getText().toString()));
        }
        return values;
    }
    private void displayQauntFields(){
        LinearLayout insert_quant= (LinearLayout)findViewById(R.id.insert);
        insert_quant.removeAllViews();
        for(int i=0;i<event.getQuant_names().size();i++){
            View qf = getLayoutInflater().inflate(R.layout.quant_field_input,null);
            TextView qt=(TextView)qf.findViewById(R.id.quant_title);
            qt.setText(event.getQuant_names().get(i));
            EditText qi =(EditText)qf.findViewById(R.id.quant_input);
            qi.setHint(event.getQuant_units().get(i));
            qi.setId(i);
            insert_quant.addView(qf);
        }

    }

}
