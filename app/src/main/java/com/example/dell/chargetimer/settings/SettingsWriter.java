package com.example.dell.chargetimer.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsWriter implements ISettingsWriter {
    private final SharedPreferences preferences;

    public SettingsWriter(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void saveGoogleAdvancedNotificationsAllowed(boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allow_calendar_permission_notifications", value);
        editor.commit();
    }
}
