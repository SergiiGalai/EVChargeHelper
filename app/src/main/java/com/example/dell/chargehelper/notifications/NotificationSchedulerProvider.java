package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.dell.chargehelper.MainActivity;
import com.example.dell.chargehelper.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationSchedulerProvider {

    @NonNull
    public List<ICarChargedNotificationScheduler> getNotificationSchedulers(Activity activity) {
        ArrayList<ICarChargedNotificationScheduler> r = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        if (preferences.getBoolean("allow_app_notifications", SettingsActivity.DEFAULT_ALLOW_APP_NOTIFICATIONS)){
            r.add(new CarChargedAlarmScheduler(activity));
        }
        if (preferences.getBoolean("allow_calendar_notifications", SettingsActivity.DEFAULT_ALLOW_CALENDAR_NOTIFICATIONS)){
            r.add(new CarChargedCalendarEventScheduler(activity));
        }
        if (preferences.getBoolean("allow_calendar_permission_notifications", SettingsActivity.DEFAULT_ALLOW_CALENDAR_PERMISSION_NOTIFICATIONS)){
            r.add(new CarChargedDirectCalendarWriteScheduler(activity));
        }
        return r;
    } 
}
