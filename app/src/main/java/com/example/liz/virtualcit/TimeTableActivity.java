package com.example.liz.virtualcit;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.liz.virtualcit.Controller.Controller;
import com.example.liz.virtualcit.Model.TableEntry;

import java.util.ArrayList;

public class TimeTableActivity extends HomePage {
    ListView lv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        lv = (ListView) findViewById(R.id.timeTableList);

        ArrayList<TableEntry> values = Controller.getInstance().getAllTimeTableEntrys();
        ArrayAdapter<TableEntry> adapter = new ArrayAdapter<TableEntry>(this,
                android.R.layout.simple_list_item_1, values);
        lv.setAdapter(adapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
