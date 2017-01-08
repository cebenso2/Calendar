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
    private ArrayList<String> tasks;

    public Event()
    {

    }
    public Event(int id,String name,Time time)
    {
        this.id=id;
        this.name=name;
        this.time=time;
        this.tasks=new ArrayList<String>();
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

    public ArrayList<String> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<String> tasks) {
        this.tasks = tasks;
    }
}
