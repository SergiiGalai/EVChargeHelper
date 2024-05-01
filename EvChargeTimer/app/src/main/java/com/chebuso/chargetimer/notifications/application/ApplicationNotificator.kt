package com.chebuso.chargetimer.notifications.application

import android.app.Activity
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.shared.UserMessage.showSnackbar
import com.chebuso.chargetimer.shared.helpers.TimeHelper
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsShortTime
import com.chebuso.chargetimer.shared.helpers.TimeHelper.toDate
import com.chebuso.chargetimer.notifications.INotificator
import com.chebuso.chargetimer.settings.ISettingsReader
import java.util.Date


class ApplicationNotificator internal constructor(
    private val settingsProvider: ISettingsReader,
    private val registrar: NotificationChannelRegistrar,
    private val alarmScheduler: NotificationAlarmScheduler,
    private val activity: Activity
) : INotificator {

    override fun scheduleCarChargedNotification(millisToEvent: Long) {
        val triggerAtMillis = getTriggerAtMillis(millisToEvent)
        val chargedAtMillis = getChargedAtMillis(millisToEvent)

        val channelId = registrar.registerCarChargedNotificationChannel()
        alarmScheduler.schedule(AlarmItem(
            triggerAtMillis,
            chargedAtMillis,
            CAR_CHARGED_NOTIFICATION_ID,
            channelId,
        ))

        val text = getSnackbarText(toDate(chargedAtMillis), toDate(triggerAtMillis))
        showSnackbar(activity, text)
    }

    private fun getTriggerAtMillis(millisToEvent: Long): Long{
        val millisToNotify = getMillisToNotify(millisToEvent)
        return TimeHelper.now() + millisToNotify
    }

    private fun getChargedAtMillis(millisToEvent: Long): Long = TimeHelper.now() + millisToEvent

    private fun getMillisToNotify(millisToEvent: Long): Long {
        val minutesToRemind = settingsProvider.getApplicationReminderMinutes()
        val millisToNotify = TimeHelper.convertMinutesToMs(minutesToRemind)
        return if (millisToEvent > millisToNotify) millisToEvent - millisToNotify else millisToEvent
    }

    private fun getSnackbarText(chargedAt: Date, triggerAt: Date): String {
        val descriptionTemplate = activity.getString(R.string.car_charged_time_snackbar_desc)
        return String.format(descriptionTemplate,
            chargedAt.formatAsShortTime(),
            triggerAt.formatAsShortTime())
    }

    companion object {
        private val TAG = ApplicationNotificator::class.java.simpleName
        private const val CAR_CHARGED_NOTIFICATION_ID = 204
    }
}

