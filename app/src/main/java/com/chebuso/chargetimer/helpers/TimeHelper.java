package com.chebuso.chargetimer.helpers;

import android.util.Log;

import com.chebuso.chargetimer.Time;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    private static final String TAG = "TimeHelper";

    public static String formatAsShortDateTime(Date date){
        Log.d(TAG, "formatAsShortDateTime");
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
    }

    public static String formatAsShortTime(Date date){
        Log.d(TAG, "formatAsShortTime");
        return DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
    }

    public static Time toTime(long millis){
        Log.d(TAG, "toTime");
        return new Time(
                (int)TimeUnit.MILLISECONDS.toDays(millis),
                (int)TimeUnit.MILLISECONDS.toHours(millis) % 24,
                (int)TimeUnit.MILLISECONDS.toMinutes(millis) % 60);
    }

    public static Date toDate(long millis){
        Log.d(TAG, "toDate");
        return new Date(millis);
    }
    public static long now(){ return System.currentTimeMillis(); }
    public static long convertMinutesToMs(int minutes){
        Log.d(TAG, "convertMinutesToMs");
        if (minutes < 0)
            throw new IllegalArgumentException("minutes must be positive");

        return minutes * 60 * 1000;
    }
}
