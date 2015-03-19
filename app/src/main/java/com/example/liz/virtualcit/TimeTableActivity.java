package com.example.liz.virtualcit;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.liz.virtualcit.Model.TableEntry;

import java.util.List;
import java.util.Random;


public class TimeTableActivity extends ListActivity {
    private DatabaseManagement datasource;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_timetable);
        datasource = new DatabaseManagement(this);
        datasource.open();

        List<TableEntry> values = datasource.getAllTableEntrys();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<TableEntry> adapter = new ArrayAdapter<TableEntry>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);

    }

    public void onClick(View view) {
        @SuppressWarnings("unchecked")
        ArrayAdapter<TableEntry> adapter = (ArrayAdapter<TableEntry>) getListAdapter();
        TableEntry tableEntry = null;
        switch (view.getId()) {
            case R.id.add:
                String[] tableEntrys = new String[]{"Cool", "Very nice", "Hate it"};
                int nextInt = new Random().nextInt(3);
                // save the new tableEntry to the database
                tableEntry = datasource.createTableEntry(tableEntrys[nextInt]);
                adapter.add(tableEntry);
                break;
            case R.id.delete:
                if (getListAdapter().getCount() > 0) {
                    tableEntry = (TableEntry) getListAdapter().getItem(0);
                    datasource.deleteTableEntry(tableEntry);
                    adapter.remove(tableEntry);
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

}
