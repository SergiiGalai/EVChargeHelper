package com.example.dell.chargehelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsProvider
{
    private final SharedPreferences preferences;

    public SettingsProvider(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean applicationNotificationsAllowed(){
        return preferences.getBoolean("allow_app_notifications", true);
    }

    public boolean googleCalendarNotificationsAllowed(){
        return preferences.getBoolean("allow_calendar_notifications", true);
    }

    public boolean googlePermissionCalendarNotificationsAllowed(){
        return preferences.getBoolean("allow_calendar_permission_notifications", false);
    }

    public Double getBatteryCapacity(){
        return Double.parseDouble(preferences.getString("battery_capacity","11"));
    }

    public Integer getChargingLossPct(){
        return Integer.parseInt(preferences.getString("charging_loss", "12"));
    }

    public Integer getDefaultAmperage(){
        return Integer.parseInt(preferences.getString("default_amperage",  "16"));
    }

    public Integer getDefaultVoltage(){
        return Integer.parseInt(preferences.getString("default_voltage", "220"));
    }

    public int getNotificationReminderMinutes(){
        return Integer.parseInt(preferences.getString("app_notification_reminder_minutes", String.valueOf(10)));
    }

    public int getCalendarReminderMinutes(){
        return Integer.parseInt(preferences.getString("calendar_permission_reminder_minutes", String.valueOf(15)));
    }
}
