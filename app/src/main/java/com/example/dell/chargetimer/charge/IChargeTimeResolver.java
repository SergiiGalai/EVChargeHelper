package com.example.dell.chargetimer.charge;

public interface IChargeTimeResolver
{
    long getMillisToCharge(byte remainingEnergyPct);
}
