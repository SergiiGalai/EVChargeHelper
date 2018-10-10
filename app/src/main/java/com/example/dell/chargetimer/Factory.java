package com.example.dell.chargetimer;

import android.app.Activity;

import com.example.dell.chargetimer.notifications.IResourceProvider;
import com.example.dell.chargetimer.notifications.NotificationScheduler;
import com.example.dell.chargetimer.notifications.ResourceProvider;
import com.example.dell.chargetimer.settings.ISettingsReader;
import com.example.dell.chargetimer.settings.ISettingsWriter;
import com.example.dell.chargetimer.settings.SharedPreferenceSettingsWriter;
import com.example.dell.chargetimer.settings.SharedPreferenceSettingsReader;

class Factory {

    public static ISettingsReader createSettings(Activity activity){
        return new SharedPreferenceSettingsReader(activity);
    }

    public static NotificationScheduler createScheduler(Activity activity){
        IResourceProvider resourceProvider = new ResourceProvider(activity);
        ISettingsWriter settingsWriter = new SharedPreferenceSettingsWriter(activity);
        ISettingsReader settingsProvider = createSettings(activity);

        return new NotificationScheduler(activity, settingsProvider, resourceProvider, settingsWriter);
    }
}
