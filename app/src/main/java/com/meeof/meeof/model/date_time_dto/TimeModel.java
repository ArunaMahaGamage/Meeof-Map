package com.meeof.meeof.model.date_time_dto;

/**
 * Created by Anuja Ranwalage on 10/18/17.
 */

public class TimeModel {
    int hour ;
    int minute;
    boolean is24hours;

    public TimeModel(int hour, int minute, boolean is24hours) {
        this.hour = hour;
        this.minute = minute;
        this.is24hours = is24hours;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean is24hours() {
        return is24hours;
    }

    public void setIs24hours(boolean is24hours) {
        this.is24hours = is24hours;
    }
}
