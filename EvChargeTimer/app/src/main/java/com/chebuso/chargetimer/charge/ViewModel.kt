package com.chebuso.chargetimer.charge

import android.content.Context
import android.util.Log
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.shared.Time
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsShortDateTime
import com.chebuso.chargetimer.shared.helpers.TimeHelper.formatAsShortTime
import com.chebuso.chargetimer.shared.helpers.TimeHelper.now
import com.chebuso.chargetimer.shared.helpers.TimeHelper.toDate
import com.chebuso.chargetimer.shared.helpers.TimeHelper.toTime
import java.util.Date

class ViewModel(
    context: Context,
    powerLine: PowerLine,
    battery: Battery,
) {
    val remainingEnergyText: String
    val chargedInText: String
    val remindButtonText: String
    val millisToCharge: Long

    init {
        Log.d(TAG, "refresh")
        remainingEnergyText = getRemainingEnergyText(context, battery)
        millisToCharge = getMillisToChargeValue(powerLine, battery)

        val timeToCharge = getTimeToCharge()
        Log.d(TAG, "refresh.$timeToCharge")
        chargedInText = getChargedInText(context, timeToCharge)
        remindButtonText = getRemindButtonText(context, timeToCharge)
    }

    private fun getChargedAt(): Date = toDate(now() + millisToCharge)
    private fun getTimeToCharge(): Time = toTime(millisToCharge)

    private fun getRemindButtonText(context: Context, timeToCharge: Time): String {
        val chargedAt = getChargedAt()
        return if (timeToCharge.days > 0) String.format(
            context.getString(R.string.remind_me_button_title),
            chargedAt.formatAsShortDateTime()
        ) else String.format(
            context.getString(R.string.remind_me_button_title),
            chargedAt.formatAsShortTime()
        )
    }

    private fun getChargedInText(context: Context, timeToCharge: Time): String =
        if (timeToCharge.days > 0) String.format(
            context.getString(R.string.should_be_charged_in_days_title),
            timeToCharge.days, timeToCharge.hours, timeToCharge.minutes
        ) else String.format(
            context.getString(R.string.should_be_charged_in_hours_title),
            timeToCharge.hours, timeToCharge.minutes
        )

    private fun getRemainingEnergyText(context: Context, battery: Battery): String {
        val remainingEnergyKWt = getRemainingEnergyKWh(battery)
        return String.format(
            context.getString(R.string.remaining_energy_title),
            battery.remainingEnergyPct, remainingEnergyKWt, battery.UsableCapacityKWh
        )
    }

    private fun getMillisToChargeValue(powerLine: PowerLine, battery: Battery): Long {
        val chargeTimeResolver = LiIonChargeTimeResolver(powerLine, battery)
        return chargeTimeResolver.getTimeToCharge()
    }

    private fun getRemainingEnergyKWh(battery: Battery): Double {
        val value = battery.UsableCapacityKWh * battery.remainingEnergyPct / 100
        Log.d(TAG, "getRemainingEnergyKWh=$value")
        return value
    }

    companion object {
        private val TAG = ViewModel::class.simpleName
    }
}

