package com.example.dell.chargehelper.charge;

public class LiionChargeTimeProvider implements IChargeTimeProvider
{
    private PowerLine powerLine;
    private Battery battery;

    public LiionChargeTimeProvider(PowerLine powerLine, Battery battery) {
        this.powerLine = powerLine;
        this.battery = battery;
    }

    @Override
    public long getTimeToChargeMillis(){
        final int thresholdPercentage = 85;
        double hoursToCharge;

        if (battery.RemainingEnergyPercents < thresholdPercentage){
            hoursToCharge = getLinearDependencyTime(thresholdPercentage);
            hoursToCharge += getFinishChargingTime(thresholdPercentage);
        } else {
            hoursToCharge = getFinishChargingTime(battery.RemainingEnergyPercents);
        }

        Double msToCharge = hoursToCharge * 3600 * 1000;
        return msToCharge.isInfinite() ? 0 : msToCharge.longValue();
    }

    private int getChargingPowerWh() {
        return powerLine.Voltage * powerLine.Amperage;
    }

    private double getChargingEfficiency() {
        return 100 / (100 + battery.ChargingLoss);
    }

    private double getLinearDependencyTime(int maxPercentage) {
        double WhToCharge = battery.UsefulCapacityKWh * 1000 * (maxPercentage - battery.RemainingEnergyPercents) / 100;
        return WhToCharge / (getChargingPowerWh() * getChargingEfficiency());
    }

    private double getFinishChargingTime(int currentPercentage) {
        double WhToCharge = battery.UsefulCapacityKWh * 1000 * (100 - currentPercentage) / 100;
        return WhToCharge / (getChargingPowerWh() * 0.55 * getChargingEfficiency());
    }
}

