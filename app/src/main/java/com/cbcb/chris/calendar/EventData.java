package com.cbcb.chris.calendar;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by chris on 1/18/17.
 */

public class EventData {
    private int event_id;
    private int data_id;
    private Time start;
    private Time end;
    private ArrayList<Double> values;

    public EventData(){
        values=new ArrayList<Double>();
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public Time getEnd() {
        return end;
    }

    public void setEnd(Time end) {
        this.end = end;
    }

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
    }

    public int getData_id() {
        return data_id;
    }

    public void setData_id(int data_id) {
        this.data_id = data_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }
    public String toString(){
        String result="Event Id: "+ event_id +" Start Time: " + start.toString() + " End Time: " + end.toString() +" Values: ";
        int count=1;
        for(Double i: values){
            result +=count+". "+i+" ";
            count++;
        }
        return result;
    }
}
