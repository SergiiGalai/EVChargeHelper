package com.example.dell.chargehelper.charge;

import com.example.dell.chargehelper.charge.Battery;
import com.example.dell.chargehelper.charge.PowerLine;

public class ChargeTimeCalculator
{
    public long calculateMsToCharge(PowerLine powerLine, Battery battery){
        double powerWh = powerLine.Voltage * powerLine.Amperage;
        double kWhToCharge = battery.UsefulCapacityKWh * (100 - battery.RemainingEnergyPercents) / 100;
        double chargingInefficiency = (100 + battery.ChargingLoss) / 100;
        double hoursToCharge = (kWhToCharge * 1000 / powerWh) * chargingInefficiency;
        long msToCharge = (long)(hoursToCharge * 3600 * 1000);
        return msToCharge;
    }
}

