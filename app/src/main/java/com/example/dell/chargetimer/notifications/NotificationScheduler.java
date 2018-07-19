package com.example.dell.chargetimer.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.dell.chargetimer.helpers.PermissionHelper;
import com.example.dell.chargetimer.settings.ISettingsProvider;
import com.example.dell.chargetimer.settings.ISettingsWriter;

public class NotificationScheduler
{
    private NotificatorFactory notificatorFactory;

    public NotificationScheduler(Activity activity,
                                 ISettingsProvider settingsProvider,
                                 IResourceProvider resourceProvider,
                                 ISettingsWriter settingsWriter) {

        notificatorFactory = new NotificatorFactory(activity, settingsProvider, resourceProvider, settingsWriter);
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
