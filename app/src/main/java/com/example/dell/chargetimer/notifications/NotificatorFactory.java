package com.example.dell.chargetimer.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.dell.chargetimer.settings.ISettingsReader;
import com.example.dell.chargetimer.settings.ISettingsWriter;

import java.util.ArrayList;
import java.util.List;

public class NotificatorFactory {

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

    public INotificator tryCreate(boolean calendarPermissionsGranted){
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
    public List<INotificator> createNotificators() {
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
