package com.chebuso.chargetimer.repositories;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chebuso.chargetimer.models.CalendarEntity;
import com.chebuso.chargetimer.models.CalendarEventEntity;

import java.util.List;

public interface ICalendarRepository {
    @Nullable
    CalendarEntity getPrimaryCalendar();

    @NonNull
    List<CalendarEntity> getAvailableCalendars();

    int createCalendar(CalendarEntity calendar, String calendarColor);
    int deleteCalendar(String calendarName);
    long createEvent(long calendarId, CalendarEventEntity event);
    void setReminder(long eventID, int minutesBefore);
}
