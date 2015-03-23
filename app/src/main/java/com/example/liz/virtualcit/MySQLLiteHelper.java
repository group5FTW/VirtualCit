package com.example.liz.virtualcit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLLiteHelper extends SQLiteOpenHelper {
    //time table columns
    public static final String TABLENAME = "TimeTable";
    public static final String CLASSNAME = "Module";
    public static final String ROOMNAME = "RoomName";
    public static final String STARTTIME = "StartTime";
    public static final String DAY = "Day";

    //room table comments
    public static final String ROOMTABLENAME = "RoomTable";
    public static final String ROOMSNAME = "Name";
    public static final String LONGITUDE = "Longitude";
    public static final String LATITUDE = "Latitude";


    private static final String DATABASE_NAME = "virtualCit.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String TIMETABLETABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TABLENAME + "(" + CLASSNAME
            + " varchar(45) primary key, "
            + ROOMNAME + " varchar(45), "
            + STARTTIME + " varchar(45), "
            + DAY + " integer not null);";

    private static final String ROOMTABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + ROOMTABLENAME + "(" + ROOMSNAME
            + " text, "
            + LONGITUDE + " integer not null, "
            + LATITUDE + " integer not null);";

    public MySQLLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        System.out.println(TIMETABLETABLE_CREATE);
        database.execSQL(TIMETABLETABLE_CREATE);
        System.out.println(ROOMTABLE_CREATE);
        database.execSQL(ROOMTABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TIMETABLETABLE_CREATE);
        onCreate(db);
    }
}