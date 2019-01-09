package com.chebuso.chargetimer;

import android.app.Activity;
import android.content.Context;

import com.chebuso.chargetimer.notifications.IResourceProvider;
import com.chebuso.chargetimer.notifications.NotificationScheduler;
import com.chebuso.chargetimer.notifications.ResourceProvider;
import com.chebuso.chargetimer.settings.ISettingsReader;
import com.chebuso.chargetimer.settings.ISettingsWriter;
import com.chebuso.chargetimer.settings.SharedPreferenceSettingsWriter;
import com.chebuso.chargetimer.settings.SharedPreferenceSettingsReader;

class Factory {

    static ISettingsReader createSettingsReader(Context context){
        return new SharedPreferenceSettingsReader(context);
    }

    static ISettingsWriter createSettingsWriter(Context context) {
        return new SharedPreferenceSettingsWriter(context);
    }

    static NotificationScheduler createScheduler(Activity activity){
        IResourceProvider resourceProvider = new ResourceProvider(activity);
        ISettingsWriter settingsWriter = new SharedPreferenceSettingsWriter(activity);
        ISettingsReader settingsProvider = createSettingsReader(activity);

        return new NotificationScheduler(activity, settingsProvider, resourceProvider, settingsWriter);
    }
}
