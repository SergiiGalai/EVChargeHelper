package com.example.dell.chargehelper.charge;

import com.example.dell.chargehelper.helpers.Convert;

import org.junit.Test;

import java.util.List;
import static org.junit.Assert.*;

public class ChargeValuesProviderTest {

    @Test
    public void contains_amperage_when_default_amperage_not_in_range(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(20);
        assertTrue(amperage.contains("20"));
    }

    @Test
    public void contains_amperage_when_default_amperage_in_range(){
        List<String> amperage = ChargeValuesProvider.getAllowedAmperage(16);
        assertTrue(amperage.contains("16"));
    }

    @Test
    public void equals_expected_when_default_voltage_bigger_delta(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(200, 10);
        String[] expected = {"190", "195", "200", "205", "210" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }

    @Test
    public void equals_expected_when_default_voltage_less_delta(){
        List<String> voltage = ChargeValuesProvider.getAllowedVoltage(10, 40);
        String[] expected = {"0", "5", "10", "15", "20" };
        assertArrayEquals(expected, Convert.toArray(voltage));
    }
}
