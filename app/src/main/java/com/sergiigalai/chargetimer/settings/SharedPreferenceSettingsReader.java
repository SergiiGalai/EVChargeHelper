package com.sergiigalai.chargetimer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceSettingsReader implements ISettingsReader
{
    private final SharedPreferences preferences;

    public SharedPreferenceSettingsReader(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean firstApplicationRun() {
        return preferences.getBoolean("first_application_run", true);
    }

    @Override
    public boolean applicationNotificationsAllowed(){
        return preferences.getBoolean("allow_app_notifications", true);
    }

    @Override
    public boolean googleBasicNotificationsAllowed(){
        return preferences.getBoolean("allow_calendar_notifications", true);
    }

    @Override
    public boolean googleAdvancedNotificationsAllowed(){
        return googleBasicNotificationsAllowed() &&
                preferences.getBoolean("allow_calendar_permission_notifications", true);
    }

    @Override
    public double getBatteryCapacity(){
        return parseDouble("battery_capacity",11);
    }

    @Override
    public int getChargingLossPct(){
        return parseInteger("charging_loss", 12);
    }

    @Override
    public int getDefaultAmperage(){
        return parseInteger("default_amperage", 16);
    }

    @Override
    public int getDefaultVoltage(){
        return parseInteger("default_voltage", 220);
    }

    @Override
    public int getApplicationReminderMinutes(){
        return parseInteger("app_notification_reminder_minutes", 10);
    }

    @Override
    public int getCalendarReminderMinutes(){
        return parseInteger("calendar_permission_reminder_minutes", 15);
    }

    private Integer parseInteger(String key, Integer valueWhenEmpty){
        final String value = preferences.getString(key, String.valueOf(valueWhenEmpty));
        if (value.equals(""))
            return valueWhenEmpty;
        return Integer.parseInt(value);
    }

    private double parseDouble(String key, double valueWhenEmpty){
        final String value = preferences.getString(key, String.valueOf(valueWhenEmpty));
        if (value.equals(""))
            return valueWhenEmpty;
        return Double.parseDouble(value);
    }
}
