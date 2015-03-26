package com.example.liz.virtualcit.Controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.liz.virtualcit.HomePage;
import com.example.liz.virtualcit.Model.LectureRoom;
import com.example.liz.virtualcit.Model.MenuObject;
import com.example.liz.virtualcit.Model.TableEntry;
import com.example.liz.virtualcit.MySQLLiteHelper;
import com.example.liz.virtualcit.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Controller {
    private static Controller instance;
    private ArrayList menuArray = new ArrayList();
    public NotificationCompat.Builder notification;
    private int keyValue;
    private String user;
    private String dep;
    private String course;
    private String semester;
    public SQLiteDatabase sqldb;
    private MySQLLiteHelper dbHelper;
    private String[] timeTableAllColumns = {MySQLLiteHelper.CLASSNAME,
            MySQLLiteHelper.ROOMNAME, MySQLLiteHelper.STARTTIME, MySQLLiteHelper.DAY};
    private String[] roomTableAllColumns = {MySQLLiteHelper.ROOMSNAME, MySQLLiteHelper.LONGITUDE, MySQLLiteHelper.LATITUDE};

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    private ArrayList loadOptions() {
        MenuObject mo = new MenuObject("MyCit", "http://www.mycit.ie");
        menuArray.add(mo);
        mo = new MenuObject("BlackBoard", "http://citbb.blackboard.com");
        menuArray.add(mo);
        mo = new MenuObject("Access Student Drive", "http://webvpn.cit.ie");
        menuArray.add(mo);
        mo = new MenuObject("CIT TimeTables", "http://timetables.cit.ie");
        menuArray.add(mo);
        mo = new MenuObject("Student Email", "https://mail.google.com/mail/u/1/#inbox");
        menuArray.add(mo);
        mo = new MenuObject("Students Union", "http://http://www.citsu.ie");
        menuArray.add(mo);
        mo = new MenuObject("Student Handbook", "https://docs.google.com/gview?url=http://www.mycit.ie/contentFiles/PDF/CIT-Sports.pdf");
        menuArray.add(mo);
        mo = new MenuObject("College Map", "http://www.mycit.ie/images/cit-map.jpg");
        menuArray.add(mo);
        mo = new MenuObject("Locate Room", "Locates the room");
        menuArray.add(mo);
        return menuArray;
    }

    public ArrayList getMenu() {
        menuArray = loadOptions();
        return menuArray;
    }

    public void databaseConnection(HomePage homePage) {
        dbHelper = new MySQLLiteHelper(homePage);
        sqldb = dbHelper.getWritableDatabase();
        sqldb.close();
    }

    /*public void localHostConnection(HomePage homePage) throws IOException {
        URL url = new URL("http://localhost:8080/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Toast toast = Toast.makeText(homePage, "Connection set", Toast.LENGTH_LONG);
        toast.show();

        System.out.println("Connection opened");
        urlConnection.disconnect();
    }*/

    public String getUser() {
        return user;
    }

    public void setUser(String userType) {
        user = userType;
    }

    public void setDepartment(String department) {
        dep = department;
    }

    public void setCourse(String userCourse) {
        course = userCourse;
    }

    public void setSemester(String semesterChoice) {
        semester = semesterChoice;
    }

    public ArrayList<TableEntry> getAllTimeTableEntrys() {
        ArrayList<TableEntry> tableEntries = new ArrayList<TableEntry>();
        sqldb = dbHelper.getReadableDatabase();
        Cursor cursor = sqldb.query(MySQLLiteHelper.TABLENAME,
                timeTableAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TableEntry te = cursorToTableEntry(cursor);
            tableEntries.add(te);
            cursor.moveToNext();
        }
        cursor.close();
        sqldb.close();
        return tableEntries;
    }

    private TableEntry cursorToTableEntry(Cursor cursor) {
        TableEntry te = new TableEntry();
        te.setModule(cursor.getString(0));
        te.setRoomName(cursor.getString(1));
        te.setStartTime(cursor.getString(2));
        te.setDay(cursor.getInt(3));
        return te;
    }

    public ArrayList<LectureRoom> getAllRooms() {
        ArrayList<LectureRoom> roomList = new ArrayList<LectureRoom>();
        sqldb = dbHelper.getReadableDatabase();
        Cursor cursor = sqldb.query(MySQLLiteHelper.ROOMTABLENAME,
                roomTableAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LectureRoom lr = cursorToRoomEntry(cursor);
            roomList.add(lr);
            cursor.moveToNext();
        }
        cursor.close();
        sqldb.close();
        return roomList;
    }

    public LectureRoom cursorToRoomEntry(Cursor cursor) {
        LectureRoom lr = new LectureRoom();
        lr.setRoomName(cursor.getString(0));
        lr.setGpsLatitude(cursor.getDouble(1));
        lr.setGpsLongitude(cursor.getDouble(2));
        return lr;
    }

    public void populateRoomTable(HomePage homePage) {
        sqldb = dbHelper.getWritableDatabase();
        InputStream inputStream = homePage.getResources().openRawResource(R.raw.roominfo);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);

            String currentLine;

            while ((currentLine = br.readLine()) != null) {
                String[] split = currentLine.split("#");
                String insertRooms = "INSERT INTO " + MySQLLiteHelper.ROOMTABLENAME
                        + "(" + MySQLLiteHelper.ROOMSNAME + ","
                        + MySQLLiteHelper.LATITUDE + ","
                        + MySQLLiteHelper.LONGITUDE
                        + ") VALUES("
                        + split[0] + ","
                        + split[1] + ","
                        + split[2] + ");";
                sqldb.execSQL(insertRooms);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqldb.close();
        Toast dbtoast = Toast.makeText(homePage, "Locate Room Setup Complete", Toast.LENGTH_LONG);
        dbtoast.show();

    }

    public void populateTimeTable(String module, String room, String time, int day) {

        sqldb = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLLiteHelper.TTPRIMARY_KEY, keyValue);
        values.put(MySQLLiteHelper.CLASSNAME, module);
        values.put(MySQLLiteHelper.ROOMNAME, room);
        values.put(MySQLLiteHelper.STARTTIME, time);
        values.put(MySQLLiteHelper.DAY, day);

        long rowID = sqldb.insert(MySQLLiteHelper.TABLENAME, "NULL", values);

        keyValue++;
    }
}