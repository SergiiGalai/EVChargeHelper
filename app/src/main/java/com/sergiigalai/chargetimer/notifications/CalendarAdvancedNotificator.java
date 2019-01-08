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

public class CalendarAdvancedNotificator implements INotificator
{
    public static final int REQUEST_CALENDAR = 1;
    private static final String CALENDAR_NAME = "com.sergiigalai.chargetimer";
    private static final String CALENDAR_COLOR = "purple";

    private static final int MS_IN_1_HOUR = 60 * 60 * 1000;
    private static final String[] PERMISSIONS_CALENDAR = {
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
    };

    private final ICalendarRepository calendarRepository;
    private final Activity activity;
    private final ISettingsReader settingsProvider;
    private final ISettingsWriter settingsWriter;
    private final INotificator fallbackNotificator;

    CalendarAdvancedNotificator(
            INotificator fallbackNotificator,
            ICalendarRepository calendarRepository,
            ISettingsReader settingsProvider,
            ISettingsWriter settingsWriter,
            Activity activity) {
        this.fallbackNotificator = fallbackNotificator;
        this.activity = activity;
        this.settingsProvider = settingsProvider;
        this.settingsWriter = settingsWriter;
        this.calendarRepository = calendarRepository;
    }

    @Override
    public void scheduleCarChargedNotification(long millisToEvent) {
        if (calendarPermissionsGranted()) {
            int calendarId = calendarRepository.createCalendar(CALENDAR_NAME, CALENDAR_COLOR);
            if (calendarId == -1){
                calendarId = calendarRepository.getPrimaryCalendarId();
            }

            scheduleCalendarEvent(calendarId,
                    activity.getString(R.string.car_charged_title),
                    activity.getString(R.string.car_charged_descr),
                    millisToEvent);
        } else if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            requestCalendarPermission();
        }
    }

    private boolean calendarPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ;
    }

    private void scheduleCalendarEvent(int calendarId, String title, String description, long millisToEvent) {
        if (calendarId == -1) {
            UserMessage.showToast(activity, R.string.error_no_primary_calendar, Toast.LENGTH_LONG);
            fallbackNotificator.scheduleCarChargedNotification(millisToEvent);
        }else{
            ContentValues eventData = createCalendarEventContent(calendarId, title, description, millisToEvent);
            int reminderMinutes = settingsProvider.getCalendarReminderMinutes();

            long eventId = createEventWithReminder(eventData, reminderMinutes);
            notifyUser_open_event(eventId);
        }
    }

    private long createEventWithReminder(ContentValues eventData, int reminderMinutes){
        long eventId = calendarRepository.createEvent(eventData);
        calendarRepository.setReminder(eventId, reminderMinutes);
        return eventId;
    }

    private void notifyUser_open_event(final long eventId){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(eventId));
        intent.setData(uri.build());
        activity.startActivity(intent);
    }

    @NonNull
    private ContentValues createCalendarEventContent(int calendarId, String title, String description, long millisToEvent) {
        long eventTime = TimeHelper.now() + millisToEvent;

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, eventTime);
        values.put(CalendarContract.Events.DTEND, eventTime + MS_IN_1_HOUR);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                Calendar.getInstance().getTimeZone().getID());

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
                        settingsWriter.saveCalendarAdvancedNotificationsAllowed(false);
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
