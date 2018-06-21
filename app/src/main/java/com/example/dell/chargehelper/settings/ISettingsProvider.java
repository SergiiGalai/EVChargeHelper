package com.example.dell.chargehelper.settings;

public interface ISettingsProvider {
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
