package com.example.dell.chargehelper;

import android.app.Activity;

import com.example.dell.chargehelper.notifications.IResourceProvider;
import com.example.dell.chargehelper.notifications.NotificationScheduler;
import com.example.dell.chargehelper.notifications.ResourceProvider;
import com.example.dell.chargehelper.settings.ISettingsProvider;
import com.example.dell.chargehelper.settings.ISettingsWriter;
import com.example.dell.chargehelper.settings.SettingsWriter;
import com.example.dell.chargehelper.settings.SharedPreferenceSettingsProvider;

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
