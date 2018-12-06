package com.example.dell.chargetimer.charge;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChargeValuesProvider {
    private final static int MAX_HOME_SOCKET_AMPERAGE = 16;

    public static List<String> getAllowedAmperage(int defaultAmperage){

        List<String> values = defaultAmperage > MAX_HOME_SOCKET_AMPERAGE
            ? getAllowedAmperageForPublicChargers(defaultAmperage)
            : getAllowedAmperageForHomeSockets(defaultAmperage);

        Collections.sort(values, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });

        return values;
    }

    @NonNull
    private static List<String> getAllowedAmperageForHomeSockets(int defaultAmperage) {
        List<String> values;
        values = generateSequence(6, MAX_HOME_SOCKET_AMPERAGE, 2);

        if (!values.contains(String.valueOf(defaultAmperage)))
            values.add(String.valueOf(defaultAmperage));

        values.add("32");
        return values;
    }

    private static List<String> getAllowedAmperageForPublicChargers(int defaultAmperage) {
        List<String> values;
        values = generateSequence(8, MAX_HOME_SOCKET_AMPERAGE, 4);
        List<String> additionalValues = generateSequence(defaultAmperage - 10, defaultAmperage + 10, 2);

        for (String tmpValue : additionalValues) {
            if (!values.contains(tmpValue))
                values.add(tmpValue);
        }
        return values;
    }

    public static List<String> getAllowedVoltage(int defaultVoltage){
        final int MAX_US_VOLTAGE = 140;
        final int MAX_DELTA = 40;
        final int DELTA_US = 20;

        final int delta = defaultVoltage < MAX_DELTA
                ? defaultVoltage : defaultVoltage < MAX_US_VOLTAGE
                ? DELTA_US : MAX_DELTA;

        return getAllowedVoltage(defaultVoltage, delta, 10);
    }

    private static List<String> getAllowedVoltage(int defaultVoltage, int delta, int step){
        int min = defaultVoltage - delta;
        int max = defaultVoltage + delta;

        return generateSequence(min, max, step);
    }

    private static List<String> generateSequence(int min, int max, int step){
        List<String> allowedValues = new ArrayList<>();

        for (int i = min; i <= max; i += step){
            allowedValues.add(String.valueOf(i));
        }

        return allowedValues;
    }
}
