package com.chebuso.chargetimer.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.chebuso.chargetimer.UserMessage;
import com.chebuso.chargetimer.helpers.PermissionHelper;
import com.chebuso.chargetimer.models.CalendarEntity;
import com.chebuso.chargetimer.models.CalendarEventEntity;
import com.chebuso.chargetimer.repositories.ICalendarRepository;
import com.chebuso.chargetimer.settings.ISettingsReader;
import com.chebuso.chargetimer.R;
import com.chebuso.chargetimer.helpers.TimeHelper;
import com.chebuso.chargetimer.settings.ISettingsWriter;

import java.util.Calendar;

public class CalendarAdvancedNotificator implements INotificator
{
    public static final int REQUEST_CALENDAR = 1;

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
        if (PermissionHelper.isFullCalendarPermissionsGranted(activity)) {
//            String calendarName = activity.getString(R.string.calendar_name);
//            String calendarColor = activity.getString(R.string.calendar_color);
//            String accountName = activity.getString(R.string.calendar_account_name);

            CalendarEntity calendar = calendarRepository.getPrimaryCalendar();
            CalendarEventEntity event = new CalendarEventEntity(
                    activity.getString(R.string.car_charged_title),
                    activity.getString(R.string.car_charged_descr),
                    millisToEvent);

            scheduleCalendarEvent(calendar, event);
        } else if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            requestCalendarPermission();
        }
    }

    private void scheduleCalendarEvent(CalendarEntity calendar, @NonNull CalendarEventEntity event) {
        if (calendar == null) {
            UserMessage.showToast(activity, R.string.error_no_primary_calendar, Toast.LENGTH_LONG);
            fallbackNotificator.scheduleCarChargedNotification(event.millisToStart);
        }else{
            ContentValues eventData = createCalendarEventContent(calendar.id, event);
            long eventId = calendarRepository.createEvent(eventData);

            int reminderMinutes = settingsProvider.getCalendarReminderMinutes();
            calendarRepository.setReminder(eventId, reminderMinutes);

            openEventActivity(eventId);
        }
    }

    private void openEventActivity(final long eventId){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(eventId));
        intent.setData(uri.build());
        activity.startActivity(intent);
    }

    @NonNull
    private ContentValues createCalendarEventContent(long calendarId, @NonNull CalendarEventEntity event) {
        long eventTime = TimeHelper.now() + event.millisToStart;

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, eventTime);
        values.put(CalendarContract.Events.DTEND, eventTime + MS_IN_1_HOUR);
        values.put(CalendarContract.Events.TITLE, event.title);
        values.put(CalendarContract.Events.DESCRIPTION, event.description);
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
