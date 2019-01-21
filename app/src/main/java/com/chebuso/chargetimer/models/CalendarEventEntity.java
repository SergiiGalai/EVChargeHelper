package com.chebuso.chargetimer.models;

public class CalendarEventEntity {
    public String title;
    public String description;
    public long millisToStart;

    public CalendarEventEntity(String title, String description, long millisToStart) {
        this.title = title;
        this.description = description;
        this.millisToStart = millisToStart;
    }
}
