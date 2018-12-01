package com.example.dell.chargetimer.charge;

public class LiionChargeTimeResolver implements IChargeTimeResolver
{
    private PowerLine powerLine;
    private Battery battery;

    public LiionChargeTimeResolver(PowerLine powerLine, Battery battery) {
        this.powerLine = powerLine;
        this.battery = battery;
    }

    @Override
    public long getMillisToCharge(byte remainingEnergyPct){
        final int linearThresholdPercentage = 80;
        double hoursToCharge;

        if (remainingEnergyPct < linearThresholdPercentage){
            hoursToCharge = getLinearDependencyTime(linearThresholdPercentage, remainingEnergyPct);
            hoursToCharge += getFinishChargingTime(linearThresholdPercentage);
        } else {
            hoursToCharge = getFinishChargingTime(remainingEnergyPct);
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

    private double getLinearDependencyTime(int maxPercentage, short remainingEnergyPct) {
        double WhToCharge = battery.UsefulCapacityKWh * 1000 * (maxPercentage - remainingEnergyPct) / 100;
        return WhToCharge / (getChargingPowerWh() * getChargingEfficiency());
    }

    private double getFinishChargingTime(int currentPercentage) {
        final int ChargingAmperageSlowdown = 2;
        double WhToCharge = battery.UsefulCapacityKWh * 1000 * (100 - currentPercentage) / 100;
        return ChargingAmperageSlowdown * WhToCharge / (getChargingPowerWh() * getChargingEfficiency());
    }
}

