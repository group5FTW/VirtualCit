package com.example.liz.virtualcit;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.liz.virtualcit.Model.TableEntry;


public class TimeTableActivity extends ListActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timetable);


        //List<TableEntry> values = datasource.getAllTableEntrys();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        //ArrayAdapter<TableEntry> adapter = new ArrayAdapter<TableEntry>(this,
        //android.R.layout.simple_list_item_1, values);
        //setListAdapter(adapter);

    }

    public void onClick(View view) {
        ArrayAdapter<TableEntry> adapter = (ArrayAdapter<TableEntry>) getListAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        //datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //datasource.close();
        super.onPause();
    }

}
