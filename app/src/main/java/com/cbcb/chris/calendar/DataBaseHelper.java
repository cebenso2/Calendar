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
    private static final String DATABASE_NAME = "eventsInfo_best";
    // Contacts table name
    private static final String TABLE_EVENTS = "events";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TIME = "time";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DATE = "date";
    private static final String KEY_DAYS_OF_WEEK = "week";
    private static final String KEY_FREQUENCY = "freq";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_TIME + " Time," + KEY_TYPE +" INTEGER,"+KEY_DATE+" INTEGER,"+KEY_DAYS_OF_WEEK+ " INTEGER,"+ KEY_FREQUENCY+" INTEGER"+")";
        Log.d("test","create");
        Log.d("test",CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
        Log.d("test","upgrade");
    }
    // Adding new shop
    public void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, event.getName()); // Shop Name
        values.put(KEY_TIME , event.getTime().toString()); // Shop Phone Number
        values.put(KEY_TYPE , event.getType());
        values.put(KEY_DATE,event.getDate());
        values.put(KEY_DAYS_OF_WEEK,convertBoolToInt(event.getDays_of_week()));
        Log.d("Freq event",String.valueOf(event.getFreq()));
        values.put(KEY_FREQUENCY,event.getFreq());
        db.insert(TABLE_EVENTS, null, values);
        db.close(); // Closing database connection
    }

    public Event getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_EVENTS, new String[] { KEY_ID,
                        KEY_NAME, KEY_TIME,KEY_TYPE,KEY_DATE,KEY_DAYS_OF_WEEK,KEY_FREQUENCY }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Event event = new Event(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Time.valueOf(cursor.getString(2)),Integer.valueOf(cursor.getString(3)),Integer.valueOf(cursor.getString(4)),convertIntToBool(Integer.valueOf(cursor.getString(5))),Integer.valueOf(cursor.getString(6)));

        return event;
    }
    private boolean[] convertIntToBool(int d){
        boolean[] b=new boolean[7];
        int count=0;
        while(d>0){
            b[count]= (d& 1)>0;
            d=d>>1;
            count+=1;
        }
        return b;
    }
    private int convertBoolToInt(boolean[] b){
        int result=0;
        for(int i =0; i<7;i++){
            if(b[i]){
                result+=Math.pow(2,i);
            }
        }
        return result;
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
                event.setDays_of_week(convertIntToBool(Integer.parseInt(cursor.getString(5))));
                event.setFreq(Integer.parseInt(cursor.getString(6)));
// Adding contact to list
                eventList.add(event);
            } while (cursor.moveToNext());
        }
// return contact list
        return eventList;
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
        db.close();
    }
}
