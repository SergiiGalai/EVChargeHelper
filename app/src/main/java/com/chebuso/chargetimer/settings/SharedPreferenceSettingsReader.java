package com.chebuso.chargetimer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.chebuso.chargetimer.R;
import com.chebuso.chargetimer.helpers.StringHelper;

public class SharedPreferenceSettingsReader implements ISettingsReader
{
    private final SharedPreferences preferences;
    private final Context context;

    public SharedPreferenceSettingsReader(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public boolean firstApplicationRun() {
        return preferences.getBoolean("first_application_run", true);
    }

    @Override
    public boolean applicationNotificationsAllowed(){
        return preferences.getBoolean("allow_app_notifications",
                context.getResources().getBoolean(R.bool.pref_default_allow_app_notifications));
    }

    @Override
    public boolean calendarBasicNotificationsAllowed(){
        return preferences.getBoolean("allow_calendar_notifications",
                context.getResources().getBoolean(R.bool.pref_default_allow_calendar_notifications));
    }

    @Override
    public boolean calendarAdvancedNotificationsAllowed(){
        return calendarBasicNotificationsAllowed() &&
                preferences.getBoolean("allow_calendar_permission_notifications",
                        context.getResources().getBoolean(R.bool.pref_default_allow_calendar_permission_notifications));
    }

    @Override
    public double getBatteryCapacity(){
        return parseDouble("battery_capacity",
                context.getString(R.string.pref_default_battery_capacity));
    }

    @Override
    public int getChargingLossPct(){
        return parseInteger("charging_loss",
                context.getString(R.string.pref_default_charging_loss));
    }

    @Override
    public int getDefaultHomeVoltage(){
        return parseInteger("default_voltage",
                context.getString(R.string.pref_default_home_voltage));
    }

    @Override
    public int getDefaultHomeAmperage(){
        return parseInteger("default_amperage",
                context.getString(R.string.pref_default_home_amperage));
    }

    @Override
    public int getDefaultPublicVoltage(){
        return parseInteger("default_public_voltage",
                context.getString(R.string.pref_default_public_voltage));
    }

    @Override
    public int getDefaultPublicAmperage(){
        return parseInteger("default_public_amperage",
                context.getString(R.string.pref_default_public_amperage));
    }

    @Override
    public int getApplicationReminderMinutes(){
        return parseInteger("app_notification_reminder_minutes",
                context.getString(R.string.pref_default_app_notification_reminder_minutes));
    }

    @Override
    public int getCalendarReminderMinutes(){
        return parseInteger("calendar_permission_reminder_minutes",
                context.getString(R.string.pref_default_calendar_permission_reminder_minutes));
    }

    private Integer parseInteger(@NonNull String key, @NonNull String valueWhenEmpty){
        final String value = preferences.getString(key, String.valueOf(valueWhenEmpty));
        return Integer.parseInt(StringHelper.isNullOrEmpty(value) ? valueWhenEmpty : value);
    }

    private double parseDouble(@NonNull String key, @NonNull String valueWhenEmpty){
        final String value = preferences.getString(key, String.valueOf(valueWhenEmpty));
        return Double.parseDouble(StringHelper.isNullOrEmpty(value) ? valueWhenEmpty : value);
    }
}
