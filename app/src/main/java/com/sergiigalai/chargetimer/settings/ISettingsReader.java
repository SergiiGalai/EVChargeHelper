package com.sergiigalai.chargetimer.settings;

public interface ISettingsReader {
    boolean firstApplicationRun();

    boolean applicationNotificationsAllowed();

    boolean googleBasicNotificationsAllowed();

    boolean googleAdvancedNotificationsAllowed();

    double getBatteryCapacity();

    int getChargingLossPct();

    int getDefaultAmperage();

    int getDefaultVoltage();

    int getApplicationReminderMinutes();

    int getCalendarReminderMinutes();
}
