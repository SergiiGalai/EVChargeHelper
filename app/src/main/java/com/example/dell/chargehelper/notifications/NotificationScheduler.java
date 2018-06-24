package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.dell.chargehelper.helpers.PermissionHelper;
import com.example.dell.chargehelper.settings.ISettingsProvider;
import com.example.dell.chargehelper.settings.SharedPreferenceSettingsProvider;

public class NotificationScheduler
{
    private NotificatorFactory notificatorFactory;
    private ISettingsProvider settingsProvider;

    public NotificationScheduler(Activity activity) {
        settingsProvider = new SharedPreferenceSettingsProvider(activity);
        notificatorFactory = new NotificatorFactory(settingsProvider, activity);
    }

    public void schedule(long millisToEvent){
        for (INotificator notificator : notificatorFactory.createNotificators()){
            notificator.scheduleCarChargedNotification(millisToEvent);
        }
    }

    public void schedule(@NonNull int[] grantResults, long millisToEvent){
        INotificator notificator = notificatorFactory.tryCreate(PermissionHelper.isPermissionsGranted(grantResults));
        if (notificator != null)
            notificator.scheduleCarChargedNotification(millisToEvent);
    }
}
