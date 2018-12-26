package com.sergiigalai.chargetimer.settings;

public interface ISettingsReader {
    boolean firstApplicationRun();

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
