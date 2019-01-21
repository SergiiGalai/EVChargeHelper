package com.chebuso.chargetimer.repositories;

import android.content.ContentValues;

import com.chebuso.chargetimer.models.CalendarEntity;

import java.util.List;

public interface ICalendarRepository {
    CalendarEntity getPrimaryCalendar();
    List<CalendarEntity> getAvailableCalendars();
    int createCalendar(CalendarEntity calendar, String calendarColor);
    int deleteCalendar(String calendarName);
    long createEvent(ContentValues values);
    void setReminder(long eventID, int minutesBefore);
}
