package com.example.dell.chargetimer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceSettingsWriter implements ISettingsWriter {
    private final SharedPreferences preferences;

    public SharedPreferenceSettingsWriter(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void setFirstApplicationRunCompleted() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("first_application_run", false);
        editor.apply();
    }

    @Override
    public void saveGoogleAdvancedNotificationsAllowed(boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allow_calendar_permission_notifications", value);
        editor.apply();
    }
}
