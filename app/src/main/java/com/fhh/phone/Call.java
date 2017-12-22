package com.fhh.phone;

import android.net.Uri;


/**
 * 来电的相关信息
 * Created by FengHaHa on 2017/12/21.
 */

public class Call {
    public static final int CALL_IN=1;
    public static final int CALL_OUT=2;
    public static final int CALL_MISSED=3;
    private String name;
    private String phoneNumber;
    private Uri photo;
    private String date;
    private String time;
    private String duration;
    private int type;

    public Call(String name, String phoneNumber, Uri photo, String date, String time, int type, String duration) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.date = date;
        this.time = time;
        this.type = type;
        this.duration = duration;
    }


    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Uri getPhoto() {
        return photo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public String getDuration() {
        return duration;
    }
}
