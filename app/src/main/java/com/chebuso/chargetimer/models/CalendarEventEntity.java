package com.chebuso.chargetimer.models;

public class CalendarEventEntity {
    public final String title;
    public final String description;
    public final long millisToStart;
    public final int eventColor = CalendarEventColor.VIOLET;

    public CalendarEventEntity(String title, String description, long millisToStart) {
        this.title = title;
        this.description = description;
        this.millisToStart = millisToStart;
    }
}
