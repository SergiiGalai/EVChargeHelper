package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.dell.chargehelper.SettingsProvider;

import java.util.ArrayList;
import java.util.List;

public class NotificatorFactory {

    @NonNull
    public List<INotificator> createNotificators(Activity activity) {
        ArrayList<INotificator> r = new ArrayList<>();
        SettingsProvider settingsProvider = new SettingsProvider(activity);

        if (settingsProvider.applicationNotificationsAllowed()){
            r.add(new ApplicationNotificator(activity));
        }
        if (settingsProvider.googleBasicNotificationsAllowed()){
            r.add(new GoogleCalendarDefaultNotificator(activity));
        }
        if (settingsProvider.googleAdvancedNotificationsAllowed()){
            r.add(new GoogleCalendarAdvancedNotificator(activity));
        }
        return r;
    } 
}
