package com.cbcb.chris.calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 1/7/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper{
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "eventsInfo_v7";

    private static final String TABLE_EVENTS = "events";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TIME = "time";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATE = "date";
    private static final String KEY_DAYS_OF_WEEK = "week";
    private static final String KEY_FREQUENCY = "freq";

    private static final String TABLE_EVENTS_DATA = "events_data";

    private static final String KEY_START_TIME = "startTime";
    private static final String KEY_END_TIME = "endTime";
    private static final String KEY_Q1="q1";
    private static final String[] columns={KEY_ID,KEY_START_TIME,KEY_END_TIME,KEY_Q1};

    private static final String TABLE_QUANT_FIELDS = "quant_fields";

    private static final String KEY_EVENT_ID = "event_id";
    private static final String KEY_UNITS="units";

    private static final String TABLE_QUANT_FIELDS_DATA = "quant_fields_data";

    private static final String KEY_DATA_ID = "data_id";
    private static final String KEY_FIELD_NUMBER="field_number";
    private static final String KEY_VALUE="value";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " Time," + KEY_TYPE +" INTEGER,"+KEY_DATE+" INTEGER,"+KEY_DAYS_OF_WEEK+ " INTEGER,"+ KEY_FREQUENCY+" INTEGER"+")";
        String CREATE_EVENT_DATA_TABLE = "CREATE TABLE " + TABLE_EVENTS_DATA + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_ID +" INTEGER," + KEY_START_TIME + " TIME," + KEY_END_TIME + " Time" +")";
        String CREATE_QUANT_FIELDS_TABLE = "CREATE TABLE " + TABLE_QUANT_FIELDS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EVENT_ID + " INTEGER," + KEY_NAME + " TEXT," +KEY_UNITS + " TEXT)";
        String CREATE_QUANT_FIELDS_DATA_TABLE = "CREATE TABLE " + TABLE_QUANT_FIELDS_DATA + "(" + KEY_DATA_ID + " INTEGER," + KEY_EVENT_ID+ " INTEGER, "+ KEY_FIELD_NUMBER + " INTEGER," + KEY_VALUE + " REAL)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_EVENT_DATA_TABLE);
        db.execSQL(CREATE_QUANT_FIELDS_TABLE);
        db.execSQL(CREATE_QUANT_FIELDS_DATA_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANT_FIELDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUANT_FIELDS_DATA);
        onCreate(db);
        Log.d("test","upgrade");
    }

    public void addEventData(int id, Time start, Time end, List<Double> q_values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EVENT_ID, id);
        values.put(KEY_START_TIME , start.toString());
        values.put(KEY_END_TIME , end.toString());
        long data_id=db.insert(TABLE_EVENTS_DATA, null, values);
        for(int i=0;i<q_values.size();i++){
            values=new ContentValues();
            values.put(KEY_DATA_ID,data_id);
            values.put(KEY_FIELD_NUMBER,i);
            values.put(KEY_VALUE,q_values.get(i));
            values.put(KEY_EVENT_ID,id);
            db.insert(TABLE_QUANT_FIELDS_DATA,null,values);
        }
        db.close();
    }
    // Adding new shop
    public long addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName()); // Shop Name
        values.put(KEY_TIME , event.getTime().toString()); // Shop Phone Number
        values.put(KEY_TYPE , event.getType());
        values.put(KEY_DATE,event.getDate());
        values.put(KEY_DAYS_OF_WEEK,CalendarUtil.convertBoolToInt(event.getDays_of_week()));
        values.put(KEY_FREQUENCY,event.getFreq());
        long id=db.insert(TABLE_EVENTS, null, values);
        for(int i=0;i<event.getQuant_names().size();i++){
            values=new ContentValues();
            values.put(KEY_EVENT_ID,id);
            values.put(KEY_NAME,event.getQuant_names().get(i));
            values.put(KEY_UNITS,event.getQuant_units().get(i));
            db.insert(TABLE_QUANT_FIELDS,null,values);
        }
        db.close(); // Closing database connection
        return id;
    }

    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_TIME,KEY_TYPE,KEY_DATE,KEY_DAYS_OF_WEEK,KEY_FREQUENCY }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Event event = new Event();
        event.setId(Integer.parseInt(cursor.getString(0)));
        event.setName(cursor.getString(1));
        event.setTime(Time.valueOf(cursor.getString(2)));
        event.setType(Integer.parseInt(cursor.getString(3)));
        event.setDate(Integer.parseInt(cursor.getString(4)));
        event.setDays_of_week(CalendarUtil.convertIntToBool(Integer.parseInt(cursor.getString(5))));
        event.setFreq(Integer.parseInt(cursor.getString(6)));
        String selectQuantFieldsQuery="Select * FROM " + TABLE_QUANT_FIELDS +" WHERE " +KEY_EVENT_ID+ " = " + String.valueOf(id);
        cursor=db.rawQuery(selectQuantFieldsQuery,null);
        ArrayList<String> names=new ArrayList<String>();
        ArrayList<String> units=new ArrayList<String>();

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(2);
                String unit = cursor.getString(3);
                names.add(name);
                units.add(unit);

            } while (cursor.moveToNext());
        }
        event.setQuant_names(names);
        event.setQuant_units(units);
        return event;
    }

    public List<Event> getAllEvents() {
        List<Event> eventList = new ArrayList<Event>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_EVENTS + " ORDER BY "+KEY_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Event event = new Event();
                event.setId(Integer.parseInt(cursor.getString(0)));
                event.setName(cursor.getString(1));
                event.setTime(Time.valueOf(cursor.getString(2)));
                event.setType(Integer.parseInt(cursor.getString(3)));
                event.setDate(Integer.parseInt(cursor.getString(4)));
                event.setDays_of_week(CalendarUtil.convertIntToBool(Integer.parseInt(cursor.getString(5))));
                event.setFreq(Integer.parseInt(cursor.getString(6)));
                String selectQuantFieldsQuery="Select * FROM " + TABLE_QUANT_FIELDS +" WHERE " +KEY_EVENT_ID+ " = " + String.valueOf(event.getId());
                Cursor cursor1=db.rawQuery(selectQuantFieldsQuery,null);
                ArrayList<String> names=new ArrayList<String>();
                ArrayList<String> units=new ArrayList<String>();

                if (cursor1.moveToFirst()) {
                    do {
                        String name = cursor1.getString(2);
                        String unit = cursor1.getString(3);
                        names.add(name);
                        units.add(unit);

                    } while (cursor1.moveToNext());
                }
                event.setQuant_names(names);
                event.setQuant_units(units);
// Adding contact to list
                eventList.add(event);
            } while (cursor.moveToNext());
        }

// return contact list
        return eventList;
    }
    public void showAllEvents() {

        List<Event> events= getAllEvents();
        for( Event e: events){
            Log.d("Event",e.toString());
        }

    }
    // Getting shops Count
    public int getEventsCount() {
        String countQuery = "SELECT * FROM " + TABLE_EVENTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
// return count
        return cursor.getCount();
    }
    // Updating a shop
    public int updateEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName());
        values.put(KEY_TIME, event.getTime().toString());
// updating row
        return db.update(TABLE_EVENTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(event.getId())});
    }
    // Deleting a shop
    public void deleteEvent(Event event){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });
        db.delete(TABLE_EVENTS_DATA, KEY_ID + " = ?",
                new String[] { String.valueOf(event.getId()) });
        db.delete(TABLE_QUANT_FIELDS, KEY_EVENT_ID + " = ?",new String[] { String.valueOf(event.getId()) });
        db.delete(TABLE_QUANT_FIELDS_DATA, KEY_EVENT_ID + " = ?",new String[] { String.valueOf(event.getId()) });
        db.close();
    }

    public void showEventCompletionData(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM " + TABLE_EVENTS_DATA + " WHERE "+ KEY_EVENT_ID+ " = "+id,null);
        if (cursor.moveToFirst()) {
            do {
                EventData ed=new EventData();
                ed.setEvent_id(id);
                ed.setData_id(Integer.valueOf(cursor.getString(0)));
                Log.d("Start",cursor.getString(2));
                Log.d("End",cursor.getString(3));
                ed.setStart(Time.valueOf(cursor.getString(2)));
                ed.setEnd(Time.valueOf(cursor.getString(3)));
                Cursor cursor1=db.rawQuery("SELECT * FROM " +TABLE_QUANT_FIELDS_DATA + " WHERE " + KEY_DATA_ID + " = " +ed.getData_id() + " ORDER BY " +KEY_FIELD_NUMBER,null);
                ArrayList<Double> vs=new ArrayList<Double>();
                if (cursor1.moveToFirst()) {
                    do {
                        vs.add(Double.valueOf(cursor1.getString(3)));
                    }
                    while (cursor1.moveToNext());
                    ed.setValues(vs);
                }
                Log.d("Data: ",ed.toString());
            }
            while (cursor.moveToNext());
        }

    }

}
