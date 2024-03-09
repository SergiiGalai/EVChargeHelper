package com.chebuso.chargetimer.notifications.application

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.util.Log
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.notifications.IResourceProvider

class NotificationChannelRegistrar internal constructor(
    private val resourceProvider: IResourceProvider,
    private val context: Context
) {

    /**
     * @return channel ID
     */
    fun registerCarChargedNotificationChannel(): String {
        registerNotificationChannel(carChargedNotificationChannel)
        return CAR_CHARGED_NOTIFICATION_CHANNEL_ID
    }

    private fun registerNotificationChannel(channel: NotificationChannel) {
        Log.d(TAG, "registerNotificationChannel")
        notificationManager.createNotificationChannel(channel)
    }

    private val notificationManager
        get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val carChargedNotificationChannel: NotificationChannel
        get() = NotificationChannel(
            CAR_CHARGED_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.car_charged_title),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = context.getString(R.string.car_charged_channel_descr)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setSound(resourceProvider.applicationNotificationSoundUri, soundAttributes)
        }

    private val soundAttributes: AudioAttributes
        get() = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

    companion object {
        const val CAR_CHARGED_NOTIFICATION_CHANNEL_ID = "chebuso.evChargeTimer.carCharged"
        private val TAG = NotificationChannelRegistrar::class.java.simpleName
    }
}