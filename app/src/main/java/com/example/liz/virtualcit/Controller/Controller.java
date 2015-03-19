package com.example.liz.virtualcit.Controller;

import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.liz.virtualcit.HomePage;
import com.example.liz.virtualcit.Model.MenuObject;
import com.example.liz.virtualcit.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Controller {
    private static Controller instance;
    private ArrayList<MenuObject> menuArray = new ArrayList();
    public NotificationCompat.Builder notification;
    private String user;
    private String dep;
    private String course;

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
        mo = new MenuObject("Student Handbook", "citssguide.pdf");
        menuArray.add(mo);
        mo = new MenuObject("College Map", "http://www.mycit.ie/images/cit-map.jpg");
        menuArray.add(mo);
        return menuArray;
    }


    public ArrayList getMenu() {
        menuArray = loadOptions();
        return menuArray;
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
        //NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

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
}
