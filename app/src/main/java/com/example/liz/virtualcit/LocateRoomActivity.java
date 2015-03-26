package com.example.liz.virtualcit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
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
    Location currentLoc;
    LocationManager lm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate_room_activity);
        listView = (ListView) findViewById(R.id.listView2);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                currentLoc = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

        };

        roomList = Controller.getInstance().getAllRooms();
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

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

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
        if (lr.getGpsLongitude() == 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Location No Available");
            alert.setMessage("This version does not contain location for this room. Please choose another");

            alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            alert.show();
        } else {
            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(crit, true);
            Location loc = lm.getLastKnownLocation(provider);

            String currentLongitude = String.valueOf(loc.getLongitude());
            String currentLatitude = String.valueOf(loc.getLatitude());

            String url = "http://www.google.ie/maps/dir/";
            url += currentLatitude + "," + currentLongitude + "/";
            url += lr.getGpsLatitude() + "+" + lr.getGpsLongitude() + "/";
            Uri uri = Uri.parse(url);//makes URL

            Intent map = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(map);
        }

    }
}