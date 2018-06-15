package com.example.dell.chargehelper.charge;

import java.util.ArrayList;
import java.util.List;

public class ChargeValuesProvider {

    public static List<String> getAllowedAmperage(int defaultAmperage){
        List<String> values = generateSequence(6, 16, 2);

        if (!values.contains(String.valueOf(defaultAmperage)))
            values.add(String.valueOf(defaultAmperage));

        values.add("30");
        values.add("32");

        return values;
    }

    public static List<String> getAllowedVoltage(int defaultVoltage){
        return getAllowedVoltage(defaultVoltage, 40);
    }

    public static List<String> getAllowedVoltage(int defaultVoltage, int delta){
        if (defaultVoltage < delta)
            delta = defaultVoltage;

        int min = defaultVoltage - delta;
        int max = defaultVoltage + delta;
        return generateSequence(min, max, 5);
    }

    private static List<String> generateSequence(int min, int max, int step){
        List<String> allowedValues = new ArrayList<>();

        for (int i = min; i <= max; i += step){
            allowedValues.add(String.valueOf(i));
        }

        return allowedValues;
    }
}
