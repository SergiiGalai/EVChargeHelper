package com.chebuso.chargetimer.notifications;

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
import android.util.Log;
import android.widget.Toast;

import com.chebuso.chargetimer.UserMessage;

@SuppressLint("MissingPermission")
public class CalendarRepository implements ICalendarRepository {
    private static final String TAG = "CalendarRepository";
    private Activity activity;

    public CalendarRepository(Activity activity) {
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

    public int getPrimaryCalendarId(){
        long calendarId;
        String calendarName;

        Cursor cur = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        null, null, null, null);

        if (cur != null)
        {
            try {
                while (cur.moveToNext()){
                    int idIndex = cur.getColumnIndex(CalendarContract.Calendars._ID);
                    int accountNameIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME);
                    int ownerAccountIndex = cur.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT);
                    int displayViewIndex = cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
                    int visibleIndex = cur.getColumnIndex(CalendarContract.Calendars.VISIBLE);
                    int isPrimaryIndex = cur.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY);
                    if (isPrimaryIndex == -1) {
                        isPrimaryIndex = cur.getColumnIndex("COALESCE(isPrimary, ownerAccount = account_name)");
                    }

                    calendarId = cur.getLong(idIndex);
                    String accName = cur.getString(accountNameIndex);
                    String owner = cur.getString(ownerAccountIndex);
                    calendarName = cur.getString(displayViewIndex);
                    String visible = cur.getString(visibleIndex);
                    Integer isPrimary = isPrimaryIndex== -1 ? null : cur.getInt(isPrimaryIndex);

                    if (
                            (isPrimary != null && isPrimary == 1) ||
                            (isPrimary == null && accName.equals(owner)))
                        return (int)calendarId;
                }
            }
            finally {
                cur.close();
            }
        }

        return -1;
    }

    public String getAvailableCalendars(){
        StringBuilder sb = new StringBuilder();
        Cursor cur = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        null, null, null, null);

        if (cur == null)
            return sb.toString();

        int isPrimaryIndex;

        try{
            while (cur.moveToNext()){
                int idIndex = cur.getColumnIndex(CalendarContract.Calendars._ID);
                int accountNameIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME);
                int ownerAccountIndex = cur.getColumnIndex(CalendarContract.Calendars.OWNER_ACCOUNT);
                int accountTypeIndex = cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE);
                int displayViewIndex = cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
                int visibleIndex = cur.getColumnIndex(CalendarContract.Calendars.VISIBLE);
                isPrimaryIndex = cur.getColumnIndex(CalendarContract.Calendars.IS_PRIMARY);
                if (isPrimaryIndex == -1) {
                    isPrimaryIndex = cur.getColumnIndex("COALESCE(isPrimary, ownerAccount = account_name)");
                }

                long id = cur.getLong(idIndex);
                String accName = cur.getString(accountNameIndex);
                String owner = cur.getString(ownerAccountIndex);
                String accType = cur.getString(accountTypeIndex);
                String name = cur.getString(displayViewIndex);
                String visible = cur.getString(visibleIndex);
                Integer isPrimary = isPrimaryIndex== -1 ? null : cur.getInt(isPrimaryIndex);

                sb.append(String.format("%d:type=%s, acc=%s, owner=%s, name=%s, prim=%s, vis=%s;  ",
                        id, accType, accName, owner, name, isPrimary, visible));
            }
            return sb.toString();
        }
        finally {
            cur.close();
        }
    }

    public int deleteCalendar(String calendarName){
        int id = getCalendarId(calendarName);
        if (id == -1)
            return -1;

        Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        Uri deleteUri = ContentUris.withAppendedId(calUri, id);

        final ContentResolver contentResolver = activity.getContentResolver();
        return contentResolver.delete(deleteUri, null, null);
    }

    public int createCalendar(String calendarName, String calendarColor, String accountName){
        try {
            // don't create if it already exists
            int id = getCalendarId(calendarName);
            if (id != -1)
                return id;

            // doesn't exist yet, so create
            Uri calUri = CalendarContract.Calendars.CONTENT_URI;
            ContentValues cv = new ContentValues();
            cv.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
            cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            cv.put(CalendarContract.Calendars.NAME, calendarName);
            cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, calendarName);
            if (calendarColor != null) {
                int colorInt = Color.parseColor(calendarColor);
                cv.put(CalendarContract.Calendars.CALENDAR_COLOR, colorInt);
            }
            cv.put(CalendarContract.Calendars.VISIBLE, 1);
            cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
            cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName );
            cv.put(CalendarContract.Calendars.SYNC_EVENTS, 0);

            calUri = calUri.buildUpon()
                    .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                    .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                    .build();

            final ContentResolver contentResolver = activity.getContentResolver();
            Uri created = contentResolver.insert(calUri, cv);
            if (created != null) {
                return Integer.valueOf(created.getLastPathSegment());
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
                            ) {
                        return (int)id;
                    }
                }
            }
            finally {
                cursor.close();
            }
        }
        return -1;
    }
}
