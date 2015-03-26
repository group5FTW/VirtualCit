package com.example.liz.virtualcit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liz.virtualcit.Controller.Controller;
import com.example.liz.virtualcit.Model.LectureRoom;
import com.example.liz.virtualcit.Model.MenuObject;
import com.example.liz.virtualcit.Model.TableEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class HomePage extends ActionBarActivity {
    private static final int CHILD_ACTIVITY_CODE = 1234;
    private ArrayList<MenuObject> options = new ArrayList<>();
    private String user;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Controller.getInstance().databaseConnection(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_prev_started), false);

        listView = (ListView) findViewById(R.id.listView);

        if (!previouslyStarted) {
            Controller.getInstance().populateRoomTable(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_prev_started), Boolean.TRUE);
            edit.apply();
            showLogin();
        } else {
            showList(listView);
        }

        Timer timer = new Timer();
        TimerTask timeCheck = new TimerTask() {
            @Override
            public void run() {
                notificationBuilder();
                System.out.println("Notifications started");
            }
        };
        timer.schedule(timeCheck, 1, 600000);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i;

        if (id == R.id.image1) {
            i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://twitter.com/CIT_ie?lang=en"));
            startActivity(i);
        } else if (id == R.id.image2) {
            i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/myCIT"));
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showLogin() {
        Intent i = new Intent(this, Login.class);
        startActivityForResult(i, CHILD_ACTIVITY_CODE);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        user = Controller.getInstance().getUser();
        saveInstanceState.putString("User", user);
        super.onSaveInstanceState(saveInstanceState);
    }

    public void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        user = saveInstanceState.getString("User");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromLogin) {
        if (requestCode == CHILD_ACTIVITY_CODE && resultCode == RESULT_OK) {
            user = intentFromLogin.getStringExtra("user");
            Controller.getInstance().setUser(intentFromLogin.getStringExtra("user"));

            Controller.getInstance().setDepartment(intentFromLogin.getStringExtra("department"));
            Controller.getInstance().setCourse(intentFromLogin.getStringExtra("course"));
            Controller.getInstance().setSemester(intentFromLogin.getStringExtra("semester"));

            TimeTableGeneration ttg = new TimeTableGeneration(this,
                    intentFromLogin.getStringExtra("course"),
                    intentFromLogin.getStringExtra("semester"));
            System.out.println("Semester: " + intentFromLogin.getStringExtra("semester"));
            showList(listView);
        }
    }

    public void showList(ListView listView) {
        options = Controller.getInstance().getMenu();

        try {
            ArrayAdapter<MenuObject> menuAdapter;
            menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
            listView.setAdapter(menuAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String menuOption = (String) ((TextView) view).getText();
                optionChoice(menuOption);
            }
        });
    }

    public void optionChoice(String menuOption) {
        MenuObject temp = new MenuObject();
        for (int i = 0; i < options.size(); i++) {

            if (options.get(i).toString().equals(menuOption)) {
                temp = options.get(i);
            }
        }

        if ((temp.getName() == "CIT TimeTables") && (user.compareToIgnoreCase("Student") == 0)) {

            Intent i = new Intent(this, TimeTableActivity.class);
            startActivity(i);
        } else if (temp.getName() == "Locate Room") {
            Intent lrIntent = new Intent(this, LocateRoomActivity.class);
            startActivity(lrIntent);
        } else {
            Intent intent = new Intent(this, LaunchWebsite.class);
            intent.putExtra("name", temp.getName());
            intent.putExtra("url", temp.getUrl());
            startActivity(intent);
        }
    }

    public void notificationBuilder() {
        int numDayOfWeek = 0;

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String currentLongitude = String.valueOf(loc.getLongitude());
        String currentLatitude = String.valueOf(loc.getLatitude());
        String launchNavString = "Touch to show in Map?";

        DateFormat df = new SimpleDateFormat("mm H EEEE");
        String date = df.format(Calendar.getInstance().getTime());
        String[] currentMinute = date.split(" ");

        ArrayList<TableEntry> timeTable = Controller.getInstance().getAllTimeTableEntrys();
        ArrayList<LectureRoom> roomList = Controller.getInstance().getAllRooms();
        String titleString = "";
        boolean classFound = false;

        TableEntry te = new TableEntry();

        if (currentMinute[2].compareToIgnoreCase("Monday") == 0) numDayOfWeek = 1;
        else if (currentMinute[2].compareToIgnoreCase("Tuesday") == 0) numDayOfWeek = 2;
        else if (currentMinute[2].compareToIgnoreCase("Wednesday") == 0) numDayOfWeek = 3;
        else if (currentMinute[2].compareToIgnoreCase("Thursday") == 0) numDayOfWeek = 4;
        else if (currentMinute[2].compareToIgnoreCase("Friday") == 0) numDayOfWeek = 5;
        else {
            numDayOfWeek = 0;
        }

        int tempNum;
        System.out.println("Today date is " + currentMinute[2] + " num = " + numDayOfWeek);

        if (Integer.parseInt(currentMinute[0]) < 50) {
            tempNum = Integer.parseInt(currentMinute[0] + 10);
            System.out.println("Current minute " + currentMinute[0]);
        } else {
            tempNum = Integer.parseInt(currentMinute[0]);
        }

        if ((tempNum > 50) && (tempNum < 59)) {
            System.out.println("between 50-59");
            for (int j = 0; j < timeTable.size(); j++) {
                int nextClassCheck = (Integer.parseInt(timeTable.get(j).getStartTime()) - 1);//get the time of the next class
                System.out.println("second for");
                //checks if there are classes that match the time and are on today
                if ((Integer.parseInt(currentMinute[1]) == nextClassCheck)
                        && (numDayOfWeek == timeTable.get(j).getDay())) {
                    te = timeTable.get(j);
                    titleString = "You're next class is in room " + te.getRoomName();
                    System.out.println("Class Found");
                }
                //if the class is at 1 and the current time is 12
                else if ((Integer.parseInt(currentMinute[1]) == 12) && (nextClassCheck == 1)
                        && (numDayOfWeek == timeTable.get(j).getDay())) {
                    te = timeTable.get(j);
                    titleString = "You're next class is in room " + te.getRoomName();
                    System.out.println("Class between 12 and 1 Found");
                    classFound = true;

                }
            }
        }

        if (classFound == true) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(titleString)
                    .setContentText(launchNavString);

            int position = 0;
            for (int x = 0; x < roomList.size(); x++) {
                if (roomList.get(x).getRoomName() == te.getRoomName()) {
                    position = x;
                }
            }

            String url = "http://www.google.ie/maps/dir/";
            url += currentLatitude + "," + currentLongitude + "/";
            url += roomList.get(position).getGpsLatitude() + "+" + roomList.get(position).getGpsLongitude() + "/";
            Uri uri = Uri.parse(url);//makes URL

            Intent maps = new Intent(Intent.ACTION_VIEW, uri);
            PendingIntent pi = PendingIntent.getActivities(this, 0, new Intent[]{maps}, PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setContentIntent(pi);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notification.build());
        } else {
            System.out.println("No Time available");
            classFound = false;
        }


    }
}