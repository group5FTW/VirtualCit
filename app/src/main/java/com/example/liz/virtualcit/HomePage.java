package com.example.liz.virtualcit;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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
import android.widget.Toast;

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
    private static String user;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        listView = (ListView) findViewById(R.id.listView);

        Controller.getInstance().databaseConnection(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean("start", false);

        if (!previouslyStarted) {
            Controller.getInstance().populateRoomTable(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("start", Boolean.TRUE);
            edit.apply();
            showLogin();
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
    public void onResume() {
        super.onResume();
        listView.invalidate();
        showList(listView);
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
        Intent i = new Intent(this, LaunchWebsite.class);

        if (id == R.id.image1) {
            i.putExtra("name", "Twitter");
            i.putExtra("url", "http://twitter.com/CIT_ie?lang=en");
            startActivity(i);
        } else if (id == R.id.image2) {
            i.putExtra("name", "Facebook");
            i.putExtra("url", "http://www.facebook.com/myCIT");
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        user = Controller.getInstance().getUser();
        saveInstanceState.putString("User", user);
        super.onSaveInstanceState(saveInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        user = saveInstanceState.getString("User");
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intentFromLogin) {

        boolean network = isNetworkAvailable();
        if (requestCode == CHILD_ACTIVITY_CODE && resultCode == RESULT_OK) {
            user = intentFromLogin.getStringExtra("user");
            if (user.compareToIgnoreCase("Student") == 0) {
                Controller.getInstance().setUser(intentFromLogin.getStringExtra("user"));
                Controller.getInstance().setDepartment(intentFromLogin.getStringExtra("department"));
                Controller.getInstance().setCourse(intentFromLogin.getStringExtra("course"));
                Controller.getInstance().setSemester(intentFromLogin.getStringExtra("semester"));

                TimeTableGeneration ttg = new TimeTableGeneration(this,
                        intentFromLogin.getStringExtra("course"),
                        intentFromLogin.getStringExtra("semester"));
                System.out.println("Semester: " + intentFromLogin.getStringExtra("semester"));

                if (network == false) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);

                    alert.setTitle("No connection");
                    alert.setMessage("Personal Timetable could not be generated as there is no network connection." +
                            "Please turn on WiFi or connection, log out and re enter your details.");

                    alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    alert.show();
                }
            } else {
                Controller.getInstance().setUser(intentFromLogin.getStringExtra("user"));
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString("User", user);
            edit.apply();
            showList(listView);
        } else {
            System.out.println("Error returned from login");
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showLogin() {
        Intent i = new Intent(this, Login.class);
        startActivityForResult(i, CHILD_ACTIVITY_CODE);
    }

    public void showList(ListView listView) {
        options.clear();
        try {
            options = Controller.getInstance().getMenu();
            ArrayAdapter<MenuObject> menuAdapter;
            menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
            listView.setAdapter(menuAdapter);
            listView.getAdapter().notify();

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

        if ((temp.getName() == "CIT TimeTables") && (Controller.getInstance().getUser().compareToIgnoreCase("Student") == 0)) {
            Intent i = new Intent(this, TimeTableActivity.class);
            startActivity(i);
        } else if (temp.getName() == "Locate Room") {
            Intent lrIntent = new Intent(this, LocateRoomActivity.class);
            startActivity(lrIntent);
        } else if (temp.getName() == "Log Out") {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("start", Boolean.FALSE);
            edit.apply();
            Toast exit = Toast.makeText(this, "Logging out...", Toast.LENGTH_LONG);
            exit.show();
            finish();

        } else {
            boolean network = isNetworkAvailable();
            if (network == false) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("No connection!");
                alert.setMessage("A network connection is required to load this site. " +
                        "Please connect to a network and try again.");

                alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alert.show();
            } else {
                Intent intent = new Intent(this, LaunchWebsite.class);
                intent.putExtra("name", temp.getName());
                intent.putExtra("url", temp.getUrl());
                startActivity(intent);
            }

        }
    }

    public void notificationBuilder() {
        int numDayOfWeek = 0;

        //getting the users current location
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String currentLongitude = String.valueOf(loc.getLongitude());
        String currentLatitude = String.valueOf(loc.getLatitude());
        String launchNavString = "Touch to show in Map?";

        //getting the current date
        DateFormat df = new SimpleDateFormat("mm H EEEE");
        String date = df.format(Calendar.getInstance().getTime());
        String[] currentTime = date.split(" ");
        int minute = Integer.parseInt(currentTime[0]);
        int currentHour = Integer.parseInt(currentTime[1]);
        String today = currentTime[2];

        if (currentHour > 12) {
            currentHour = currentHour - 12;
        }

        ArrayList<TableEntry> timeTable = Controller.getInstance().getAllTimeTableEntrys();
        ArrayList<LectureRoom> roomList = Controller.getInstance().getAllRooms();
        String titleString = "";
        boolean classFound = false;

        TableEntry te = new TableEntry();

        if (today.compareToIgnoreCase("Monday") == 0) numDayOfWeek = 1;
        else if (today.compareToIgnoreCase("Tuesday") == 0) numDayOfWeek = 2;
        else if (today.compareToIgnoreCase("Wednesday") == 0) numDayOfWeek = 3;
        else if (today.compareToIgnoreCase("Thursday") == 0) numDayOfWeek = 4;
        else if (today.compareToIgnoreCase("Friday") == 0) numDayOfWeek = 5;
        else {
            numDayOfWeek = 0;
        }

        System.out.println("Today date is " + today + " num = " + numDayOfWeek);

        if (minute < 50) {
            minute = minute + 10;
            System.out.println("Current minute " + minute);
        }

        if ((minute > 50) && (minute < 59)) {
            System.out.println("between 50-59");
            for (int j = 0; j < timeTable.size(); j++) {
                String[] startHour = timeTable.get(j).getStartTime().split(":");
                int nextClassCheck = (Integer.parseInt(startHour[0]) - 1);//get the time of the next class
                System.out.println(nextClassCheck);
                //checks if there are classes that match the time and are on today
                if ((currentHour == nextClassCheck)
                        && (numDayOfWeek == timeTable.get(j).getDay())) {
                    te = timeTable.get(j);
                    titleString = "You're next class is in room " + te.getRoomName();
                    System.out.println("Class Found");
                    classFound = true;
                }
                //if the class is at 1 and the current time is 12
                else if ((currentHour == 12) && (nextClassCheck == 0)
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

            int roomPosition = 0;
            for (int x = 0; x < roomList.size(); x++) {
                if (roomList.get(x).getRoomName() == te.getRoomName()) {
                    roomPosition = x;
                }
            }
            boolean networkCheck = isNetworkAvailable();
            String url = "http://www.google.ie/maps/dir/";
            url += currentLatitude + "," + currentLongitude + "/";
            url += roomList.get(roomPosition).getGpsLatitude() + "+" + roomList.get(roomPosition).getGpsLongitude() + "/";
            Uri uri = Uri.parse(url);//makes URL

            Intent maps = new Intent(Intent.ACTION_VIEW, uri);

            if (roomList.get(roomPosition).getGpsLongitude() != 0 && networkCheck) {
                PendingIntent pi = PendingIntent.getActivities(this, 0, new Intent[]{maps}, PendingIntent.FLAG_UPDATE_CURRENT);
                notification.setContentIntent(pi);
            }

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, notification.build());
        } else {
            System.out.println("No Time available");
        }


    }
}