package com.chebuso.chargetimer.charge;

import com.chebuso.chargetimer.helpers.Convert;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class ChargeValuesProviderTest {

    @Test
    public void equals_expected_when_default_amperage_less_16(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(8);
        String[] expected = {"6", "8", "10", "12", "14", "16", "22", "32", "64" };
        assertArrayEquals(expected, Convert.toArray(amperage));
    }

    @Test
    public void equals_expected_when_default_amperage_is_16(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(16);
        String[] expected = {"6", "8", "10", "12", "14", "16", "22", "32", "64" };
        assertArrayEquals(expected, Convert.toArray(amperage));
    }

    @Test
    public void equals_expected_when_default_amperage_is_22(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(22);
        String[] expected = {"8", "12", "16",  "19", "22", "25", "28", "31", "34", "37", "40", "43" };
        assertArrayEquals(expected, Convert.toArray(amperage));
    }

    @Test
    public void equals_expected_when_default_amperage_is_32(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(32);
        String[] expected = {"8", "12", "16",  "22", "28", "32", "34", "40", "46", "52", "58", "64" };
        assertArrayEquals(expected, Convert.toArray(amperage));
    }


    @Test
    public void contains_amperage_when_default_amperage_not_in_range(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(128);
        assertTrue(amperage.contains("128"));
    }

    @Test
    public void contains_amperage_when_default_amperage_in_range(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(16);
        assertTrue(amperage.contains("16"));
    }

    @Test
    public void equals_expected_when_default_voltage_bigger_delta(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(200);
        String[] expected = {"160", "165", "170", "175", "180", "185", "190", "195", "200", "205", "210", "215", "220", "225", "230", "235", "240" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }

    @Test
    public void equals_expected_when_default_voltage_less_delta(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(10);
        String[] expected = {"0", "5", "10", "15", "20" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }

    @Test
    public void equals_expected_when_default_voltage_US(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(110);
        String[] expected = {"90", "95", "100", "105", "110", "115", "120", "125", "130" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }
}
