package com.chebuso.chargetimer.charge

class PowerLine(
    val Voltage: Int,
    val Amperage: Int,
    )

class Battery(
    val UsableCapacityKWh: Double,
    val ChargingLossPct: Double,
    val remainingEnergyPct: Byte
    )
