package com.example.dell.chargehelper.charge;

import com.example.dell.chargehelper.charge.Battery;
import com.example.dell.chargehelper.charge.PowerLine;

public class ChargeTimeCalculator
{
    public long calculateTimeInMsToCharge(PowerLine powerLine, Battery battery){
        double powerWh = powerLine.Voltage * powerLine.Amperage;
        double kWhToCharge = battery.UsefulCapacityKWh * (100 - battery.RemainingEnergyPercents) / 100;
        double chargingInefficiency = (100 + battery.ChargingLoss) / 100;

        double hoursToCharge = (kWhToCharge * 1000 / powerWh) * chargingInefficiency;

        Double msToCharge = Double.valueOf(hoursToCharge * 3600 * 1000);
        return msToCharge.isInfinite() ? 0 : msToCharge.longValue();
    }
}

