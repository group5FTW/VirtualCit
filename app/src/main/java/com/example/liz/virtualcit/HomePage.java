package com.example.liz.virtualcit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.example.liz.virtualcit.Model.MenuObject;

import java.util.ArrayList;

public class HomePage extends ActionBarActivity {
    private static final int CHILD_ACTIVITY_CODE = 1234;
    private ArrayList<MenuObject> options = new ArrayList<>();
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Controller.getInstance().databaseConnection(this);

        /*try
        {
            Controller.getInstance().localHostConnection(this);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }*/

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
        }
    }

    public void showList(ListView listView) {
        options = Controller.getInstance().getMenu();
        Controller.getInstance().notificationBuilder(this);

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
        } else if (temp.getName() == "Go to F1.6 Block") {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            String currentLongitude = String.valueOf(loc.getLongitude());
            String currentLatitude = String.valueOf(loc.getLatitude());
            String url = "http://www.google.ie/maps/dir/";
            url += currentLatitude + "," + currentLongitude + "/";
            url += temp.getUrl() + "/";
            Uri uri = Uri.parse(url);
            Intent maps = new Intent(Intent.ACTION_VIEW, uri);
            Toast mapToast = Toast.makeText(this, url, Toast.LENGTH_LONG);
            mapToast.show();
            startActivity(maps);
        } else {
            Intent intent = new Intent(this, LaunchWebsite.class);
            intent.putExtra("name", temp.getName());
            intent.putExtra("url", temp.getUrl());
            startActivity(intent);
        }
    }
}