package com.chebuso.chargetimer.helpers;

import com.chebuso.chargetimer.Time;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    public static String formatAsShortDateTime(Date date){
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
    }

    public static String formatAsShortTime(Date date){
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public static Time toTime(long millis){
        return new Time(
                (int)TimeUnit.MILLISECONDS.toDays(millis),
                (int)TimeUnit.MILLISECONDS.toHours(millis) % 24,
                (int)TimeUnit.MILLISECONDS.toMinutes(millis) % 60);
    }

    public static Date toDate(long millis){
        return new Date(millis);
    }
    public static long now(){ return System.currentTimeMillis(); }
    public static long convertMinutesToMs(int minutes){
        if (minutes < 0)
            throw new IllegalArgumentException("minutes must be positive");

        return minutes * 60 * 1000;
    }
}
