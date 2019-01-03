package com.sergiigalai.chargetimer.notifications;

import android.content.ContentValues;

interface ICalendarRepository {
    int getPrimaryCalendarId();
    long createEvent(ContentValues values);
    void setReminder(long eventID, int minutesBefore);
}
