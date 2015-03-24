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
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Controller.getInstance().databaseConnection(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_prev_started), false);
        ListView listView = (ListView) findViewById(R.id.listView);

        if (!previouslyStarted) {
            Controller.getInstance().populateRoomTable(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_prev_started), Boolean.TRUE);
            edit.apply();
            showLogin();
        }

        if (count == 0) {
            showList(listView);
            count++;
        }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromLogin) {
        if (requestCode == CHILD_ACTIVITY_CODE && resultCode == RESULT_OK) {
            Controller.getInstance().setUser(intentFromLogin.getStringExtra("user"));
            Controller.getInstance().setDepartment(intentFromLogin.getStringExtra("department"));
            Controller.getInstance().setCourse(intentFromLogin.getStringExtra("course"));
            Controller.getInstance().setSemester(intentFromLogin.getStringExtra("semester"));

            TimeTableGeneration ttg = new TimeTableGeneration(this,
                    intentFromLogin.getStringExtra("course"),
                    intentFromLogin.getStringExtra("semester"));
            System.out.println("Semester: " + intentFromLogin.getStringExtra("semester"));
            notificationBuilder(this);

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

        if (temp.getName() == "CIT TimeTables") {

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

    public void notificationBuilder(final HomePage homePage) {

        Timer timer = new Timer();
        TimerTask timeCheck = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Timer Started");
                String[] numDaysOfWeek = {"1", "2", "3", "4", "5"};
                DateFormat df = new SimpleDateFormat("mm H EEE");
                System.out.println("Date format");
                String date = df.format(Calendar.getInstance().getTime());
                System.out.println(date);
                String[] currentMinute = date.split(" ");
                System.out.println("Date Split");
                ArrayList<TableEntry> timeTable = Controller.getInstance().getAllTimeTableEntrys();
                ArrayList<LectureRoom> roomList = Controller.getInstance().getAllRooms();
                String titleString = "";
                String launchNavString = "Touch to show in Map?";
                System.out.println("ArrayLists Made");
                TableEntry te = new TableEntry();

                for (int i = 0; i < 4; i++) {
                    if (currentMinute[2] == numDaysOfWeek[i]) {
                        System.out.println("first if");
                        int tempNum;
                        if (Integer.parseInt(currentMinute[0]) < 50) {
                            tempNum = Integer.parseInt(currentMinute[0] + 10);
                            System.out.println("second if");
                        } else tempNum = Integer.parseInt(currentMinute[0]);

                        if ((tempNum > 50) && (tempNum < 59)) {
                            System.out.println("third if");
                            for (int j = 0; j < timeTable.size(); j++) {
                                int nextClassCheck = (Integer.parseInt(timeTable.get(j).getStartTime()) - 1);
                                System.out.println("second for");
                                if (Integer.parseInt(currentMinute[1]) == nextClassCheck) {
                                    te = timeTable.get(j);
                                    titleString = "You're next class is in room " + te.getRoomName();

                                } else if ((Integer.parseInt(currentMinute[1]) == 12) && (nextClassCheck == 1)) {
                                    te = timeTable.get(j);
                                    titleString = "You're next class is in room " + te.getRoomName();
                                }
                            }

                            int position = 0;
                            for (int x = 0; x < roomList.size(); x++) {
                                if (roomList.get(x).getRoomName() == te.getRoomName()) {
                                    position = x;
                                }
                            }

                            NotificationCompat.Builder notification = new NotificationCompat.Builder(homePage)
                                    .setSmallIcon(R.drawable.notification_icon)
                                    .setContentTitle(titleString)
                                    .setContentText(launchNavString);

                            LocationManager lm = (LocationManager) homePage.getSystemService(LOCATION_SERVICE);
                            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            String currentLongitude = String.valueOf(loc.getLongitude());
                            String currentLatitude = String.valueOf(loc.getLatitude());
                            String url = "http://www.google.ie/maps/dir/";
                            url += currentLatitude + "," + currentLongitude + "/";
                            url += roomList.get(position).getGpsLatitude() + "+" + roomList.get(position).getGpsLongitude() + "/";
                            Uri uri = Uri.parse(url);//makes URL

                            Intent maps = new Intent(Intent.ACTION_VIEW, uri);
                            PendingIntent pi = PendingIntent.getActivities(homePage, 0, new Intent[]{maps}, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pi);

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(1, notification.build());
                        }
                    }
                }
            }
        };
        timer.schedule(timeCheck, 01, 1000 * 60 * 60);
    }
}