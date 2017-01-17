package com.cbcb.chris.calendar;


import java.nio.channels.NonReadableChannelException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by chris on 1/7/17.
 */

public class Event {
    private Time time;
    private int id;
    private String name;
    private ArrayList<String> quant_names;
    private ArrayList<String> quant_units;
    private int date;
    private boolean[] days_of_week;
    private int type;
    private int freq;

    public Event()
    {

    }
    public Event(int id,String name,Time time, int type, int date, boolean[] days_of_week,int freq,ArrayList<String> quant_names,ArrayList<String> quant_units)
    {
        this.id=id;
        this.name=name;
        this.time=time;
        this.quant_names=quant_names;
        this.quant_units=quant_units;
        this.days_of_week=days_of_week;
        this.date=date;
        this.type=type;
        this.freq=freq;
    }
    public Event(int id){
        this.id=id;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean[] getDays_of_week() {
        return days_of_week;
    }

    public void setDays_of_week(boolean[] days_of_week) {
        this.days_of_week = days_of_week;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public ArrayList<String> getQuant_names(){return quant_names;}
    public ArrayList<String> getQuant_units(){return quant_units;}

    public void setQuant_names(ArrayList<String> quant_names){this.quant_names=quant_names;}
    public void setQuant_units(ArrayList<String> quant_units){this.quant_units=quant_units;}

    public String toString(){
        String s="Event - Id: "+this.id+" Name: "+this.name+" Time: "+this.time+" Type: "+this.type+" Frequency: "+this.freq+" Date: "+this.date+" DaysOfWeek: ";
        for(int i=0;i<7;i++){
            s+=" "+String.valueOf(this.days_of_week[i]);
        }
        s+="\n Quant Fields: ";
        for(int i=0;i<quant_names.size();i++){
            s+=" "+(i+1)+". Name: "+quant_names.get(i)+" Units: "+quant_units.get(i);
        }
        return s;
    }
}
