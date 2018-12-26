package com.sergiigalai.chargetimer.charge;

public interface IChargeTimeResolver
{
    long getMillisToCharge(byte remainingEnergyPct);
}
