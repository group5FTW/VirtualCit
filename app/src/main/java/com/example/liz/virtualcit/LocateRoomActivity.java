package com.example.liz.virtualcit;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.liz.virtualcit.Controller.Controller;
import com.example.liz.virtualcit.Model.LectureRoom;

import java.util.ArrayList;

public class LocateRoomActivity extends ActionBarActivity {
    ListView listView;
    ArrayList<LectureRoom> roomList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate_room_activity);
        listView = (ListView) findViewById(R.id.listView2);

        roomList = Controller.getInstance().getAllRooms();
        System.out.println(roomList.get(2).getRoomName());
        System.out.println(roomList.get(2).getGpsLatitude());
        try {
            ArrayAdapter<LectureRoom> roomArrayAdapter;
            roomArrayAdapter = new ArrayAdapter<LectureRoom>(this, android.R.layout.simple_list_item_1, roomList);
            System.out.println("Adapter");
            listView.setAdapter(roomArrayAdapter);
            System.out.println("Adapter set");

        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String roomChoice = (String) ((TextView) view).getText();
                System.out.println("Item selected");
                goButtonClick(roomChoice);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void goButtonClick(String roomChoice) {
        LectureRoom lr = new LectureRoom();
        for (int i = 0; i < roomList.size(); i++) {
            if (roomList.get(i).getRoomName().equals(roomChoice)) {
                lr = roomList.get(i);
            }
        }
        System.out.println("Room Found");
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        System.out.println("Location got");

        String currentLongitude = String.valueOf(loc.getLongitude());
        String currentLatitude = String.valueOf(loc.getLatitude());
        System.out.println(currentLongitude + " " + currentLatitude);
        String url = "http://www.google.ie/maps/dir/";
        url += currentLatitude + "," + currentLongitude + "/";
        url += lr.getGpsLatitude() + "+" + lr.getGpsLongitude() + "/";
        Uri uri = Uri.parse(url);//makes URL
        System.out.println("URL parsed");

        Intent map = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(map);
    }
}