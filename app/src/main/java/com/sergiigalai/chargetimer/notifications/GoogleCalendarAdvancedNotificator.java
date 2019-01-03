package com.sergiigalai.chargetimer.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.sergiigalai.chargetimer.UserMessage;
import com.sergiigalai.chargetimer.settings.ISettingsReader;
import com.sergiigalai.chargetimer.R;
import com.sergiigalai.chargetimer.helpers.TimeHelper;
import com.sergiigalai.chargetimer.settings.ISettingsWriter;

import java.util.Calendar;

public class GoogleCalendarAdvancedNotificator implements INotificator
{
    private static final int EventColor = GoogleCalendarEventColor.VIOLET;
    private static final int MS_IN_1_HOUR = 60 * 60 * 1000;

    private final GoogleCalendarRepository repository;
    private final Activity activity;
    private final ISettingsReader settingsProvider;
    public static final int REQUEST_CALENDAR = 1;
    private static final String[] PERMISSIONS_CALENDAR = {Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};
    private final ISettingsWriter settingsWriter;
    private final INotificator fallbackNotificator;

    GoogleCalendarAdvancedNotificator(
            INotificator fallbackNotificator,
            ISettingsReader settingsProvider,
            ISettingsWriter settingsWriter,
            Activity activity) {
        this.fallbackNotificator = fallbackNotificator;
        this.activity = activity;
        this.settingsProvider = settingsProvider;
        this.settingsWriter = settingsWriter;
        repository = new GoogleCalendarRepository(activity);
    }

    @Override
    public void scheduleCarChargedNotification(long millisToEvent) {
        if (calendarPermissionsGranted())
        {
            scheduleCalendarEvent(activity.getString(R.string.car_charged_title),
                    activity.getString(R.string.car_charged_descr),
                    millisToEvent);
        } else if (settingsProvider.googleAdvancedNotificationsAllowed())
        {
            requestCalendarPermission();
        }
    }

    private boolean calendarPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ;
    }

    private void scheduleCalendarEvent(String title, String description, long millisToEvent) {
        //repository.showColors();

        int calendarId = repository.getPrimaryCalendarId();
        if (calendarId == -1) {
            UserMessage.showToast(activity, R.string.error_no_primary_calendar, Toast.LENGTH_LONG);
            fallbackNotificator.scheduleCarChargedNotification(millisToEvent);
        }else{
            long epochMs = TimeHelper.now() + millisToEvent;
            ContentValues values = createCalendarEventContent(calendarId, title, description, epochMs);

            long eventId = repository.createEvent(values);
            int reminderMinutes = settingsProvider.getCalendarReminderMinutes();

            repository.setReminder(eventId, reminderMinutes);

            notifyUser_open_event(eventId);
        }
    }

    private void notifyUser_open_event(final long eventId){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(eventId));
        intent.setData(uri.build());
        activity.startActivity(intent);
    }

    @NonNull
    private ContentValues createCalendarEventContent(int calendarId, String title, String description, long eventTime) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, eventTime);
        values.put(CalendarContract.Events.DTEND, eventTime + MS_IN_1_HOUR);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                Calendar.getInstance().getTimeZone().getID());

        if (repository.customColorsSupported())
            values.put(CalendarContract.Events.EVENT_COLOR_KEY, EventColor);

        return values;
    }

    private void requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_CALENDAR)){
            showRationaleDialog();
        } else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
        }
    }

    private void showRationaleDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.calendar_permission_rationale_title));
        alertDialog.setMessage(activity.getString(R.string.calendar_permission_rationale));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity.getString(R.string.permission_dialog_forbid),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        settingsWriter.saveGoogleAdvancedNotificationsAllowed(false);
                        //NotificationScheduler scheduler = new NotificationScheduler(activity);
                        //scheduler.schedule();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, activity.getString(R.string.permission_dialog_allow),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(activity, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
                    }
                });
        alertDialog.show();
    }
}
