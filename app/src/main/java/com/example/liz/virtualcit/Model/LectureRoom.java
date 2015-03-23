package com.example.liz.virtualcit.Model;

public class LectureRoom {

    private String roomName;
    private String floorLevel;
    private int gpsLongitude;
    private int gpsLatitude;

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

    public int getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(int gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }
}
