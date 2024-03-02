package com.chebuso.chargetimer.charge

class LiIonChargeTimeResolver(private val powerLine: PowerLine,
                              private val battery: Battery)
{
    fun getTimeToCharge(): Long {
        var hoursToCharge: Double
        if (battery.remainingEnergyPct < LINEAR_CHARGING_THRESHOLD_PCT) {
            hoursToCharge =
                getLinearDependencyTime(LINEAR_CHARGING_THRESHOLD_PCT, battery.remainingEnergyPct.toShort())
            hoursToCharge += getFinishChargingTime(LINEAR_CHARGING_THRESHOLD_PCT.toInt())
        } else {
            hoursToCharge = getFinishChargingTime(battery.remainingEnergyPct.toInt())
        }
        return convertHoursToMs(hoursToCharge)
    }

    private fun convertHoursToMs(hours: Double): Long {
        val ms = hours * 3600 * 1000
        return if (ms.isInfinite()) 0 else ms.toLong()
    }

    private val chargingPowerWh: Int
        get() = powerLine.Voltage * powerLine.Amperage

    private val chargingEfficiency: Double
        get() = 100 / (100 + battery.ChargingLossPct)

    private fun getLinearDependencyTime(maxPercentage: Short, currentPercentage: Short): Double {
        return emptyBatteryChargeTime *
                (maxPercentage - currentPercentage) / 100
    }

    private fun getFinishChargingTime(currentPercentage: Int): Double {
        val chargingAmperageSlowdown = 2
        return emptyBatteryChargeTime * chargingAmperageSlowdown *
                (100 - currentPercentage) / 100
    }

    private val emptyBatteryChargeTime: Double
        get() {
            val whToCharge: Double = battery.UsableCapacityKWh * 1000
            return whToCharge / (chargingPowerWh * chargingEfficiency)
        }

    companion object {
        private const val LINEAR_CHARGING_THRESHOLD_PCT: Short = 80
    }
}


