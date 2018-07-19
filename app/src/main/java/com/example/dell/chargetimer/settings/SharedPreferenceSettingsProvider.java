package com.example.dell.chargetimer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceSettingsProvider implements ISettingsProvider
{
    private final SharedPreferences preferences;

    public SharedPreferenceSettingsProvider(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
        return googleBasicNotificationsAllowed() && preferences.getBoolean("allow_calendar_permission_notifications", true);
    }

    @Override
    public Double getBatteryCapacity(){
        return Double.parseDouble(preferences.getString("battery_capacity","11"));
    }

    @Override
    public Integer getChargingLossPct(){
        return Integer.parseInt(preferences.getString("charging_loss", "12"));
    }

    @Override
    public Integer getDefaultAmperage(){
        return Integer.parseInt(preferences.getString("default_amperage",  "16"));
    }

    @Override
    public Integer getDefaultVoltage(){
        return Integer.parseInt(preferences.getString("default_voltage", "220"));
    }

    @Override
    public int getApplicationReminderMinutes(){
        return Integer.parseInt(preferences.getString("app_notification_reminder_minutes", String.valueOf(10)));
    }

    @Override
    public int getCalendarReminderMinutes(){
        return Integer.parseInt(preferences.getString("calendar_permission_reminder_minutes", String.valueOf(15)));
    }
}
