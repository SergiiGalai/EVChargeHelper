package com.chebuso.chargetimer.repositories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDoneException;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.chebuso.chargetimer.UserMessage;
import com.chebuso.chargetimer.helpers.StringHelper;
import com.chebuso.chargetimer.helpers.TimeHelper;
import com.chebuso.chargetimer.models.CalendarEntity;
import com.chebuso.chargetimer.models.CalendarEventEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("MissingPermission")
public class CalendarRepository implements ICalendarRepository {
    private static final String TAG = "CalendarRepository";
    private static final int MS_IN_1_HOUR = 60 * 60 * 1000;

    private final Activity activity;
    private final CalendarEntityReader entityReader;

    public CalendarRepository(Activity activity) {
        this.activity = activity;
        entityReader = new CalendarEntityReader();
    }

    public long createEvent(long calendarId, CalendarEventEntity event){
        Log.d(TAG, "createEvent. calendarId=" + calendarId);
        ContentResolver cr = activity.getContentResolver();

        try {
            ContentValues values = createCalendarEventContent(calendarId, event);
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            String lastSegment = uri == null ? "" : uri.getLastPathSegment();
            String eventId = StringHelper.emptyIfNull(lastSegment);

            return Long.parseLong(eventId);
        }catch(SQLiteDoneException ex) {
            UserMessage.showToast(activity, ex.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, ex.getMessage());
            return -1;
        }
    }

    @NonNull
    private ContentValues createCalendarEventContent(long calendarId, CalendarEventEntity event) {
        long eventTime = TimeHelper.now() + event.millisToStart;

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.TITLE, event.title);
        values.put(CalendarContract.Events.DESCRIPTION, event.description);
        values.put(CalendarContract.Events.DTSTART, eventTime);
        values.put(CalendarContract.Events.DTEND, eventTime + MS_IN_1_HOUR);
        values.put(CalendarContract.Events.EVENT_TIMEZONE,
                Calendar.getInstance().getTimeZone().getID());

        return values;
    }

    private boolean customColorsSupported(){
        Log.d(TAG, "customColorsSupported");
        Cursor cursor = activity
                .getContentResolver()
                .query(CalendarContract.Colors.CONTENT_URI,
                        new String[]{
                                CalendarContract.Colors.COLOR_KEY,
                                CalendarContract.Colors.COLOR
                        },
                        null, null, null);
        if (cursor == null)
            return false;

        try {
            boolean result = cursor.moveToFirst();
            Log.d(TAG, "customColorsSupported. result=" + result);
            return result;
        }
        finally {
            cursor.close();
        }
    }


    public void setReminder(long eventId, int minutesBefore) {
        Log.d(TAG, "setReminder. eventId=" + eventId);

        ContentResolver cr = activity.getContentResolver();
        ContentValues reminderValues = createReminderValues(eventId, minutesBefore);

        try {
            cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

            try (Cursor cursor = CalendarContract.Reminders.query(cr, eventId,
                    new String[]{CalendarContract.Reminders.MINUTES})) {
                if (cursor.moveToFirst()) {
                    int minutes = cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES));
                    Log.i(TAG, "calendar " + minutes);
                }
            }
        }catch(SQLiteDoneException ex) {
            UserMessage.showToast(activity, ex.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, ex.getMessage());
        }
    }

    @NonNull
    private ContentValues createReminderValues(long eventID, int minutesBefore) {
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Reminders.MINUTES, minutesBefore);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_DEFAULT);

        return values;
    }

    @Nullable
    public CalendarEntity getPrimaryCalendar(){
        Log.d(TAG, "getPrimaryCalendar");
        Cursor cur = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        null, null, null, null);

        if (cur != null)
        {
            List<CalendarEntity> calendars = new ArrayList<>();

            try {
                while (cur.moveToNext()){
                    CalendarEntity calendar = entityReader.fromCursorPosition(cur);
                    if (calendar.isPrimary)
                    {
                        Log.d(TAG, "getPrimaryCalendar. Found id=" + calendar.id);
                        return calendar;
                    }
                    calendars.add(calendar);
                }
            }
            finally {
                cur.close();
            }

            Log.d(TAG, "getPrimaryCalendar. Circle through the calendar list");
            for (CalendarEntity calendar : calendars ) {
                if (calendar.isPrimaryAlternative())
                {
                    Log.d(TAG, "getPrimaryCalendar. Found id=" + calendar.id);
                    return calendar;
                }
            }
        }

        return null;
    }

    @NonNull
    public List<CalendarEntity> getAvailableCalendars(){
        Log.d(TAG, "getAvailableCalendars");

        Cursor cur = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        null, null, null, null);

        List<CalendarEntity> result = new ArrayList<>();

        if (cur == null)
            return result;

        try{
            while (cur.moveToNext()){
                CalendarEntity calendar = entityReader.fromCursorPosition(cur);
                result.add(calendar);
            }

            return result;
        }
        finally {
            cur.close();
        }
    }

    public int deleteCalendar(String calendarName){
        Log.d(TAG, "deleteCalendar " + calendarName);

        int id = getCalendarId(calendarName);
        if (id == -1)
            return -1;

        Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        Uri deleteUri = ContentUris.withAppendedId(calUri, id);

        final ContentResolver contentResolver = activity.getContentResolver();
        return contentResolver.delete(deleteUri, null, null);
    }

    public int createCalendar(CalendarEntity calendar, String calendarColor){
        try {
            Log.d(TAG, "createCalendar " + calendar.displayName);

            // don't create if it already exists
            int id = getCalendarId(calendar.displayName);
            if (id != -1)
                return id;

            // doesn't exist yet, so create
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;
            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, calendar.accountName);
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            cv.put(CalendarContract.Calendars.NAME, calendar.displayName);
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendar.displayName);
            if (calendarColor != null) {
                int colorInt = Color.parseColor(calendarColor);
                cv.put(CalendarContract.Calendars.CALENDAR_COLOR, colorInt);
            }
            cv.put(CalendarContract.Calendars.VISIBLE, 1);
            cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, calendar.accountName );
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 0);

            calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, calendar.accountName)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();

            final ContentResolver contentResolver = activity.getContentResolver();
            Uri created = contentResolver.insert(calUri, cv);
            if (created != null) {
                String lastSegment = created.getLastPathSegment();
                if (lastSegment != null)
                    return Integer.valueOf(lastSegment);
            }
        } catch (Exception e) {
            Log.e(TAG, "Creating calendar failed.", e);
        }
        return -1;
    }

    private int getCalendarId(String calendarName){
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        final ContentResolver contentResolver = activity.getContentResolver();
        Cursor cursor = contentResolver.query(uri, new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        }, null, null, null);

        if (cursor != null) {
            try{
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    String displayName = cursor.getString(2);
                    if (
                            (name != null && name.equals(calendarName)) ||
                                    (displayName != null && displayName.equals(calendarName))
                            )
                        return (int)id;
                }
            }
            finally {
                cursor.close();
            }
        }
        return -1;
    }
}
