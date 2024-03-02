package com.chebuso.chargetimer.notifications


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.UserMessage.showToast
import com.chebuso.chargetimer.calendar.CalendarEntity
import com.chebuso.chargetimer.calendar.CalendarEventEntity
import com.chebuso.chargetimer.calendar.dal.ICalendarRepository
import com.chebuso.chargetimer.calendar.dal.IEventRepository
import com.chebuso.chargetimer.calendar.dal.IReminderRepository
import com.chebuso.chargetimer.helpers.PermissionHelper.isFullCalendarPermissionsGranted
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ISettingsWriter


class CalendarAdvancedNotificator internal constructor(
    private val fallbackNotificator: INotificator,
    private val calendarRepository: ICalendarRepository,
    private val eventRepository: IEventRepository,
    private val reminderRepository: IReminderRepository,
    private val settingsProvider: ISettingsReader,
    private val settingsWriter: ISettingsWriter,
    private val activity: Activity
) : INotificator {

    private lateinit var event: CalendarEventEntity

    override fun scheduleCarChargedNotification(millisToEvent: Long) {
        Log.d(TAG, "started scheduleCarChargedNotification")
        event = createEvent(millisToEvent)
        if (isFullCalendarPermissionsGranted(activity)) {
            Log.d(TAG, "Full calendar permissions granted")
            val calendar = calendarRepository.getPrimaryCalendar()
            scheduleCalendarEvent(calendar)
        } else if (settingsProvider.calendarAdvancedNotificationsAllowed()) {
            Log.d(TAG, "Calendar permissions not granted")
            requestCalendarPermission()
            scheduleCalendarEvent(null)
        }
    }

    private fun createEvent(millisToEvent: Long): CalendarEventEntity = CalendarEventEntity(
        activity.getString(R.string.car_charged_title),
        activity.getString(R.string.car_charged_descr),
        millisToEvent
    )

    private fun scheduleCalendarEvent(calendar: CalendarEntity?) {
        if (calendar == null) {
            disableAdvancedNotification()
            scheduleEventUsingDefaultNotificator(R.string.error_no_primary_calendar)
        } else {
            val eventId = eventRepository.createEvent(calendar.id, event)
            if (eventId == -1L) {
                scheduleEventUsingDefaultNotificator(R.string.error_creating_calendar_event)
                return
            }
            setReminder(eventId)
            openEventActivity(eventId)
        }
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

    private fun requestCalendarPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_CALENDAR
            )
        ) {
            Log.i(TAG, "Show permissions rationale")
            showRationaleDialog()
        } else {
            Log.i(TAG, "Request calendar permissions")
            ActivityCompat.requestPermissions(activity,
                PERMISSIONS_CALENDAR,
                REQUEST_CALENDAR
            )
        }
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(activity).create().apply {
            setTitle(activity.getString(R.string.calendar_permission_rationale_title))
            setMessage(activity.getString(R.string.calendar_permission_rationale))

            setButton(
                AlertDialog.BUTTON_NEGATIVE,
                activity.getString(R.string.permission_dialog_forbid)
            ) { dialog, _ ->
                dialog.dismiss()
            }

            setButton(
                AlertDialog.BUTTON_POSITIVE,
                activity.getString(R.string.permission_dialog_allow)
            ) { dialog, _ ->
                dialog.dismiss()
                Log.i(TAG, "Request calendar permissions")
                ActivityCompat.requestPermissions(activity,
                    PERMISSIONS_CALENDAR,
                    REQUEST_CALENDAR
                )
            }
        }.show()
    }

    companion object {
        const val REQUEST_CALENDAR = 1
        private val TAG = CalendarAdvancedNotificator::class.java.simpleName
        private val PERMISSIONS_CALENDAR = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    }
}

