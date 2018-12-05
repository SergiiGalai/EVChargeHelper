package com.example.dell.chargetimer.charge;

import com.example.dell.chargetimer.Time;
import com.example.dell.chargetimer.helpers.TimeHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class Volt2014ChargeTimeResolverTest
{
    private LiionChargeTimeResolver timeResolver;
    private PowerLine power;
    private Battery battery;

    @Before
    public void setUp() {
        power = new PowerLine();
        battery = new Battery();

        power.Voltage = 220;
        power.Amperage = 16;
        battery.ChargingLoss = 12;
        battery.UsefulCapacityKWh = 10.5;

        timeResolver = new LiionChargeTimeResolver(power, battery);
    }

    @Test()
    public void return_zero_when_amperage_zero(){
        power.Amperage = 0;

        long actual = timeResolver.getMillisToCharge((byte) 10);
        assertEquals(0, actual);
    }

    @Test
    public void return_zero_when_voltage_zero(){
        power.Voltage = 0;

        long actual = timeResolver.getMillisToCharge((byte) 10);
        assertEquals(0, actual);
    }

    @Test
    public void return_time_when_calculating_for_0pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 0);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(4, 0), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_10pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 10);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(3, 40), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_25pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 25);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(3, 10), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_50pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 50);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(2, 20), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_80pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 80);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(1, 20), actualTime);
    }


    @Test
    public void return_time_when_calculating_for_90pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 90);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(0, 40), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_95pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 95);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(0, 20), actualTime);
    }


}
