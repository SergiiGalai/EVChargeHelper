package com.chebuso.chargetimer.notifications.calendar


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.shared.UserMessage.showToast
import com.chebuso.chargetimer.calendar.CalendarEntity
import com.chebuso.chargetimer.calendar.CalendarEventEntity
import com.chebuso.chargetimer.calendar.dal.ICalendarRepository
import com.chebuso.chargetimer.calendar.dal.IEventRepository
import com.chebuso.chargetimer.calendar.dal.IReminderRepository
import com.chebuso.chargetimer.permissions.PermissionHelper
import com.chebuso.chargetimer.notifications.INotificator
import com.chebuso.chargetimer.permissions.PermissionActivityResultLauncher
import com.chebuso.chargetimer.permissions.PermissionRequestDialog
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ISettingsWriter


class CalendarAdvancedNotificator internal constructor(
    private val fallbackNotificator: INotificator,
    private val calendarRepository: ICalendarRepository,
    private val eventRepository: IEventRepository,
    private val reminderRepository: IReminderRepository,
    private val settingsProvider: ISettingsReader,
    private val settingsWriter: ISettingsWriter,
    private val activity: Activity,
    permissionResultLauncher: PermissionActivityResultLauncher,
) : INotificator {

    private lateinit var event: CalendarEventEntity
    private val permissionDialog = PermissionRequestDialog(
        activity.getString(R.string.calendar_permission_rationale_title),
        activity.getString(R.string.calendar_permission_rationale),
        CALENDAR_PERMISSIONS,
        activity,
        permissionResultLauncher,
    )

    override fun scheduleCarChargedNotification(millisToEvent: Long) {
        Log.d(TAG, "started scheduleCarChargedNotification")
        event = createEvent(millisToEvent)
        if (PermissionHelper.isFullCalendarPermissionsGranted(activity)) {
            Log.d(TAG, "Full calendar permissions granted")
            val calendar = calendarRepository.findPrimaryCalendar()
            scheduleCalendarEvent(calendar)
            return
        }
        if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            Log.d(TAG, "Calendar permissions not granted")
            permissionDialog.requestPermissionsIfNeeded()
        }
    }

    private fun createEvent(millisToEvent: Long) = CalendarEventEntity(
        activity.getString(R.string.car_charged_title),
        activity.getString(R.string.car_charged_descr),
        millisToEvent
    )

    private fun scheduleCalendarEvent(calendar: CalendarEntity?) {
        if (calendar == null) {
            disableAdvancedNotification()
            scheduleEventUsingDefaultNotificator(R.string.error_no_primary_calendar)
            return
        }

        val eventId = eventRepository.createEvent(calendar.id, event)
        if (eventId == -1L) {
            scheduleEventUsingDefaultNotificator(R.string.error_creating_calendar_event)
            return
        }

        setReminder(eventId)
        openEventActivity(eventId)
    }

    private fun disableAdvancedNotification() {
        Log.i(TAG, "disableAdvancedNotification")
        settingsWriter.saveCalendarAdvancedNotificationsAllowed(false)
    }

    private fun scheduleEventUsingDefaultNotificator(
        @StringRes messageId: Int
    ) {
        Log.i(TAG, "scheduleEventUsingDefaultNotificator $event")
        showToast(activity, messageId, Toast.LENGTH_LONG)
        fallbackNotificator.scheduleCarChargedNotification(event.millisToStart)
    }

    private fun openEventActivity(eventId: Long) {
        val uri = CalendarContract.Events.CONTENT_URI.buildUpon()
            .appendPath(eventId.toString())
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(uri.build())
        activity.startActivity(intent)
    }

    private fun setReminder(eventId: Long) {
        val reminderMinutes = settingsProvider.getCalendarReminderMinutes()
        reminderRepository.setReminder(eventId, reminderMinutes)
        Log.i(TAG, "setReminder $eventId")
    }

    companion object {
        private val TAG = this::class.java.simpleName
        private val CALENDAR_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    }
}

