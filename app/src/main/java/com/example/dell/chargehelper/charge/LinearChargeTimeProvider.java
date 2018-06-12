package com.example.dell.chargehelper.charge;

public class LinearChargeTimeProvider implements IChargeTimeProvider
{
    private PowerLine powerLine;
    private Battery battery;

    public LinearChargeTimeProvider(PowerLine powerLine, Battery battery) {
        this.powerLine = powerLine;
        this.battery = battery;
    }

    @Override
    public long getTimeToChargeMillis(){
        double hoursToCharge = getLinearDependencyTime();
        Double msToCharge = hoursToCharge * 3600 * 1000;
        return msToCharge.isInfinite() ? 0 : msToCharge.longValue();
    }

    private int getChargingPowerWh() {
        return powerLine.Voltage * powerLine.Amperage;
    }

    private double getChargingInefficiency() {
        return (100 + battery.ChargingLoss) / 100;
    }

    private double getLinearDependencyTime() {
        double kWhToCharge = battery.UsefulCapacityKWh * (100 - battery.RemainingEnergyPercents) / 100;
        return kWhToCharge * getChargingInefficiency() * 1000 / getChargingPowerWh();
    }
}

