package com.cbcb.chris.calendar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chris on 1/12/17.
 */

public class EventDataAdapter extends ArrayAdapter<EventData> {
    private Context context;
    private ArrayList<EventData> eds;
    private Event e;

    public EventDataAdapter(Context context, int resource, ArrayList<EventData> eds, Event e) {
        super(context, resource, eds);
        this.eds=eds;
        this.context=context;
        this.e=e;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventData ed = eds.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_data_row, null);

        TextView date = (TextView) view.findViewById(R.id.date_title);
        TextView se_time = (TextView) view.findViewById(R.id.start_end_time);
        LinearLayout insert = (LinearLayout) view.findViewById(R.id.insert);

        date.setText("Jan 21, 2017");

        String start_time_text=CalendarUtil.createClockText(ed.getStart().getMinutes(),ed.getStart().getHours());
        String end_time_text=CalendarUtil.createClockText(ed.getEnd().getMinutes(),ed.getEnd().getHours());
        se_time.setText(start_time_text + " - "+end_time_text);

        LinearLayout ll=(LinearLayout)view.findViewById(R.id.insert);

        for(int i=0;i<e.getQuant_names().size();i++){
            TextView tv=new TextView(getContext());
            tv.setText(""+(i+1)+". "+e.getQuant_names().get(i) + ": "+ed.getValues().get(i)+" "+e.getQuant_units().get(i));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(lp);
            ll.addView(tv);

        }

        return view;
    }

}
