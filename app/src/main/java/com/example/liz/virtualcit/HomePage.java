package com.example.liz.virtualcit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.listView);
        showList(listView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        Intent intent = new Intent(this, LaunchWebsite.class);
        intent.putExtra("name", temp.getName());
        intent.putExtra("url", temp.getUrl());
        startActivity(intent);
    }

}
