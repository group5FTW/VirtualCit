package com.example.liz.virtualcit;

import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.liz.virtualcit.Controller.Controller;
import com.example.liz.virtualcit.Model.MenuObject;

import java.util.ArrayList;


public class HomePage extends ActionBarActivity {
    ArrayList<MenuObject> options = new ArrayList<MenuObject>();
    ListView listView;
    int count = 0;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean previouslyStarted = prefs.getBoolean(getString(R.string.pref_prev_started), false);
        if (!previouslyStarted) {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(getString(R.string.pref_prev_started), Boolean.TRUE);
            edit.commit();
            showLogin();
        }
        Intent intentFromLogin = getIntent();
        user = intentFromLogin.getStringExtra("user");



        listView = (ListView) findViewById(R.id.listView);
        if (count == 0)//initializes first opening of the map
        {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent i;

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.image1) {
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
        startActivity(i);
    }

    public void showList(ListView listView) {
        options = Controller.getInstance().getMenu();
        try {
            ArrayAdapter<MenuObject> menuAdapter;
            menuAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, options);
            this.listView.setAdapter(menuAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

        if (temp.getName() == "TimeTable" && user == "Student") {
            Intent i = new Intent(this, TimeTableActivity.class);
        } else {
            Intent intent = new Intent(this, LaunchWebsite.class);
            intent.putExtra("name", temp.getName());
            intent.putExtra("url", temp.getUrl());
            startActivity(intent);
        }



    }
}
