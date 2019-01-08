package com.sergiigalai.chargetimer.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDoneException;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.sergiigalai.chargetimer.UserMessage;

@SuppressLint("MissingPermission")
class CalendarRepository implements ICalendarRepository {
    private static final String TAG = "CalendarRepository";
    private Activity activity;

    CalendarRepository(Activity activity) {
        this.activity = activity;
    }

    public long createEvent(ContentValues values){
        ContentResolver cr = activity.getContentResolver();

        try {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            String lastSegment = uri == null ? "" : uri.getLastPathSegment();
            String event = lastSegment == null ? "" : lastSegment;
            return Long.parseLong(event);
        }catch(SQLiteDoneException ex) {
            UserMessage.showToast(activity, ex.getMessage(), Toast.LENGTH_LONG);
            Log.e(TAG, ex.getMessage());
            return 0;
        }
    }

    public void setReminder(long eventID, int minutesBefore) {
        ContentResolver cr = activity.getContentResolver();
        ContentValues reminderValues = createReminderValues(eventID, minutesBefore);

        try {
            cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

            try (Cursor cursor = CalendarContract.Reminders.query(cr, eventID,
                    new String[]{CalendarContract.Reminders.MINUTES})) {
                if (cursor.moveToFirst()) {
                    System.out.println("calendar"
                            + cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)));
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

    public boolean customColorsSupported(){
        Cursor cursor = activity
                .getContentResolver()
                .query(CalendarContract.Colors.CONTENT_URI,
                        new String[]{
                                CalendarContract.Colors._ID,
                                CalendarContract.Colors.COLOR_KEY,
                                CalendarContract.Colors.COLOR
                        },
                        null, null, null);
        if (cursor == null)
            return false;
        try {
            return cursor.moveToFirst();
        }
        finally {
            cursor.close();
        }
    }

    public void showColors(){
        Cursor cur = activity
                .getContentResolver()
                .query(CalendarContract.Colors.CONTENT_URI,
                        new String[]{
                                CalendarContract.Colors._ID,
                                CalendarContract.Colors.COLOR_KEY,
                                CalendarContract.Colors.COLOR,
                        },
                        null, null, null);

        if (cur == null)
            return;

        try{
            while (cur.moveToNext()){
                long colId = cur.getLong(0);
                long colorKey = cur.getLong(1);
                long color = cur.getLong(2);

                String hexColor= Long.toHexString(color);

                Log.d( TAG, "id=" + colId
                        + "; key=" + colorKey
                        + "; hexColor="+ hexColor);
            }
        }
        finally {
            cur.close();
        }
    }


    public int getPrimaryCalendarId(){
        long calendarId = -1;

        Cursor cursor = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        new String[]{
                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                        },
                        CalendarContract.Calendars.IS_PRIMARY + "=1", null, null);

        if (cursor != null)
        {
            try {
                if (cursor.moveToNext()){
                    calendarId = cursor.getLong(0);
                }
            }
            finally {
                cursor.close();
            }
        }

        return (int)calendarId;
    }

    public String getAvailableCalendars(){
        StringBuilder sb = new StringBuilder();
        Cursor cur = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        new String[]{
                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.ACCOUNT_NAME,
                                CalendarContract.Calendars.OWNER_ACCOUNT,
                                CalendarContract.Calendars.ACCOUNT_TYPE,
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                                CalendarContract.Calendars.VISIBLE,
                                CalendarContract.Calendars.IS_PRIMARY,
                        },
                        null, null, null);

        if (cur == null)
            return sb.toString();

        int primaryColumnIndex;

        try{
            while (cur.moveToNext()){
                long id = cur.getLong(0);
                String accName = cur.getString(1);
                String owner = cur.getString(2);
                String accType = cur.getString(3);
                String name = cur.getString(4);
                String visible = cur.getString(5);

                primaryColumnIndex = cur.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY);
                if (primaryColumnIndex == -1) {
                    primaryColumnIndex = cur.getColumnIndex("COALESCE(isPrimary, ownerAccount = account_name)");
                }
                String isPrimary = cur.getString(primaryColumnIndex);

                sb.append(String.format("%d:type=%s, acc=%s, owner=%s, name=%s, prim=%s, vis=%s;  ",
                        id, accType, accName, owner, name, isPrimary, visible));
            }
            return sb.toString();
        }
        finally {
            cur.close();
        }
    }

    public String createCalendar(String calendarName, String calendarColor){
        final String CALENDAR_ACCOUNT_NAME = "com.sergiigalai.chargetimer";

        try {
            // don't create if it already exists
            Uri evuri = CalendarContract.Calendars.CONTENT_URI;
            final ContentResolver contentResolver = activity.getContentResolver();
            Cursor result = contentResolver.query(evuri, new String[]{
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.NAME,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            }, null, null, null);
            if (result != null) {
                while (result.moveToNext()) {
                    if ((result.getString(1) != null &&
                            result.getString(1).equals(calendarName)) ||
                            (result.getString(2) != null && result.getString(2).equals(calendarName))) {
                        result.close();
                        return null;
                    }
                }
                result.close();
            }

            // doesn't exist yet, so create
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;
            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME);
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            cv.put(CalendarContract.Calendars.NAME, calendarName);
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarName);
            if (calendarColor != null) {
                cv.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor(calendarColor));
            }
            cv.put(CalendarContract.Calendars.VISIBLE, 1);
            cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDAR_ACCOUNT_NAME );
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 0);

            calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_ACCOUNT_NAME)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();

            Uri created = contentResolver.insert(calUri, cv);
            if (created != null) {
                return created.getLastPathSegment();
            }
        } catch (Exception e) {
            Log.e(TAG, "Creating calendar failed.", e);
        }
        return null;

    }
}
