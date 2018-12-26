package com.sergiigalai.chargetimer.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.sergiigalai.chargetimer.settings.ISettingsReader;
import com.sergiigalai.chargetimer.settings.ISettingsWriter;

import java.util.ArrayList;
import java.util.List;

class NotificatorFactory {

    private final ISettingsReader settingsProvider;
    private final Activity activity;
    private final IResourceProvider resourceProvider;
    private ISettingsWriter settingsWriter;

    NotificatorFactory(Activity activity,
                       ISettingsReader settingsProvider,
                       IResourceProvider resourceProvider,
                       ISettingsWriter settingsWriter) {
        this.settingsProvider = settingsProvider;
        this.resourceProvider = resourceProvider;
        this.settingsWriter = settingsWriter;
        this.activity = activity;
    }

    INotificator tryCreate(boolean calendarPermissionsGranted){
        if(calendarPermissionsGranted && settingsProvider.googleAdvancedNotificationsAllowed()){
            return new GoogleCalendarAdvancedNotificator(settingsProvider, settingsWriter, activity);
        }else{
            if (settingsProvider.googleBasicNotificationsAllowed()){
                return new GoogleCalendarDefaultNotificator(activity);
            }
        }
        return null;
    }

    @NonNull
    List<INotificator> createNotificators() {
        ArrayList<INotificator> r = new ArrayList<>();

        if (settingsProvider.applicationNotificationsAllowed()){
            r.add(new ApplicationNotificator(settingsProvider, resourceProvider, activity));
        }
        if (settingsProvider.googleAdvancedNotificationsAllowed()){
            r.add(new GoogleCalendarAdvancedNotificator(settingsProvider, settingsWriter, activity));
        }else{
            if (settingsProvider.googleBasicNotificationsAllowed()){
                r.add(new GoogleCalendarDefaultNotificator(activity));
            }
        }
        return r;
    } 
}
