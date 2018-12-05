package com.example.dell.chargetimer.charge;

import com.example.dell.chargetimer.Time;
import com.example.dell.chargetimer.helpers.TimeHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Leaf24ChargeTimeResolverTest
{
    private LiionChargeTimeResolver timeResolver;
    private PowerLine power;
    private Battery battery;

    @Before
    public void setUp() {
        power = new PowerLine();
        battery = new Battery();

        power.Voltage = 220;
        power.Amperage = 32;
        battery.ChargingLoss = 12;
        battery.UsefulCapacityKWh = 22;

        timeResolver = new LiionChargeTimeResolver(power, battery);
    }

    @Test
    public void return_time_when_calculating_for_0pct_battery_home_charger(){
        power.Amperage = 16;

        long actual = timeResolver.getMillisToCharge((byte) 0);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(8, 23), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_0pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 0);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(4, 11), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_80pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 80);
        Time actualTime = TimeHelper.getHoursAndMinutes(actual);
        assertEquals(new Time(1, 27), actualTime);
    }

}
