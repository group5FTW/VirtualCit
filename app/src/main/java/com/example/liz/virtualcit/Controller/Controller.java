package com.example.liz.virtualcit.Controller;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Controller {
    private static Controller instance;
    private ArrayList menuArray = new ArrayList();
    public NotificationCompat.Builder notification;
    private String user;
    private String dep;
    private String course;
    private String semester;
    public SQLiteDatabase sqldb;
    private MySQLLiteHelper dbHelper;
    private String[] timeTableAllColumns = {dbHelper.CLASSNAME,
            dbHelper.ROOMNAME, dbHelper.STARTTIME, dbHelper.DAY};
    private String[] roomTableAllColumns = {dbHelper.ROOMSNAME, dbHelper.LONGITUDE, dbHelper.LATITUDE};

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
        mo = new MenuObject("Go to F Block", "51.8836091+-8.5356899");
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

    public void localHostConnection(HomePage homePage) throws IOException {
        URL url = new URL("http://localhost:8080/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Toast toast = Toast.makeText(homePage, "Connection set", Toast.LENGTH_LONG);
        toast.show();

        System.out.println("Connection opened");
        urlConnection.disconnect();
    }

    public void notificationBuilder(HomePage homePage) {
        DateFormat df = new SimpleDateFormat("H mm");
        String date = df.format(Calendar.getInstance().getTime());
        String[] currentMinute = date.split(" ");

        int[] days = {1, 2, 3, 4, 5};



        //NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        //Handler mHandler = new Handler(Looper.getMainLooper());

        /*Runnable mStatusChecker;
        int UPDATE_INTERVAL = 2000;

        mStatusChecker = new Runnable()
        {
            @Override
            public void run()
            {

                //Run the passed runnable

                // Re-run it after the update interval
                mHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };*/

        String titleString = "The current minute is" + currentMinute[1];
        String userInfo = "User: " + user + "Dep:" + dep + "Course:" + course;
        //Toast toast = Toast.makeText(HomePage.this, titleString, Toast.LENGTH_LONG);
        //toast.show();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(homePage)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(titleString)
                .setContentText(userInfo);
        /*Intent resultIntent = new Intent(this, TimeTableActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomePage.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);*/
        //NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //mNotificationManager.notify(1, mBuilder.build());
    }

    public String getUser() {
        return user;
    }

    public void setUser(String userType) {
        user = userType;
    }

    public String getDepartment() {
        return dep;
    }

    public void setDepartment(String department) {
        dep = department;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String userCourse) {
        course = userCourse;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semesterChoice) {
        semester = semesterChoice;
    }

    public ArrayList<TableEntry> getAllTimeTableEntrys() {
        ArrayList<TableEntry> tableEntries = new ArrayList<TableEntry>();
        sqldb = dbHelper.getReadableDatabase();
        Cursor cursor = sqldb.query(dbHelper.TABLENAME,
                timeTableAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TableEntry te = cursorToTableEntry(cursor);
            tableEntries.add(te);
            cursor.moveToNext();
        }
        cursor.close();
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
        Cursor cursor = sqldb.query(dbHelper.ROOMTABLENAME,
                roomTableAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LectureRoom lr = cursorToRoomEntry(cursor);
            roomList.add(lr);
            cursor.moveToNext();
        }
        cursor.close();
        return roomList;
    }

    public LectureRoom cursorToRoomEntry(Cursor cursor) {
        LectureRoom lr = new LectureRoom();
        lr.setRoomName(cursor.getString(0));
        lr.setGpsLongitude(cursor.getInt(1));
        lr.setGpsLatitude(cursor.getInt(2));
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
                String insert = "INSERT INTO " +
                        dbHelper.ROOMTABLENAME + "(" +
                        dbHelper.ROOMSNAME + "," +
                        dbHelper.LONGITUDE + "," +
                        dbHelper.LATITUDE + ")" + " VALUES("
                        + split[0] + "," + split[1] + "," + split[2] + ")";

                sqldb.execSQL(insert);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast dbtoast = Toast.makeText(homePage, "Room Database Populated", Toast.LENGTH_LONG);
        dbtoast.show();
        sqldb.close();
    }

    public void populateTimeTable(String module, String room, String time, String day) {
        sqldb = dbHelper.getWritableDatabase();
        String insertStatement = "INSERT INTO " +
                dbHelper.TABLENAME + "(" +
                dbHelper.CLASSNAME + "," +
                dbHelper.ROOMNAME + "," +
                dbHelper.STARTTIME + "," +
                dbHelper.DAY + ") VALUES("
                + "'" + module + "',"
                + "'" + room + "',"
                + "'" + time + "',"
                + "'" + day + "');";

        System.out.println(insertStatement);

        sqldb.execSQL(insertStatement);
    }
}