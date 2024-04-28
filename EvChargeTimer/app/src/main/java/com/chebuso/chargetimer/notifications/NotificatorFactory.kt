package com.chebuso.chargetimer.notifications


import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import com.chebuso.chargetimer.calendar.dal.CalendarRepository
import com.chebuso.chargetimer.calendar.dal.EventRepository
import com.chebuso.chargetimer.calendar.dal.ReminderRepository
import com.chebuso.chargetimer.notifications.application.ApplicationNotificator
import com.chebuso.chargetimer.notifications.application.NotificationAlarmScheduler
import com.chebuso.chargetimer.notifications.application.NotificationChannelRegistrar
import com.chebuso.chargetimer.notifications.calendar.CalendarAdvancedNotificator
import com.chebuso.chargetimer.notifications.calendar.CalendarDefaultNotificator
import com.chebuso.chargetimer.notifications.calendar.PermissionActivityResultLauncher
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ISettingsWriter


class NotificatorFactory(
    private val settingsProvider: ISettingsReader,
    private val resourceProvider: IResourceProvider,
    private val settingsWriter: ISettingsWriter,
    private val activity: Activity,
    private val permissionResultLauncher: PermissionActivityResultLauncher,
) {

    fun tryCreate(calendarPermissionsGranted: Boolean): INotificator? {
        if (calendarPermissionsGranted && settingsProvider.calendarAdvancedNotificationsAllowed()) {
            return createAdvancedCalendarNotificator()
        }
        if (settingsProvider.calendarBasicNotificationsAllowed()) {
            return createBasicCalendarNotificator()
        }
        return null
    }

    fun createNotificators() = sequence {
        if (settingsProvider.applicationNotificationsAllowed()) {
            yield(createApplicationNotificator())
        }
        if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            yield(createAdvancedCalendarNotificator())
        } else {
            if (settingsProvider.calendarBasicNotificationsAllowed()) {
                yield(createBasicCalendarNotificator())
            }
        }
    }

    private fun createApplicationNotificator(): INotificator = ApplicationNotificator(
        settingsProvider,
        NotificationChannelRegistrar(resourceProvider, activity),
        NotificationAlarmScheduler(activity),
        activity
    )

    private fun createAdvancedCalendarNotificator(): INotificator = CalendarAdvancedNotificator(
        createBasicCalendarNotificator(),
        CalendarRepository(activity),
        EventRepository(activity),
        ReminderRepository(activity),
        settingsProvider,
        settingsWriter,
        activity,
        permissionResultLauncher,
    )

    private fun createBasicCalendarNotificator() = CalendarDefaultNotificator(activity)
}

