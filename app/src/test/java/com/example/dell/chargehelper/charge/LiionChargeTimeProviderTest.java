package com.example.dell.chargehelper.charge;

import com.example.dell.chargehelper.Time;
import com.example.dell.chargehelper.helpers.TimeHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LiionChargeTimeProviderTest
{
    private LiionChargeTimeProvider calculator;
    private PowerLine power;
    private Battery battery;

    @Before
    public void setUp() {
        power = new PowerLine();
        battery = new Battery();

        power.Voltage = 220;
        power.Amperage = 16;

        calculator = new LiionChargeTimeProvider(power, battery);
        initChevyVoltBattery();
    }

    private void initChevyVoltBattery(){
        battery.ChargingLoss = 12;
        battery.UsefulCapacityKWh = 11;
    }

    @Test()
    public void return_zero_when_amperage_zero(){
        power.Amperage = 0;
        battery.RemainingEnergyPercents = 10;

        long actual = calculator.getTimeToChargeMillis();
        assertEquals(0, actual);
    }

    @Test
    public void return_zero_when_voltage_zero(){
        power.Voltage = 0;
        battery.RemainingEnergyPercents = 10;

        long actual = calculator.getTimeToChargeMillis();
        assertEquals(0, actual);
    }

    @Test
    public void return_time_when_calculating_for_0_battery_full_Volt(){
        battery.RemainingEnergyPercents = 0;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(3, 55), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_10pct_battery_full_Volt(){
        battery.RemainingEnergyPercents = 10;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(3, 34), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_25pct_battery_full_Volt(){
        battery.RemainingEnergyPercents = 25;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(3, 3), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_50pct_battery_full_Volt(){
        battery.RemainingEnergyPercents = 50;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(2, 10), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_80pct_battery_full_Volt(){
        battery.RemainingEnergyPercents = 80;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(1, 7), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_90pct_battery_full_Volt(){
        battery.RemainingEnergyPercents = 90;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(0, 38), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_95pct_battery_full_Volt(){
        battery.RemainingEnergyPercents = 95;

        long actual = calculator.getTimeToChargeMillis();
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(0, 19), actualTime);
    }


}
