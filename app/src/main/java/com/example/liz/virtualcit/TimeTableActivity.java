package com.example.liz.virtualcit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.liz.virtualcit.Controller.Controller;
import com.example.liz.virtualcit.Model.TableEntry;

import org.jsoup.Jsoup;

import java.util.ArrayList;

public class TimeTableActivity extends ActionBarActivity {
    String stringOfRooms = "IT1.2 F1.2 IT2.3 IT1.3 B219 B165";
    String[] roomArray = stringOfRooms.split(" ");
    public String[] roomsNumsToAdd = new String[100];
    public String[] roomsNamesToAdd = new String[100];
    String[] timesToAdd = new String[100];
    int count = 0;
    int countTime = 0;
    String firstUrlPart = "http://timetables.cit.ie:70/reporting/Individual;Programme+Of+Study;name;";
    String course = "CO.DCOM3+-+KSDEV_8_Y3";
    String secondUrlPart = "%0D%0A?weeks=";
    String semester1 = "4-16";
    String semester2 = "24-31";
    String thirdUrlPart = "&days=";
    String days[] = {"1", "2", "3", "4", "5"};
    String fourthUrlPart = "&periods=";
    int periodsLow = 5;
    String hyphen = "-";
    int periodsHigh = 8;
    String fifthUrlPart = "&height=100&width=100";
    ListView lv;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        lv = (ListView) findViewById(R.id.timeTableList);
        for (int y = 0; y < 9; y++) {
            String siteUrl = firstUrlPart + course + secondUrlPart + semester2 + thirdUrlPart + days[0] + fourthUrlPart + periodsLow + hyphen + periodsHigh + fifthUrlPart;
            new ParseURL().execute(new String[]{siteUrl});
            periodsLow += 4;
            periodsHigh += 4;
        }

        ArrayList<TableEntry> values = Controller.getInstance().getAllTimeTableEntrys();
        ArrayAdapter<TableEntry> adapter = new ArrayAdapter<TableEntry>(this,
                android.R.layout.simple_list_item_1, values);
        lv.setAdapter(adapter);
    }

    private class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
            String[] times = {"9:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "12:00 - 1:00", "1:00 - 2:00", "2:00 - 3:00", "3:00 - 4:00", "4:00 - 5:00", "5:00 - 6:00"};

            try {

                org.jsoup.nodes.Document doc = Jsoup.connect(strings[0]).get();

                String text = doc.body().text();
                String[] parts = text.split("CO.DCOM3 ");
                String newStringMinusStuff = parts[2];
                String[] roomStuff = newStringMinusStuff.split(" ");


                outerLoop:
                for (int i = 0; i < roomStuff.length; i++) {
                    innerLoop:
                    for (int j = 0; j < roomArray.length; j++) {
                        if (roomStuff[i].equalsIgnoreCase(roomArray[j])) {
                            roomsNumsToAdd[count] = roomStuff[i];

                            break outerLoop;
                        }
                    }

                    buffer.append(roomStuff[i] + " ");

                }

                roomsNamesToAdd[count] = buffer.toString();
                Controller.getInstance().populateTimeTable(roomsNamesToAdd[count], roomsNumsToAdd[count],
                        times[countTime], days[0]);

                countTime++;
                count++;

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return buffer.toString();
        }
    }
}
