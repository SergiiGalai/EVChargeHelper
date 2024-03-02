package com.chebuso.chargetimer.charge

import android.content.Context
import android.util.Log
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.helpers.TimeHelper.formatAsShortDateTime
import com.chebuso.chargetimer.helpers.TimeHelper.formatAsShortTime
import com.chebuso.chargetimer.helpers.TimeHelper.now
import com.chebuso.chargetimer.helpers.TimeHelper.toDate
import com.chebuso.chargetimer.helpers.TimeHelper.toTime

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

        val remainingEnergyKWt = getRemainingEnergyKWh(battery)
        remainingEnergyText = String.format(
            context.getString(R.string.remaining_energy_title),
            battery.remainingEnergyPct, remainingEnergyKWt, battery.UsableCapacityKWh
        )
        val chargeTimeResolver = LiIonChargeTimeResolver(powerLine, battery)
        millisToCharge = chargeTimeResolver.getTimeToCharge()
        val dateChargedAt = toDate(now() + millisToCharge)
        val time = toTime(millisToCharge)
        Log.d(TAG, "refresh.$time")
        if (time.days > 0) {
            chargedInText = String.format(
                context.getString(R.string.should_be_charged_in_days_title),
                time.days, time.hours, time.minutes
            )
            remindButtonText = String.format(
                context.getString(R.string.remind_me_button_title),
                dateChargedAt.formatAsShortDateTime()
            )
        } else {
            chargedInText = String.format(
                context.getString(R.string.should_be_charged_in_hours_title),
                time.hours, time.minutes
            )
            remindButtonText = String.format(
                context.getString(R.string.remind_me_button_title),
                dateChargedAt.formatAsShortTime()
            )
        }
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

