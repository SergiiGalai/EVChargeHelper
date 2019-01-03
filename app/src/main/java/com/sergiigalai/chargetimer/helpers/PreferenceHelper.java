package com.sergiigalai.chargetimer.helpers;

import android.preference.Preference;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    public static String getValue(Preference preference){
        return PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");
    }

    public static void setChangeListenerAndTriggerChange(Preference preference,
                                                         Preference.OnPreferenceChangeListener changeListener){
        preference.setOnPreferenceChangeListener(changeListener);
        changeListener.onPreferenceChange(preference, getValue(preference));
    }

}
