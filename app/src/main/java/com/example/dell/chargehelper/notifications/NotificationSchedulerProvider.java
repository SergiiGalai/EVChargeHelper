package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.dell.chargehelper.SettingsProvider;

import java.util.ArrayList;
import java.util.List;

public class NotificationSchedulerProvider {

    @NonNull
    public List<ICarChargedNotificationScheduler> getNotificationSchedulers(Activity activity) {
        ArrayList<ICarChargedNotificationScheduler> r = new ArrayList<>();
        SettingsProvider settingsProvider = new SettingsProvider(activity);

        if (settingsProvider.applicationNotificationsAllowed()){
            r.add(new CarChargedAlarmScheduler(activity));
        }
        if (settingsProvider.googleCalendarNotificationsAllowed()){
            r.add(new CarChargedCalendarEventScheduler(activity));
        }
        if (settingsProvider.googlePermissionCalendarNotificationsAllowed()){
            r.add(new CarChargedDirectCalendarWriteScheduler(activity));
        }
        return r;
    } 
}
