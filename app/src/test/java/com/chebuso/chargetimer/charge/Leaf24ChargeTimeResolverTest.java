package com.chebuso.chargetimer.charge;

import com.chebuso.chargetimer.Time;
import com.chebuso.chargetimer.helpers.TimeHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Leaf24ChargeTimeResolverTest
{
    private LiIonChargeTimeResolver timeResolver;
    private PowerLine power;

    @Before
    public void setUp() {
        power = new PowerLine();
        Battery battery = new Battery();

        power.Voltage = 220;
        power.Amperage = 32;
        battery.ChargingLossPct = 12;
        battery.UsableCapacityKWh = 22;

        timeResolver = new LiIonChargeTimeResolver(power, battery);
    }

    @Test
    public void return_time_when_calculating_for_0pct_battery_home_charger(){
        power.Amperage = 16;

        long actual = timeResolver.getMillisToCharge((byte) 0);
        Time actualTime = TimeHelper.toTime(actual);
        assertEquals(new Time(8, 23), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_0pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 0);
        Time actualTime = TimeHelper.toTime(actual);
        assertEquals(new Time(4, 11), actualTime);
    }

    @Test
    public void return_time_when_calculating_for_80pct_battery(){
        long actual = timeResolver.getMillisToCharge((byte) 80);
        Time actualTime = TimeHelper.toTime(actual);
        assertEquals(new Time(1, 23), actualTime);
    }

}
