package com.cbcb.chris.calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.cbcb.chris.calendar.R.layout.repetition;

public class EventActivity extends AppCompatActivity {
    private int minutes;
    private int hours;
    private int type;
    private int freq;
    private int date;
    private boolean[] days_of_week;
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    private static final String[] type_dict={"day","week","month"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        type=1;
        freq=0;
        hours=12;
        minutes=0;
        date=12;
        days_of_week= new boolean[]{false,false,false,false,false,false,false};

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
        TimePickerDialog l=new TimePickerDialog(EventActivity.this,R.style.Theme_Dialog , t, hours, minutes, false);
        l.show();
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addEventToDB(View view){
        EditText name=(EditText)findViewById(R.id.EventName);
        if(name.getText().toString().length()==0){
            Snackbar.make(view, "Enter a name for the Event", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        String event_name=name.getText().toString();
        Time event_time=new Time(hours,minutes,0);
        DataBaseHelper db = new DataBaseHelper(this);
        db.addEvent(new Event(1,event_name,event_time,type,date,days_of_week,freq));
        db.close();
        scheduleNotification(getNotification(event_name),event_time);
        this.finish();
    }
    private void scheduleNotification(Notification notification, Time time) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, time.getHours());
        calendar.set(Calendar.MINUTE, time.getMinutes());
        calendar.set(Calendar.SECOND,time.getSeconds());
        Log.d("time",calendar.getTime().toString());
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Notification getNotification(String content) {

        String replyLabel = "test";
        Intent resultIntent = new Intent(this, EventActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        Notification.Action action =
                new Notification.Action.Builder(android.R.drawable.ic_menu_recent_history,
                        "Completed",resultPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
        Notification.Action action1 =
                new Notification.Action.Builder(android.R.drawable.ic_menu_recent_history,
                        "Failed",resultPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setColor(123);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.addAction(action);
        builder.addAction(action1);

        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.ic_menu_recent_history);
        return builder.build();
    }
    public void setRepetition(View view){
        final View child = getLayoutInflater().inflate(repetition,null);
        final PopupWindow popupWindow=new PopupWindow(child);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.update();
        Log.d("test","PopupWindow");
        Button b=(Button)child.findViewById(R.id.submit_repetition);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test","test");
                TextView tv=(TextView)findViewById(R.id.event_type);
                Spinner s=(Spinner)child.findViewById(R.id.event_type_spinner);
                type=s.getSelectedItemPosition();
                tv.setText(s.getSelectedItem().toString());
                s = (Spinner) child.findViewById(R.id.frequency_spinner);
                freq = s.getSelectedItemPosition();
                if(type==1) {
                    CheckBox cb = (CheckBox) child.findViewById(R.id.checkBox);
                    days_of_week[0] = cb.isChecked();
                    cb = (CheckBox) child.findViewById(R.id.checkBox1);
                    days_of_week[1] = cb.isChecked();
                    cb = (CheckBox) child.findViewById(R.id.checkBox2);
                    days_of_week[2] = cb.isChecked();
                    cb = (CheckBox) child.findViewById(R.id.checkBox3);
                    days_of_week[3] = cb.isChecked();
                    cb = (CheckBox) child.findViewById(R.id.checkBox4);
                    days_of_week[4] = cb.isChecked();
                    cb = (CheckBox) child.findViewById(R.id.checkBox5);
                    days_of_week[5] = cb.isChecked();
                    cb = (CheckBox) child.findViewById(R.id.checkBox6);
                    days_of_week[6] = cb.isChecked();
                }
                popupWindow.dismiss();
            }
        });
        final Spinner t= (Spinner)child.findViewById(R.id.event_type_spinner);
        t.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("test","Select");
                RelativeLayout rl=(RelativeLayout)child.findViewById(R.id.insert);
                rl.removeAllViews();
                if(position==0){
                    View child = getLayoutInflater().inflate(R.layout.daily_event_sub,null);
                    rl.addView(child);
                }
                else if(position==1){
                    View child = getLayoutInflater().inflate(R.layout.weekly_event_sub, null);
                    CheckBox cb=(CheckBox)child.findViewById(R.id.checkBox);
                    cb.setChecked(days_of_week[0]);
                    cb=(CheckBox)child.findViewById(R.id.checkBox1);
                    cb.setChecked(days_of_week[1]);
                    cb=(CheckBox)child.findViewById(R.id.checkBox2);
                    cb.setChecked(days_of_week[2]);
                    cb=(CheckBox)child.findViewById(R.id.checkBox3);
                    cb.setChecked(days_of_week[3]);
                    cb=(CheckBox)child.findViewById(R.id.checkBox4);
                    cb.setChecked(days_of_week[4]);
                    cb=(CheckBox)child.findViewById(R.id.checkBox5);
                    cb.setChecked(days_of_week[5]);
                    cb=(CheckBox)child.findViewById(R.id.checkBox6);
                    cb.setChecked(days_of_week[6]);
                    rl.addView(child);
                }
                else if(position==2){
                    View child = getLayoutInflater().inflate(R.layout.monthly_event_sub, null);
                    final CalendarView cv=(CalendarView)child.findViewById(R.id.calendarView);
                    cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                            date=dayOfMonth;
                        }
                    });
                    Calendar c= Calendar.getInstance();
                    c.set(Calendar.YEAR,2017);
                    c.set(Calendar.MONTH,0);
                    c.set(Calendar.DATE,date);
                    c.set(Calendar.HOUR,0);
                    cv.setDate(c.getTimeInMillis(),true,true);
                    rl.addView(child);
                }


                TextView tv=(TextView)child.findViewById(R.id.textView5);
                if(freq>1){
                    tv.setText(type_dict[position]+"s");
                }
                else{
                    tv.setText(type_dict[position]);
                }
                type=position;
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        t.setSelection(type);
        Spinner f= (Spinner)child.findViewById(R.id.frequency_spinner);
        f.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                freq=position+1;
                TextView tv=(TextView)child.findViewById(R.id.textView5);
                if(freq>1){
                    tv.setText(type_dict[type]+"s");
                }
                else{
                    tv.setText(type_dict[type]);
                }

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        f.setSelection(freq);

        popupWindow.showAtLocation(findViewById(R.id.activity_event), Gravity.CENTER,0,0);


    }
    public void setQuantitativeFields(View view){


    }
}
