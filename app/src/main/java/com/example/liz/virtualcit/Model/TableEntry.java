package com.example.liz.virtualcit.Model;


public class TableEntry {
    private String module;
    private String roomName;
    private String startTime;
    private int day;
    private String[] dayName = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public String getModule() {
        return module;
    }

    public void setModule(String moduleName) {
        this.module = moduleName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String room) {
        this.roomName = room;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String moduleStartTime) {
        this.startTime = moduleStartTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int dayNo) {
        this.day = dayNo;
    }

    public String getDayName() {
        return dayName[getDay()];
    }

    @Override
    public String toString() {
        return startTime + " " + getDayName() + " " + roomName + " "
                + module;
    }
}

