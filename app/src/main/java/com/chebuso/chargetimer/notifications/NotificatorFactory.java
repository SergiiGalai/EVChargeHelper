package com.chebuso.chargetimer.notifications;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.chebuso.chargetimer.settings.ISettingsReader;
import com.chebuso.chargetimer.settings.ISettingsWriter;

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
        if(calendarPermissionsGranted && settingsProvider.calendarAdvancedNotificationsAllowed()){
            return createAdvancedNotificator();
        }else{
            if (settingsProvider.calendarBasicNotificationsAllowed()){
                return createBasicNotificator();
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
        if (settingsProvider.calendarAdvancedNotificationsAllowed()){
            r.add(createAdvancedNotificator());
        }else{
            if (settingsProvider.calendarBasicNotificationsAllowed()){
                r.add(createBasicNotificator());
            }
        }
        return r;
    }

    @NonNull
    private INotificator createAdvancedNotificator() {
        return new CalendarAdvancedNotificator(
                createBasicNotificator(),
                createCalendarRepository(),
                settingsProvider,
                settingsWriter,
                activity);
    }

    @NonNull
    private CalendarRepository createCalendarRepository() {
        return new CalendarRepository(activity);
    }

    @NonNull
    private CalendarDefaultNotificator createBasicNotificator() {
        return new CalendarDefaultNotificator(activity);
    }
}
