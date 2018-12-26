package com.sergiigalai.chargetimer;

import android.app.Activity;
import android.content.Context;

import com.sergiigalai.chargetimer.notifications.IResourceProvider;
import com.sergiigalai.chargetimer.notifications.NotificationScheduler;
import com.sergiigalai.chargetimer.notifications.ResourceProvider;
import com.sergiigalai.chargetimer.settings.ISettingsReader;
import com.sergiigalai.chargetimer.settings.ISettingsWriter;
import com.sergiigalai.chargetimer.settings.SharedPreferenceSettingsWriter;
import com.sergiigalai.chargetimer.settings.SharedPreferenceSettingsReader;

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
