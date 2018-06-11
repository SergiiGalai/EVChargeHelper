package com.example.dell.chargehelper.charge;

import com.example.dell.chargehelper.Time;
import com.example.dell.chargehelper.helpers.TimeHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChargeTimeCalculatorTest
{
    private ChargeTimeCalculator calculator;
    private PowerLine power;
    private Battery battery;

    @Before
    public void setUp() {
        calculator = new ChargeTimeCalculator();
        power = new PowerLine();
        power.Voltage = 220;
        power.Amperage = 16;

        battery = new Battery();
        initChevyVoltBattery();
    }

    private void initChevyVoltBattery(){
        battery.ChargingLoss = 27;
        battery.UsefulCapacityKWh = 10.5;
    }

    @Test()
    public void return_zero_when_amperage_zero(){
        power.Amperage = 0;
        battery.RemainingEnergyPercents = 10;

        long actual = calculator.calculateTimeInMsToCharge(power, battery);
        assertEquals(0, actual);
    }

    @Test
    public void return_zero_when_voltage_zero(){
        power.Voltage = 0;
        battery.RemainingEnergyPercents = 10;

        long actual = calculator.calculateTimeInMsToCharge(power, battery);
        assertEquals(0, actual);
    }

    @Test
    public void return_time_when_calculating_for_empty_Volt(){
        battery.RemainingEnergyPercents = 0;

        long actual = calculator.calculateTimeInMsToCharge(power, battery);
        Time time = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(13638068, actual);
    }
}
