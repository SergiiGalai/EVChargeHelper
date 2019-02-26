package com.chebuso.chargetimer.settings;

public interface ISettingsReader {
    boolean firstApplicationRun();

    boolean applicationNotificationsAllowed();
    boolean calendarBasicNotificationsAllowed();
    boolean calendarAdvancedNotificationsAllowed();

    double getBatteryCapacity();
    int getChargingLossPct();

    int getDefaultHomeVoltage();
    int getDefaultHomeAmperage();

    int getDefaultPublicVoltage();
    int getDefaultPublicAmperage();

    int getApplicationReminderMinutes();
    int getCalendarReminderMinutes();
}
