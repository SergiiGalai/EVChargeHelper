package com.example.dell.chargetimer.notifications;

import android.app.Activity;
import android.net.Uri;

import com.example.dell.chargetimer.settings.ISettingsReader;
import com.example.dell.chargetimer.settings.ISettingsWriter;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class NotificatorFactoryTest {

    private Activity activity;
    private IResourceProvider uriProvider;
    private ISettingsReader settings;
    private NotificatorFactory factory;
    private ISettingsWriter settingsWriter;


    @Before
    public void setUp(){
        activity = mock(Activity.class);
        settings = mock(ISettingsReader.class);
        uriProvider = mock(IResourceProvider.class);
        settingsWriter = mock(ISettingsWriter.class);

        factory = new NotificatorFactory(activity, settings, uriProvider, settingsWriter);
    }

    @Test
    public void empty_list_when_no_settings(){
        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.isEmpty(), is(true));
    }

    @Test
    public void application_notifications_when_allowed_in_settings(){
        when(settings.applicationNotificationsAllowed()).thenReturn(true);
        when(uriProvider.getApplicationNotificationSoundUri()).thenReturn(Uri.EMPTY);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(1));
        assertTrue(actual.get(0) instanceof  ApplicationNotificator);
    }

    @Test
    public void advanced_google_notifications_when_googleAdvancedNotificationsAllowed(){
        when(settings.googleAdvancedNotificationsAllowed()).thenReturn(true);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(1));
        assertTrue(actual.get(0) instanceof  GoogleCalendarAdvancedNotificator);
    }

    @Test
    public void advanced_google_notifications_when_allowed_in_settings(){
        when(settings.googleAdvancedNotificationsAllowed()).thenReturn(true);
        when(settings.googleBasicNotificationsAllowed()).thenReturn(true);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(1));
        assertTrue(actual.get(0) instanceof  GoogleCalendarAdvancedNotificator);
    }

    @Test
    public void basic_google_notifications_when_allowed_in_settings(){
        when(settings.googleBasicNotificationsAllowed()).thenReturn(true);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(1));
        assertTrue(actual.get(0) instanceof  GoogleCalendarDefaultNotificator);
    }

    @Test
    public void app_and_basic_google_notifications_when_allowed_in_settings(){
        when(settings.applicationNotificationsAllowed()).thenReturn(true);
        when(settings.googleBasicNotificationsAllowed()).thenReturn(true);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(2));
        assertTrue(actual.get(0) instanceof  ApplicationNotificator);
        assertTrue(actual.get(1) instanceof  GoogleCalendarDefaultNotificator);
    }
}