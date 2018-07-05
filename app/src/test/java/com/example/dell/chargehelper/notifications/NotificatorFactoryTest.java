package com.example.dell.chargehelper.notifications;

import android.app.Activity;
import android.net.Uri;

import com.example.dell.chargehelper.settings.ISettingsProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class NotificatorFactoryTest {

    private IResourceProvider uriProvider;
    private ISettingsProvider settings;
    private NotificatorFactory factory;
    private Activity activity;

    @Before
    public void setUp(){
        activity = mock(Activity.class);
        settings = mock(ISettingsProvider.class);
        uriProvider = mock(IResourceProvider.class);
        factory = new NotificatorFactory(settings, uriProvider, activity);
    }

    @Test
    public void empty_list_when_no_settings(){
        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.isEmpty(), is(true));
    }

    @Test
    public void application_notifications_when_applicationNotificationsAllowed(){
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
    public void advanced_google_notifications_when_googleAdvancedNotificationsAllowed_and_googleBasicNotificationsAllowed(){
        when(settings.googleAdvancedNotificationsAllowed()).thenReturn(true);
        when(settings.googleBasicNotificationsAllowed()).thenReturn(true);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(1));
        assertTrue(actual.get(0) instanceof  GoogleCalendarAdvancedNotificator);
    }

    @Test
    public void basic_google_notifications_when_googleBasicNotificationsAllowed(){
        when(settings.googleBasicNotificationsAllowed()).thenReturn(true);

        List<INotificator> actual = factory.createNotificators();

        assertThat(actual.size(), is(1));
        assertTrue(actual.get(0) instanceof  GoogleCalendarDefaultNotificator);
    }
}
