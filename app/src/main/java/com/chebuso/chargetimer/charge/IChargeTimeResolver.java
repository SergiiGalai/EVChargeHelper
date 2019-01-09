package com.chebuso.chargetimer.charge;

public interface IChargeTimeResolver
{
    long getMillisToCharge(byte remainingEnergyPct);
}
