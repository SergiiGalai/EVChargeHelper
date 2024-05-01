package com.chebuso.chargetimer.notifications.application

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsShortTime
import com.chebuso.chargetimer.notifications.IResourceProvider
import java.util.Date

class NotificationFactory internal constructor(
    private val resourceProvider: IResourceProvider,
    private val context: Context
){
    fun carChargedNotification(chargedAt: Date, channelId: String): Notification {
        val description = String.format(
            context.getString(R.string.car_charged_time_desc),
            chargedAt.formatAsShortTime())

        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.car_charged_title))
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_timer_24px)
            .setSound(resourceProvider.applicationNotificationSoundUri)
            .build()
    }
}