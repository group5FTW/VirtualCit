package com.example.liz.virtualcit.Model;

public class LectureRoom {

    private String roomName;
    private String floorLevel;
    private int gpsLongitude;
    private int gpsLatitude;


    public LectureRoom(String name, String level, int longitude, int latitude) {
        roomName = name;
        floorLevel = level;
        gpsLongitude = longitude;
        gpsLatitude = latitude;

    }

    public String getFloorLevel() {
        return floorLevel;
    }

    public void setFloorLevel(String floorLevel) {
        this.floorLevel = floorLevel;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(int gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }
}
