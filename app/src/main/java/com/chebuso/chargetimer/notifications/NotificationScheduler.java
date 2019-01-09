package com.chebuso.chargetimer.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.chebuso.chargetimer.helpers.PermissionHelper;
import com.chebuso.chargetimer.settings.ISettingsReader;
import com.chebuso.chargetimer.settings.ISettingsWriter;

public class NotificationScheduler
{
    private NotificatorFactory notificatorFactory;

    public NotificationScheduler(Activity activity,
                                 ISettingsReader settingsProvider,
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
