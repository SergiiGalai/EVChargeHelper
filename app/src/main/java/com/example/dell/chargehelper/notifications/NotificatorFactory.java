package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.example.dell.chargehelper.settings.ISettingsProvider;

import java.util.ArrayList;
import java.util.List;

public class NotificatorFactory {

    private final ISettingsProvider settingsProvider;

    public NotificatorFactory(ISettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    public INotificator tryCreate(boolean calendarPermissionsGranted, Activity activity){
        if(calendarPermissionsGranted && settingsProvider.googleAdvancedNotificationsAllowed()){
            return new GoogleCalendarAdvancedNotificator(settingsProvider, activity);
        }else{
            if (settingsProvider.googleBasicNotificationsAllowed()){
                return new GoogleCalendarDefaultNotificator(activity);
            }
        }
        return null;
    }

    @NonNull
    public List<INotificator> createNotificators(Activity activity) {
        ArrayList<INotificator> r = new ArrayList<>();

        if (settingsProvider.applicationNotificationsAllowed()){
            r.add(new ApplicationNotificator(settingsProvider, activity));
        }
        if (settingsProvider.googleAdvancedNotificationsAllowed()){
            r.add(new GoogleCalendarAdvancedNotificator(settingsProvider, activity));
        }else{
            if (settingsProvider.googleBasicNotificationsAllowed()){
                r.add(new GoogleCalendarDefaultNotificator(activity));
            }
        }
        return r;
    } 
}
