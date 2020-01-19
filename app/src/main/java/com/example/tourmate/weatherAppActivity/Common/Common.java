package com.example.tourmate.weatherAppActivity.Common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String APP_ID = "d6a7466c13492f751762f325b1c00c96";
    public static Location current_location = null;

    public static String convertUnixTiDate(long dt) {
        Date date = new Date(dt*1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formatted = dateFormat.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long sunrise) {
        Date date = new Date(sunrise*1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formatted = dateFormat.format(date);
        return formatted;
    }
}
