package com.example.dell.chargehelper.notifications;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.example.dell.chargehelper.R;
import com.example.dell.chargehelper.SettingsProvider;
import com.example.dell.chargehelper.helpers.TimeHelper;

import java.util.Calendar;

public class CarChargedDirectCalendarWriteScheduler implements ICarChargedNotificationScheduler
{
    private static final int EventColor = GoogleCalendarEventColor.VIOLET;
    private static final int MS_ONE_HOUR = 60 * 60 * 1000;


    private CalendarRepository repository;
    private Activity context;
    private SettingsProvider settingsProvider;
    public static final int REQUEST_CALENDAR = 1;
    private static String[] PERMISSIONS_CALENDAR = {Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};

    CarChargedDirectCalendarWriteScheduler(Activity context) {
        this.context = context;
        repository = new CalendarRepository(context);
        settingsProvider = new SettingsProvider(context);
    }

    @Override
    public void scheduleNotification(long eventTime) {
        long epochMs = TimeHelper.addToNow(eventTime);
        scheduleCalendarEvent(context.getString(R.string.car_charged_title),
                context.getString(R.string.car_charged_descr),
                epochMs);
    }

    private void scheduleCalendarEvent(String title, String description, long eventTime) {
        if (calendarPermissionsGranted())
        {
            //repository.showColors();
            ContentValues values = createCalendarEventContent(title, description, eventTime);
            long eventId = repository.createEvent(values);
            int reminderMinutes = settingsProvider.getCalendarReminderMinutes();
            repository.setReminder(eventId, reminderMinutes);

            notifyUser("Calendar event created", eventId);
        }
        else
        {
            requestCalendarPermission();
        }
    }

    private void notifyUser(String description, final long eventId) {
        Snackbar.make(context.findViewById(android.R.id.content), description, Snackbar.LENGTH_LONG)
                .setAction(R.string.open, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                                .appendPath(Long.toString(eventId));

                        Intent intent = new Intent(Intent.ACTION_VIEW)
                                .setData(uri.build());

                        context.startActivity(intent);
                    }
                })
                .show();
    }

    @NonNull
    private ContentValues createCalendarEventContent(String title, String description, long eventTime) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, eventTime);
        values.put(CalendarContract.Events.DTEND, eventTime + MS_ONE_HOUR);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, repository.getPrimaryCalendarId());
        values.put(CalendarContract.Events.EVENT_COLOR_KEY, EventColor);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                Calendar.getInstance().getTimeZone().getID());
        return values;
    }

    private void requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                Manifest.permission.WRITE_CALENDAR)){
            Snackbar.make(context.findViewById(android.R.id.content), R.string.calendar_permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(context, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(context, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
        }
    }

    private boolean calendarPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ;
    }
}
