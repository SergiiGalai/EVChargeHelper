package com.chebuso.chargetimer.charge

import com.chebuso.chargetimer.charge.ChargeValuesProvider.getAllowedAmperage
import com.chebuso.chargetimer.charge.ChargeValuesProvider.getAllowedVoltage
import org.junit.Assert
import org.junit.Test


class ChargeValuesProviderTest {
    @Test fun equals_expected_when_default_amperage_less_16() {
        val amperage = getAllowedAmperage(8)
        val expected = arrayOf("6", "8", "10", "12", "14", "16", "22", "32", "64")

        Assert.assertArrayEquals(expected, amperage.toTypedArray())
    }

    @Test fun equals_expected_when_default_amperage_is_16() {
        val amperage = getAllowedAmperage(16)
        val expected = arrayOf("6", "8", "10", "12", "14", "16", "22", "32", "64")

        Assert.assertArrayEquals(expected, amperage.toTypedArray())
    }

    @Test fun equals_expected_when_default_amperage_is_22() {
        val amperage = getAllowedAmperage(22)
        val expected =
            arrayOf("8", "12", "16", "19", "22", "25", "28", "31", "34", "37", "40", "43")

        Assert.assertArrayEquals(expected, amperage.toTypedArray())
    }

    @Test fun equals_expected_when_default_amperage_is_32() {
        val amperage = getAllowedAmperage(32)
        val expected =
            arrayOf("8", "12", "16", "22", "28", "32", "34", "40", "46", "52", "58", "64")

        Assert.assertArrayEquals(expected, amperage.toTypedArray())
    }

    @Test fun contains_amperage_when_default_amperage_not_in_range() {
        val amperage = getAllowedAmperage(128)

        Assert.assertTrue(amperage.contains("128"))
    }

    @Test fun contains_amperage_when_default_amperage_in_range() {
        val amperage = getAllowedAmperage(16)

        Assert.assertTrue(amperage.contains("16"))
    }

    @Test fun equals_expected_when_default_voltage_bigger_delta() {
        val voltage = getAllowedVoltage(200)
        val expected = arrayOf(
            "160", "165", "170", "175", "180", "185", "190", "195",
            "200", "205", "210", "215", "220", "225", "230", "235", "240"
        )
        Assert.assertArrayEquals(expected, voltage.toTypedArray())
    }

    @Test fun equals_expected_when_default_voltage_less_delta() {
        val voltage = getAllowedVoltage(10)
        val expected = arrayOf("0", "5", "10", "15", "20")

        Assert.assertArrayEquals(expected, voltage.toTypedArray())
    }

    @Test fun equals_expected_when_default_voltage_US() {
        val voltage = getAllowedVoltage(110)
        val expected = arrayOf("90", "95", "100", "105", "110", "115", "120", "125", "130")

        Assert.assertArrayEquals(expected, voltage.toTypedArray())
    }
}

