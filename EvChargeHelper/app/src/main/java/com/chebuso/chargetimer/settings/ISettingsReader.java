package com.chebuso.chargetimer.settings;

public interface ISettingsReader {
    boolean firstApplicationRun();

    boolean applicationNotificationsAllowed();

    boolean calendarBasicNotificationsAllowed();

    boolean calendarAdvancedNotificationsAllowed();

    double getBatteryCapacity();

    int getChargingLossPct();

    int getDefaultAmperage();

    int getDefaultVoltage();

    int getApplicationReminderMinutes();

    int getCalendarReminderMinutes();
}
