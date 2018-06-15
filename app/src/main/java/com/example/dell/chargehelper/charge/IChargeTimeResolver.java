package com.example.dell.chargehelper.charge;

public interface IChargeTimeResolver
{
    long getMillisToCharge(byte remainingEnergyPct);
}
