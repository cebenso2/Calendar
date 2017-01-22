package com.cbcb.chris.calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import static com.cbcb.chris.calendar.R.layout.quantitative;
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
    private ArrayList<String> quant_names;
    private ArrayList<String> quant_units;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        type=1;
        freq=0;
        hours=12;
        minutes=0;
        date=1;
        days_of_week= new boolean[]{false,false,false,false,false,false,false};
        quant_names=new ArrayList<String>();
        quant_units=new ArrayList<String>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public void setTime(View view){
        TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                minutes=minute;
                hours=hourOfDay;
                TextView tc=(TextView)findViewById(R.id.textClock);
                tc.setText(CalendarUtil.createClockText(minute,hourOfDay));
            }
        };
        TimePickerDialog l=new TimePickerDialog(EventActivity.this,R.style.Theme_Dialog , t, hours, minutes, false);
        l.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void addEventToDB(View view){
        EditText name=(EditText)findViewById(R.id.EventName);
        if(name.getText().toString().length()==0){
            Snackbar.make(view, "Enter a name for the Event", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        boolean day_set=false;
        for(int i=0;i<7;i++){
            day_set|=days_of_week[i];
        }
        if(type==1 && !day_set){
            Snackbar.make(view, "Enter at least one day for weekly events", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }
        String event_name=name.getText().toString();
        Time event_time=new Time(hours,minutes,0);
        DataBaseHelper db = new DataBaseHelper(this);
        Event e=new Event(1,event_name,event_time,type,date,days_of_week,freq+1,quant_names,quant_units);
        e.setId((int)db.addEvent(e));
        db.close();
        setEventAlarm(e);
        this.finish();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setEventAlarm(Event e){
        Time time=e.getTime();
        Notification n=getNotification(e);

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, e.getId());
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, n);
        notificationIntent.putExtra("Type",e.getType());
        notificationIntent.putExtra("Time",time.toString());
        notificationIntent.putExtra("DOW",CalendarUtil.convertBoolToInt(e.getDays_of_week()));
        notificationIntent.putExtra("Date",e.getDate());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, e.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d("Create Event Id", ""+e.getId());
        Calendar calendar=createEventTime(e);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(),pendingIntent);

    }
    private Calendar createEventTime(Event e){
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,e.getTime().getHours());
        c.set(Calendar.MINUTE,e.getTime().getMinutes());
        c.set(Calendar.SECOND,0);

        if(e.getType()==0){
            if(c.getTimeInMillis()<=System.currentTimeMillis()){
                c.set(Calendar.DATE,c.get(Calendar.DATE)+1);
            }
        }
        else if(e.getType()==1){
            int day_of_week=c.get(Calendar.DAY_OF_WEEK);
            for(int i=day_of_week-1;i<14;i++){
                if(e.getDays_of_week()[i%7]){
                    c.set(Calendar.DAY_OF_WEEK,(i+1)%7);
                    if(i>=7){
                        c.set(Calendar.DATE,c.get(Calendar.DATE)+7);
                    }
                }
                if(c.getTimeInMillis()>System.currentTimeMillis()){
                    break;
                }
            }
        }
        else{
            c.set(Calendar.DATE,e.getDate());
            if(c.getTimeInMillis()<=System.currentTimeMillis()){
                c.set(Calendar.MONTH,c.get(Calendar.MONTH)+1);
            }
        }
        Log.d("Monthyl",c.getTime().toString());
        return c;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Notification getNotification(Event e) {

        String replyLabel = "test";
        Intent resultIntent = new Intent(this, CompletedEvent.class);
        resultIntent.putExtra("ID",e.getId());


        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        e.getId(),
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();
        Notification.Action action =
                new Notification.Action.Builder(android.R.drawable.ic_menu_save,
                        "Completed",resultPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();
        Notification.Builder builder = new Notification.Builder(this);

        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.addAction(action);
        builder.setContentTitle("Calendar: "+e.getName());
        builder.setContentText(e.getTime().toString());
        builder.setOngoing(true);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        return builder.build();
    }
    public void setRepetition(View view){
        final View child = getLayoutInflater().inflate(repetition,null);
        final PopupWindow popupWindow=new PopupWindow(child);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.update();
        Button b=(Button)child.findViewById(R.id.submit_repetition);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv=(TextView)findViewById(R.id.event_type);
                Spinner s=(Spinner)child.findViewById(R.id.event_type_spinner);
                type=s.getSelectedItemPosition();
                tv.setText(s.getSelectedItem().toString());
                s = (Spinner) child.findViewById(R.id.frequency_spinner);
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
                if(freq>0){
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
                freq=position;
                TextView tv=(TextView)child.findViewById(R.id.textView5);
                if(freq>0){
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
    public void addQuantitative(View view){
        final View child = getLayoutInflater().inflate(quantitative,null);
        final PopupWindow popupWindow=new PopupWindow(child);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        Button b=(Button)child.findViewById(R.id.submit_quant);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_name=(EditText)child.findViewById(R.id.quant_name_edit);
                EditText et_units=(EditText)child.findViewById(R.id.quant_unit_edit);
                if(et_name.getText().toString().length()==0 || et_units.getText().toString().length()==0){
                    popupWindow.dismiss();
                    return;
                }
                quant_names.add(et_name.getText().toString());
                quant_units.add(et_units.getText().toString());
                displayQauntFields();
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(findViewById(R.id.timeLayout), Gravity.CENTER,0,0);

    }
    public void deleteQuantitative(View view){
        int id=view.getId();
        quant_names.remove(id);
        quant_units.remove(id);
        displayQauntFields();

    }
    private void displayQauntFields(){
        LinearLayout insert_quant= (LinearLayout)findViewById(R.id.insert_quant);
        insert_quant.removeAllViews();
        for(int i=0;i<quant_names.size();i++){
            View qf = getLayoutInflater().inflate(R.layout.quant_field_sub,null);
            FloatingActionButton fab=(FloatingActionButton)qf.findViewById(R.id.remove_quant);
            fab.setId(i);
            TextView q=(TextView)qf.findViewById(R.id.quant_text_all);
            q.setText(""+(i+1)+". "+quant_names.get(i)+ " - "+quant_units.get(i));
            q.setId(i);
            insert_quant.addView(qf);
        }

    }
    public void close(View view){
        this.finish();
    }
}
