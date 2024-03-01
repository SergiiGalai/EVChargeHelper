package com.chebuso.chargetimer.settings;

public interface ISettingsWriter {
    void setFirstApplicationRunCompleted();
    void saveCalendarAdvancedNotificationsAllowed(boolean value);
}
