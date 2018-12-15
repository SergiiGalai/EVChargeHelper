package com.example.dell.chargetimer.settings;

public interface ISettingsWriter {
    void setFirstApplicationRunCompleted();
    void saveGoogleAdvancedNotificationsAllowed(boolean value);
}
