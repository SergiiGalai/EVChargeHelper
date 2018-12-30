package com.sergiigalai.chargetimer.notifications;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.util.Log;

@SuppressLint("MissingPermission")
class GoogleCalendarRepository {
    private static final String TAG = "GoogleCalendarRepo";
    private Activity activity;

    GoogleCalendarRepository(Activity activity) {
        this.activity = activity;
    }

    long createEvent(ContentValues values){
        ContentResolver cr = activity.getContentResolver();

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        String lastSegment = uri == null ? "" : uri.getLastPathSegment();
        String event = lastSegment == null ? "" : lastSegment;

        return Long.parseLong(event);
    }

    void setReminder(long eventID, int minutesBefore) {
        ContentResolver cr = activity.getContentResolver();
        ContentValues values = createReminderValues(eventID, minutesBefore);

        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);

        try (Cursor c = CalendarContract.Reminders.query(cr, eventID,
                new String[]{CalendarContract.Reminders.MINUTES})) {
            if (c.moveToFirst()) {
                System.out.println("calendar"
                        + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            }
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

    int getPrimaryCalendarId(){
        long calId = 1;

        final int projection_id = 0;
        final int projection_name = 1;
        final int projection_primary = 2;

        Cursor cur = activity.getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        new String[]{
                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                                CalendarContract.Calendars.IS_PRIMARY,
                        },
                        null, null, null);

        if (cur != null)
        {
            try {
                while (cur.moveToNext()){
                    calId = cur.getLong(projection_id);
                    String name = cur.getString(projection_name);
                    String primary = cur.getString(projection_primary);
                    if (primary.equals("1"))
                        return (int)calId;
                }
            }
            finally {
                cur.close();
            }
        }

        return (int)calId;
    }


    public void showColors(){
        Cursor cur = activity.getContentResolver()
                .query(CalendarContract.Colors.CONTENT_URI,
                        new String[]{
                                CalendarContract.Colors._ID,
                                CalendarContract.Colors.COLOR_KEY,
                                CalendarContract.Colors.COLOR,
                        },
                        null, null, null);
        if (cur != null)
        {
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

}
