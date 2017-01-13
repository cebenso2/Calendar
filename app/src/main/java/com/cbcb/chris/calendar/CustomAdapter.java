package com.cbcb.chris.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chris on 1/12/17.
 */

public class CustomAdapter extends ArrayAdapter<Event>{
    private Context context;
    private ArrayList<Event> events;
    private String[] type_dict=new String[]{"Daily","Weekly","Monthly"};

    public CustomAdapter(Context context, int resource,ArrayList<Event> events) {
        super(context, resource,events);
        this.context=context;
        this.events=events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = events.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.simple_row, null);

        TextView name = (TextView) view.findViewById(R.id.row_name);
        TextView type = (TextView) view.findViewById(R.id.row_type);
        TextView time = (TextView) view.findViewById(R.id.row_time);

        //set price and rental attributes
        name.setText(event.getName());
        type.setText(type_dict[event.getType()]);
        time.setText(createClockText(event.getTime().getMinutes(),event.getTime().getHours()));

        return view;
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
        if(hourOfDay==12){
            meridiem="PM";
        }
        hour=String.valueOf(hourOfDay);
        if(hourOfDay==0){
            hour="12";
            meridiem="AM";
        }
        return hour+":"+min+" "+meridiem;

    }


}
