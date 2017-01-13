package com.cbcb.chris.calendar;

/**
 * Created by chris on 1/8/17.
 */
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification n = intent.getParcelableExtra(NOTIFICATION);

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, n);
        int dow=intent.getIntExtra("DOW",0);
        int type=intent.getIntExtra("Type",0);
        String time=intent.getStringExtra("Time");
        int month_date=intent.getIntExtra("Date",1);
        boolean[] days_of_week=CalendarUtil.convertIntToBool(dow);


        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, n);
        notificationIntent.putExtra("Type",type);
        notificationIntent.putExtra("Time",time);
        notificationIntent.putExtra("DOW",dow);
        notificationIntent.putExtra("Date",month_date);


        DateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am=(AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Calendar calendar = createEventTime(date,type,days_of_week,month_date);
        am.set(AlarmManager.RTC,calendar.getTimeInMillis(),pendingIntent);
    }

    private Calendar createEventTime(Date e,int type,boolean[] days_of_week, int month_date){
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY,e.getHours());
        c.set(Calendar.MINUTE,e.getMinutes());
        c.set(Calendar.SECOND,0);
        Log.d("Type",""+type);
        if(type==0){
            if(c.getTimeInMillis()<=System.currentTimeMillis()){
                c.set(Calendar.DATE,c.get(Calendar.DATE)+1);
            }
        }
        else if(type==1){
            int day_of_week=c.get(Calendar.DAY_OF_WEEK);
            for(int i=day_of_week-1;i<14;i++){
                if(days_of_week[i%7]){
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
            c.set(Calendar.DATE,month_date);
            if(c.getTimeInMillis()<=System.currentTimeMillis()){
                c.set(Calendar.MONTH,c.get(Calendar.MONTH)+1);
            }
        }
        Log.d("Monthly Fire",c.getTime().toString());
        return c;
    }


}