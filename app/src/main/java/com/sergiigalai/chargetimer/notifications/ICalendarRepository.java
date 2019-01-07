package com.sergiigalai.chargetimer.notifications;

import android.content.ContentValues;

interface ICalendarRepository {
    boolean customColorsSupported();
    int getPrimaryCalendarId();
    long createEvent(ContentValues values);
    void setReminder(long eventID, int minutesBefore);
}
