package com.example.dell.chargetimer.settings;

public interface ISettingsWriter {
    void setFirstApplicationRunDone();
    void saveGoogleAdvancedNotificationsAllowed(boolean value);
}
