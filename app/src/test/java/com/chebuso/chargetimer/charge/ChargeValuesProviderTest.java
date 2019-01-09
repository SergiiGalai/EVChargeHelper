package com.chebuso.chargetimer.charge;

import com.chebuso.chargetimer.helpers.Convert;
import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class ChargeValuesProviderTest {

    @Test
    public void equals_expected_when_default_amperage_bigger_16(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(20);
        String[] expected = {"8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30" };
        assertArrayEquals(expected, Convert.toArray(amperage));
    }

    @Test
    public void equals_expected_when_default_amperage_less_16(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(8);
        String[] expected = {"6", "8", "10", "12", "14", "16", "32" };
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
        String[] expected = {"160", "170", "180", "190", "200", "210", "220", "230", "240" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }

    @Test
    public void equals_expected_when_default_voltage_less_delta(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(10);
        String[] expected = {"0", "10", "20" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }

    @Test
    public void equals_expected_when_default_voltage_US(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(110);
        String[] expected = {"90", "100", "110", "120", "130" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }
}
