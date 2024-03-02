package com.chebuso.chargetimer.charge

object ChargeValuesProvider {
    private const val MAX_HOME_SOCKET_AMPERAGE = 16

    fun getAllowedAmperage(defaultAmperage: Int): List<String> {
        val values =
            if (defaultAmperage > MAX_HOME_SOCKET_AMPERAGE)
                getAllowedAmperageForPublicChargers(defaultAmperage)
            else
                getAllowedAmperageForHomeSockets(defaultAmperage)

        return values.sortedWith{ o1, o2 -> o1.toInt() - o2.toInt()}
    }

    private fun getAllowedAmperageForHomeSockets(defaultAmperage: Int): List<String> {
        val values: MutableList<String> = generateSequence(6, MAX_HOME_SOCKET_AMPERAGE, 2)
        if (!values.contains(defaultAmperage.toString()))
            values.add(defaultAmperage.toString())
        values.add("22")
        values.add("32")
        values.add("64")
        return values
    }

    private fun getAllowedAmperageForPublicChargers(defaultAmperage: Int): List<String> {
        val values = generateSequence(8, MAX_HOME_SOCKET_AMPERAGE, 4)
        values.add(defaultAmperage.toString())
        val MAX_STEPS = 8
        var minAmperage = defaultAmperage / 2
        if (minAmperage < MAX_HOME_SOCKET_AMPERAGE) minAmperage = MAX_HOME_SOCKET_AMPERAGE
        val maxAmperage = defaultAmperage * 2
        val step = (maxAmperage - minAmperage) / MAX_STEPS
        val additionalValues: List<String> = generateSequence(minAmperage, maxAmperage, step)
        for (tmpValue in additionalValues) {
            if (!values.contains(tmpValue))
                values.add(tmpValue)
        }
        return values
    }

    fun getAllowedVoltage(defaultVoltage: Int): List<String> {
        var delta = getMinMaxVoltageDelta(defaultVoltage)
        if (delta > defaultVoltage) delta = defaultVoltage
        val min = defaultVoltage - delta
        val max = defaultVoltage + delta
        return getAllowedVoltages(min, max)
    }

    private fun getMinMaxVoltageDelta(defaultVoltage: Int): Int {
        val MAX_US_VOLTAGE = 140
        val US_DELTA = 20
        val DEFAULT_DELTA = 40
        val isUSVoltage = defaultVoltage < MAX_US_VOLTAGE
        return if (isUSVoltage) US_DELTA else DEFAULT_DELTA
    }

    private fun getAllowedVoltages(minVoltage: Int, maxVoltage: Int): List<String> {
        val VOLTAGE_STEP = 5
        return generateSequence(minVoltage, maxVoltage, VOLTAGE_STEP)
    }

    private fun generateSequence(min: Int, max: Int, step: Int): MutableList<String> {
        val allowedValues: MutableList<String> = ArrayList()
        for(i in min..max step step){
            allowedValues.add(i.toString())
        }
        return allowedValues
    }
}

