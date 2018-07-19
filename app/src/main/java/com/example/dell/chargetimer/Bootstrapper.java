package com.example.dell.chargetimer;

import android.app.Activity;

import com.example.dell.chargetimer.notifications.IResourceProvider;
import com.example.dell.chargetimer.notifications.NotificationScheduler;
import com.example.dell.chargetimer.notifications.ResourceProvider;
import com.example.dell.chargetimer.settings.ISettingsProvider;
import com.example.dell.chargetimer.settings.ISettingsWriter;
import com.example.dell.chargetimer.settings.SettingsWriter;
import com.example.dell.chargetimer.settings.SharedPreferenceSettingsProvider;

class Bootstrapper {

    public static ISettingsProvider ConfigureSettings(Activity activity){
        return new SharedPreferenceSettingsProvider(activity);
    }

    public static NotificationScheduler ConfigureScheduler(Activity activity){
        IResourceProvider resourceProvider = new ResourceProvider(activity);
        ISettingsWriter settingsWriter = new SettingsWriter(activity);
        ISettingsProvider settingsProvider = ConfigureSettings(activity);

        return new NotificationScheduler(activity, settingsProvider, resourceProvider, settingsWriter);
    }
}
