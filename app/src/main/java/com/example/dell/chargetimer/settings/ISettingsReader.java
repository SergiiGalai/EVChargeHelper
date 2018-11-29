package com.example.dell.chargetimer.settings;

public interface ISettingsReader {
    boolean applicationNotificationsAllowed();

    boolean googleBasicNotificationsAllowed();

    boolean googleAdvancedNotificationsAllowed();

    Double getBatteryCapacity();

    Integer getChargingLossPct();

    Integer getDefaultAmperage();

    Integer getDefaultVoltage();

    int getApplicationReminderMinutes();

    int getCalendarReminderMinutes();
}
