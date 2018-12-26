package com.sergiigalai.chargetimer.charge;

public class LiionChargeTimeResolver implements IChargeTimeResolver
{
    private static final short LINEAR_CHARGING_THRESHOLD_PCT = 80;
    private PowerLine powerLine;
    private Battery battery;

    public LiionChargeTimeResolver(PowerLine powerLine, Battery battery) {
        this.powerLine = powerLine;
        this.battery = battery;
    }

    @Override
    public long getMillisToCharge(byte remainingEnergyPct){
        double hoursToCharge;

        if (remainingEnergyPct < LINEAR_CHARGING_THRESHOLD_PCT){
            hoursToCharge = getLinearDependencyTime(LINEAR_CHARGING_THRESHOLD_PCT, remainingEnergyPct);
            hoursToCharge += getFinishChargingTime(LINEAR_CHARGING_THRESHOLD_PCT);
        } else {
            hoursToCharge = getFinishChargingTime(remainingEnergyPct);
        }

        return convertHoursToMs(hoursToCharge);
    }

    private long convertHoursToMs(double hours) {
        Double ms = hours * 3600 * 1000;
        return ms.isInfinite() ? 0 : ms.longValue();
    }

    private int getChargingPowerWh() {
        return powerLine.Voltage * powerLine.Amperage;
    }

    private double getChargingEfficiency() {
        return 100 / (100 + battery.ChargingLossPct);
    }

    private double getLinearDependencyTime(short maxPercentage, short currentPercentage) {
        return getEmptyBatteryChargeTime() *
                (maxPercentage - currentPercentage) / 100;
    }

    private double getFinishChargingTime(int currentPercentage) {
        final int ChargingAmperageSlowdown = 2;

        return getEmptyBatteryChargeTime() * ChargingAmperageSlowdown *
                (100 - currentPercentage) / 100;
    }

    private double getEmptyBatteryChargeTime() {
        double WhToCharge = battery.UsableCapacityKWh * 1000;
        return WhToCharge / (getChargingPowerWh() * getChargingEfficiency());
    }
}

