package com.chebuso.chargetimer.notifications;

import android.content.ContentValues;

public interface ICalendarRepository {
    int getPrimaryCalendarId();
    String getAvailableCalendars();
    int createCalendar(String calendarName, String calendarColor, String accountName);
    int deleteCalendar(String calendarName);
    long createEvent(ContentValues values);
    void setReminder(long eventID, int minutesBefore);
}