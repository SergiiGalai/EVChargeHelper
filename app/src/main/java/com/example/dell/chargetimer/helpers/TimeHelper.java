package com.example.dell.chargetimer.helpers;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.example.dell.chargetimer.Time;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    public static String formatAsHoursWithMinutes(Context context, Date date){
        final Format timeFormatter = android.text.format.DateFormat.getTimeFormat(context);
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
    public static long now(){ return System.currentTimeMillis(); }
    public static long convertMinutesToMs(int minutes){
        if (minutes < 0)
            throw new IllegalArgumentException("minutes must be positive");

        return minutes * 60 * 1000;
    }
}
