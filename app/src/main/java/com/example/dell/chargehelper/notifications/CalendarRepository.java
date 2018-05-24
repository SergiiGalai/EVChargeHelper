package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;

class CalendarRepository{
    private Activity context;

    public CalendarRepository(Activity context) {
        this.context = context;
    }

    @NonNull
    public long createEvent(ContentValues values){
        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        return Long.parseLong(uri.getLastPathSegment());
    }

    public void setReminder(long eventID, int minutesBefore) {
        ContentResolver cr = context.getContentResolver();
        ContentValues values = createReminderValues(eventID, minutesBefore);
        Uri uri = cr.insert(CalendarContract.Reminders.CONTENT_URI, values);
        Cursor c = CalendarContract.Reminders.query(cr, eventID,
                new String[]{CalendarContract.Reminders.MINUTES});
        if (c.moveToFirst()) {
            System.out.println("calendar"
                    + c.getInt(c.getColumnIndex(CalendarContract.Reminders.MINUTES)));
        }
        c.close();
    }

    @NonNull
    private ContentValues createReminderValues(long eventID, int minutesBefore) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, minutesBefore);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        return values;
    }

    public int getPrimaryCalendarId(){
        long calId = 1;

        final int projection_id = 0;
        final int projection_name = 1;
        final int projection_primary = 2;

        Cursor cur = context.getContentResolver()
                .query(CalendarContract.Calendars.CONTENT_URI,
                        new String[]{
                                CalendarContract.Calendars._ID,
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                                CalendarContract.Calendars.IS_PRIMARY,
                        },
                        null, null, null);

        while (cur.moveToNext()){
            calId = cur.getLong(projection_id);
            String name = cur.getString(projection_name);
            String primary = cur.getString(projection_primary);
            if (primary.equals("1"))
                return (int)calId;
        };

        if (cur != null){
            cur.close();
        }

        return (int)calId;
    }


    public void showColors(){
        Cursor cur = context.getContentResolver()
                .query(CalendarContract.Colors.CONTENT_URI,
                        new String[]{
                                CalendarContract.Colors._ID,
                                CalendarContract.Colors.COLOR_KEY,
                                CalendarContract.Colors.COLOR,
                        },
                        null, null, null);

        while (cur.moveToNext()){
            long colId = cur.getLong(0);
            long colorKey = cur.getLong(1);
            long color = cur.getLong(2);

            String hexColor= Long.toHexString(color);
            System.out.println( "id=" + colId
                    + "; key=" + colorKey
                    + "; hexColor="+ hexColor);
        };

        if (cur != null){
            cur.close();
        }
    }

}
