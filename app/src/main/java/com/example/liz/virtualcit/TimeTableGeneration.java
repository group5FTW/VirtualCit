package com.example.liz.virtualcit;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.liz.virtualcit.Controller.Controller;
import com.example.liz.virtualcit.Model.LectureRoom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class TimeTableGeneration {

    ArrayList<LectureRoom> roomList = Controller.getInstance().getAllRooms();
    String stringOfRooms;
    public String[] roomsNumsToAdd = new String[100];
    public String[] roomsNamesToAdd = new String[100];
    int count = 0;
    String currentTime = "";
    String[] daysDelim = {"Monday ", "Tuesday ", "Wednesday ", "Thursday ", "Friday ", "Saturday ", "Sunday "};
    String firstUrlPart = "http://timetables.cit.ie:70/reporting/Individual;Student+Set;name;";
    private String secondUrlPart = "%0D%0A?weeks=";
    private String thirdUrlPart = "&days=";
    int days = 1;
    String fourthUrlPart = "&periods=";
    private String semester;
    int periodsLow = 5;
    String hyphen = "-";
    int periodsHigh = 8;
    String fifthUrlPart = "&height=100&width=100";

    public TimeTableGeneration(HomePage hp, String course, String semesterChoice) {
        setSemester(semesterChoice);
        String siteUrl = firstUrlPart + course + secondUrlPart + semester + thirdUrlPart;
        new ParseURL().execute(new String[]{siteUrl});
        Toast ttToast = Toast.makeText(hp, "Time Table Generating.. Please Wait..", Toast.LENGTH_LONG);
        ttToast.show();
    }

    private void setSemester(String semesterChoice) {
        if (semesterChoice.compareToIgnoreCase("Semester 1") == 0) {
            semester = "4-16";
        }

        if (semesterChoice.compareToIgnoreCase("Semester 2") == 0) {
            semester = "24-31";
        }
    }

    private class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
            String[] times = {"9:00", "10:00", "11:00", "12:00", "1:00", "2:00", "3:00", "4:00", "5:00"};
            int[] periods = {5, 9, 13, 17, 21, 25, 29, 33, 37};
            String daysString = "";

            mainLoop:
            for (int y = 0; y < 9; y++) {
                String siteUrl2 = strings[0] + days + fourthUrlPart + periodsLow + hyphen + periodsHigh + fifthUrlPart;
                catchABreak:
                try {
                    buffer = new StringBuffer();
                    Document doc = Jsoup.connect(siteUrl2).get();
                    String text = doc.body().text();

                    String[] parts = text.split(daysDelim[days - 1]);

                    if (parts[1] != null) {
                        String newStringMinusStuff = parts[1];
                        String[] roomStuff = newStringMinusStuff.split(" ");

                        outerLoop:
                        for (int i = 1; i < roomStuff.length; i++) {
                            innerLoop:
                            for (int j = 0; j < roomList.size(); j++) {

                                stringOfRooms = roomList.get(j).getRoomName();
                                if (roomStuff[i].equalsIgnoreCase(stringOfRooms)) {
                                    roomsNumsToAdd[count] = roomStuff[i];

                                    break outerLoop;
                                }

                                if (!(roomStuff[1].matches("[A-Za-z0-9]+"))) {
                                    break catchABreak;
                                }
                            }

                            buffer.append(roomStuff[i] + " ");

                        }

                        roomsNamesToAdd[count] = buffer.toString();
                        for (int a = 0; a < times.length; a++) {
                            if (periodsLow == periods[a]) {
                                currentTime = times[a];
                            }
                        }

                        try {
                            Controller.getInstance().populateTimeTable(roomsNamesToAdd[count], roomsNumsToAdd[count],
                                    currentTime, days);
                        } catch (NullPointerException e) {
                            break;
                        }
                    }
                    count++;
                } catch (Throwable t) {
                    t.printStackTrace();
                }

                periodsLow += 4;
                periodsHigh += 4;

                if (y == 8) {
                    y = 0;
                    days++;
                    periodsLow = 5;
                    periodsHigh = 8;

                    if (days == 6) {
                        break mainLoop;
                    }
                }
            }
            return buffer.toString();
        }
    }
}