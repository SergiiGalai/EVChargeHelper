package com.sergiigalai.chargetimer.settings;

public interface ISettingsWriter {
    void setFirstApplicationRunCompleted();
    void saveGoogleAdvancedNotificationsAllowed(boolean value);
}
