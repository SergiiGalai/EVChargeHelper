package com.example.dell.chargehelper.helpers;

import android.preference.Preference;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    public static String getValue(Preference preference){
        return PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");
    }

}
