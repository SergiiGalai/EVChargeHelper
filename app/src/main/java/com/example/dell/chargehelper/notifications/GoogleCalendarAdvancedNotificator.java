package com.example.dell.chargehelper.notifications;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.example.dell.chargehelper.settings.ISettingsProvider;
import com.example.dell.chargehelper.R;
import com.example.dell.chargehelper.helpers.TimeHelper;
import com.example.dell.chargehelper.settings.ISettingsWriter;

import java.util.Calendar;

public class GoogleCalendarAdvancedNotificator implements INotificator
{
    private static final int EventColor = GoogleCalendarEventColor.VIOLET;
    private static final int MS_IN_1_HOUR = 60 * 60 * 1000;

    private final GoogleCalendarRepository repository;
    private final Activity activity;
    private final ISettingsProvider settingsProvider;
    public static final int REQUEST_CALENDAR = 1;
    private static final String[] PERMISSIONS_CALENDAR = {Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR};
    private final ISettingsWriter settingsWriter;

    GoogleCalendarAdvancedNotificator(ISettingsProvider settingsProvider,
                                      ISettingsWriter settingsWriter,
                                      Activity activity) {
        this.activity = activity;
        this.settingsProvider = settingsProvider;
        this.settingsWriter = settingsWriter;
        repository = new GoogleCalendarRepository(activity);
    }

    @Override
    public void scheduleCarChargedNotification(long millisToEvent) {
        if (calendarPermissionsGranted())
        {
            long epochMs = TimeHelper.addToNow(millisToEvent);
            scheduleCalendarEvent(activity.getString(R.string.car_charged_title),
                    activity.getString(R.string.car_charged_descr),
                    epochMs);
        } else if (settingsProvider.googleAdvancedNotificationsAllowed())
        {
            requestCalendarPermission();
        }
    }

    private boolean calendarPermissionsGranted() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED ;
    }

    private void scheduleCalendarEvent(String title, String description, long eventTime) {
        //repository.showColors();
        ContentValues values = createCalendarEventContent(title, description, eventTime);
        long eventId = repository.createEvent(values);
        int reminderMinutes = settingsProvider.getCalendarReminderMinutes();
        repository.setReminder(eventId, reminderMinutes);

        notifyUser(activity.getString(R.string.event_created), eventId);
    }

    private void notifyUser(String description, final long eventId) {
        Snackbar.make(activity.findViewById(android.R.id.content), description, Snackbar.LENGTH_LONG)
            .setAction(R.string.open, new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    Uri.Builder uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                        .appendPath(Long.toString(eventId));

                    Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(uri.build());

                    activity.startActivity(intent);
                }
            })
            .show();
    }

    @NonNull
    private ContentValues createCalendarEventContent(String title, String description, long eventTime) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, eventTime);
        values.put(CalendarContract.Events.DTEND, eventTime + MS_IN_1_HOUR);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, repository.getPrimaryCalendarId());
        values.put(CalendarContract.Events.EVENT_COLOR_KEY, EventColor);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                Calendar.getInstance().getTimeZone().getID());
        return values;
    }

    private void requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.WRITE_CALENDAR)){
            //showRationaleSnackBar();
            showRationaleDialog();
        } else {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
        }
    }

    private void showRationaleSnackBar() {
        Snackbar.make(activity.findViewById(android.R.id.content), R.string.calendar_permission_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(activity, PERMISSIONS_CALENDAR, REQUEST_CALENDAR);
                    }
                })
                .show();
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
