package com.example.dell.chargehelper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeHelper
{
    private static Format timeFormatter = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

    public static String toHoursAndMinutes(Date date){
        return timeFormatter.format(date);
    }

    public static Time toTime(long ms){
        return new Time((int)TimeUnit.MILLISECONDS.toHours(ms) % 24, (int)TimeUnit.MILLISECONDS.toMinutes(ms) % 60);
    }

    public static Date toDate(long ms){
        return new Date(ms);
    }

    public static long addToNow(long ms){
        return System.currentTimeMillis() + ms;
    }

    public static long convertMinutesToMs(int minutes){
        if (minutes < 0)
            throw new IllegalArgumentException("minutes must be positive");

        return minutes * 60 * 1000;
    }
}
