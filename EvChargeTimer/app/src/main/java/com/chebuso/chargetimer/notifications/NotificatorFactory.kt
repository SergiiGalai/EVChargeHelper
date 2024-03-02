package com.chebuso.chargetimer.notifications


import android.app.Activity
import com.chebuso.chargetimer.calendar.dal.CalendarRepository
import com.chebuso.chargetimer.calendar.dal.EventRepository
import com.chebuso.chargetimer.calendar.dal.ReminderRepository
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ISettingsWriter
import kotlinx.coroutines.yield


internal class NotificatorFactory(
    private val activity: Activity,
    private val settingsProvider: ISettingsReader,
    private val resourceProvider: IResourceProvider,
    private val settingsWriter: ISettingsWriter
) {

    fun tryCreate(calendarPermissionsGranted: Boolean): INotificator? {
        if (calendarPermissionsGranted && settingsProvider.calendarAdvancedNotificationsAllowed()) {
            return createAdvancedNotificator()
        } else {
            if (settingsProvider.calendarBasicNotificationsAllowed()) {
                return createBasicNotificator()
            }
        }
        return null
    }

    fun createNotificators() = sequence {
        if (settingsProvider.applicationNotificationsAllowed()) {
            yield(ApplicationNotificator(settingsProvider, resourceProvider, activity))
        }
        if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            yield(createAdvancedNotificator())
        } else {
            if (settingsProvider.calendarBasicNotificationsAllowed()) {
                yield(createBasicNotificator())
            }
        }
    }

    private fun createAdvancedNotificator(): INotificator {
        return CalendarAdvancedNotificator(
            createBasicNotificator(),
            CalendarRepository(activity),
            EventRepository(activity),
            ReminderRepository(activity),
            settingsProvider,
            settingsWriter,
            activity
        )
    }

    private fun createBasicNotificator(): CalendarDefaultNotificator =
        CalendarDefaultNotificator(activity)
}

