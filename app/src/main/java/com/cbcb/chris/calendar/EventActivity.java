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
    private static final String KEY_TEXT_REPLY = "key_text_reply";


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
        TimePickerDialog l=new TimePickerDialog(EventActivity.this,R.style.Theme_Dialog , t, 12, 0, false);
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
        db.addEvent(new Event(1,event_name,event_time));
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
}
