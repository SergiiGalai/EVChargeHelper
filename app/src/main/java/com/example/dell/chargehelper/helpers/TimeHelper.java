package com.example.dell.chargehelper.helpers;

import com.example.dell.chargehelper.Time;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    private static Format timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

    public static String formatAsHoursWithMinutes(Date date){
        return timeFormatter.format(date);
    }

    public static Time getHoursAndMinutes(long millis){
        return new Time(
                (int)TimeUnit.MILLISECONDS.toHours(millis) % 24,
                (int)TimeUnit.MILLISECONDS.toMinutes(millis) % 60);
    }

    public static Date toDate(long millis){
        return new Date(millis);
    }

    public static long addToNow(long millis){
        return System.currentTimeMillis() + millis;
    }

    public static long convertMinutesToMs(int minutes){
        if (minutes < 0)
            throw new IllegalArgumentException("minutes must be positive");

        return minutes * 60 * 1000;
    }
}
