package com.chebuso.chargetimer.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDoneException;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.chebuso.chargetimer.UserMessage;

@SuppressLint("MissingPermission")
class CalendarRepository implements ICalendarRepository {
    private static final String TAG = "CalendarRepository";
    private Activity activity;

    CalendarRepository(Activity activity) {
        this.activity = activity;
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
        long calendarId = -1;

        final int projection_id = 0;
        final int projection_name = 1;
        final int projection_primary = 2;

        Cursor cursor = activity
                .getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        new String[]{
                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                                CalendarContract.Calendars.IS_PRIMARY,
                        },
                        null, null, null);

        if (cursor != null)
        {
            try {
                while (cursor.moveToNext()){
                    calendarId = cursor.getLong(projection_id);
                    String name = cursor.getString(projection_name);
                    String primary = cursor.getString(projection_primary);
                    if ("1".equals(primary))
                        return (int)calendarId;
                }
            }
            finally {
                cursor.close();
            }
        }

        return (int)calendarId;
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

}
