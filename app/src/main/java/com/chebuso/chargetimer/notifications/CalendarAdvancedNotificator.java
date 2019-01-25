package com.chebuso.chargetimer.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.chebuso.chargetimer.UserMessage;
import com.chebuso.chargetimer.helpers.PermissionHelper;
import com.chebuso.chargetimer.models.CalendarEntity;
import com.chebuso.chargetimer.models.CalendarEventEntity;
import com.chebuso.chargetimer.repositories.ICalendarRepository;
import com.chebuso.chargetimer.settings.ISettingsReader;
import com.chebuso.chargetimer.R;
import com.chebuso.chargetimer.settings.ISettingsWriter;


public class CalendarAdvancedNotificator implements INotificator
{
    public static final int REQUEST_CALENDAR = 1;
    private static final String TAG = "CalAdvancedNotificator";

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
            Log.d(TAG, "Full calendar permissions granted");

            CalendarEntity calendar = calendarRepository.getPrimaryCalendar();
            CalendarEventEntity event = new CalendarEventEntity(
                    activity.getString(R.string.car_charged_title),
                    activity.getString(R.string.car_charged_descr),
                    millisToEvent);

            scheduleCalendarEvent(event, calendar);
        } else if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            Log.d(TAG, "Calendar permissions not granted");
            requestCalendarPermission();
        }
    }

    private void scheduleCalendarEvent(CalendarEventEntity event, @Nullable CalendarEntity calendar) {
        if (calendar == null) {
            disableAdvancedNotification();
            scheduleEventUsingDefaultNotificator(event.millisToStart, R.string.error_no_primary_calendar);
        }else{
            long eventId = calendarRepository.createEvent(calendar.id, event);
            if (eventId == -1)
            {
                scheduleEventUsingDefaultNotificator(event.millisToStart, R.string.error_creating_calendar_event);
                return;
            }

            int reminderMinutes = settingsProvider.getCalendarReminderMinutes();
            calendarRepository.setReminder(eventId, reminderMinutes);
            openEventActivity(eventId);
        }
    }

    private void disableAdvancedNotification() {
        settingsWriter.saveCalendarAdvancedNotificationsAllowed(false);
    }

    private void scheduleEventUsingDefaultNotificator(long millisToEvent, @StringRes int messageId) {
        UserMessage.showToast(activity, messageId, Toast.LENGTH_LONG);
        fallbackNotificator.scheduleCarChargedNotification(millisToEvent);
    }

    private void openEventActivity(final long eventId){
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendPath(Long.toString(eventId));
        intent.setData(uri.build());
        activity.startActivity(intent);
    }

    private void requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_CALENDAR)){
            Log.d(TAG, "Show permissions rationale");
            showRationaleDialog();
        } else {
            Log.d(TAG, "Request calendar permissions");
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
                        disableAdvancedNotification();
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
