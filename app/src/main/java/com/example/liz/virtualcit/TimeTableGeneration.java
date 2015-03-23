package com.example.liz.virtualcit;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.liz.virtualcit.Controller.Controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

public class TimeTableGeneration {
    String stringOfRooms = "IT1.2 F1.2 IT2.3 IT1.3 B219 B165";
    String[] roomArray = stringOfRooms.split(" ");
    public String[] roomsNumsToAdd = new String[100];
    public String[] roomsNamesToAdd = new String[100];
    String[] timesToAdd = new String[100];
    int count = 0;
    String currentTime = "";
    private String firstUrlPart = "http://timetables.cit.ie:70/reporting/Individual;Programme+Of+Study;name;";
    private String secondUrlPart = "%0D%0A?weeks=";
    private String thirdUrlPart = "&days=";
    int days = 1;
    String fourthUrlPart = "&periods=";
    private String semester;
    int periodsLow = 5;
    String hyphen = "-";
    int periodsHigh = 8;
    String fifthUrlPart = "&height=100&width=100";
    private ArrayList timeTableList;

    public TimeTableGeneration(HomePage hp, String course, String semesterChoice) {
        setSemester(semesterChoice);
        String siteUrl = firstUrlPart + course + secondUrlPart + semester + thirdUrlPart;
        new ParseURL().execute(new String[]{siteUrl});
        Toast ttToast = Toast.makeText(hp, "Time Table Created", Toast.LENGTH_LONG);
        ttToast.show();
    }

    private void setSemester(String semesterChoice) {
        if (semesterChoice == "Semester 1") {
            semester = "4-16";
        }

        if (semesterChoice == "Semester 2") {
            semester = "24-31";
        }
    }

    public ArrayList getTimeTable() {
        timeTableList = Controller.getInstance().getAllTimeTableEntrys();
        return timeTableList;
    }

    private class ParseURL extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer buffer = new StringBuffer();
            String[] times = {"9:00", "10:00", "11:00", "12:00", "1:00", "2:00", "3:00", "4:00", "5:00"};
            int[] periods = {5, 9, 13, 17, 21, 25, 29, 33, 37};
            mainLoop:
            for (int y = 0; y < 9; y++) {
                String siteUrl2 = strings[0] + days + fourthUrlPart + periodsLow + hyphen + periodsHigh + fifthUrlPart;
                try {
                    buffer = new StringBuffer();
                    Document doc = Jsoup.connect(siteUrl2).get();

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
                    System.out.println(roomsNamesToAdd[count]);
                    System.out.println(roomsNumsToAdd[count]);
                    for (int a = 0; a < times.length; a++) {
                        if (periodsLow == periods[a]) {
                            currentTime = times[a];
                        }
                    }
                    System.out.println(days);
                    String daysString = "";
                    daysString += days;

                    Controller.getInstance().populateTimeTable(roomsNamesToAdd[count], roomsNumsToAdd[count],
                            currentTime, daysString);

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
