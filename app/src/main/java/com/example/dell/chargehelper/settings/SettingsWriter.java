package com.example.dell.chargehelper.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsWriter
{
    private final SharedPreferences preferences;

    public SettingsWriter(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveGoogleAdvancedNotificationsAllowed(boolean value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("allow_calendar_permission_notifications", value);
        editor.commit();
    }
}
