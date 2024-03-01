package com.chebuso.chargetimer.charge;

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

        values.add("22");
        values.add("32");
        values.add("64");
        return values;
    }

    private static List<String> getAllowedAmperageForPublicChargers(int defaultAmperage) {
        List<String> values = generateSequence(8, MAX_HOME_SOCKET_AMPERAGE, 4);
        values.add(String.valueOf(defaultAmperage));

        final int MAX_STEPS = 8;

        int minAmperage = defaultAmperage / 2;
        if (minAmperage < MAX_HOME_SOCKET_AMPERAGE)
            minAmperage = MAX_HOME_SOCKET_AMPERAGE;

        final int maxAmperage = defaultAmperage * 2;
        final int step = (maxAmperage - minAmperage) / MAX_STEPS;

        List<String> additionalValues = generateSequence(minAmperage, maxAmperage, step);

        for (String tmpValue : additionalValues) {
            if (!values.contains(tmpValue))
                values.add(tmpValue);
        }
        return values;
    }

    public static List<String> getAllowedVoltage(int defaultVoltage){
        final int VOLTAGE_STEP = 5;

        int delta = getMinMaxVoltageDelta(defaultVoltage);
        if (delta > defaultVoltage) delta = defaultVoltage;

        final int min = defaultVoltage - delta;
        final int max = defaultVoltage + delta;

        return getAllowedVoltages(min, max, VOLTAGE_STEP);
    }

    private static int getMinMaxVoltageDelta(int defaultVoltage){
        final int MAX_US_VOLTAGE = 140;
        final int US_DELTA = 20;
        final int DEFAULT_DELTA = 40;

        final boolean isUSVoltage = defaultVoltage < MAX_US_VOLTAGE;
        return isUSVoltage ? US_DELTA : DEFAULT_DELTA;
    }

    private static List<String> getAllowedVoltages(int minVoltage, int maxVoltage, int step){
        return generateSequence(minVoltage, maxVoltage, step);
    }

    private static List<String> generateSequence(int min, int max, int step){
        List<String> allowedValues = new ArrayList<>();

        for (int i = min; i <= max; i += step){
            allowedValues.add(String.valueOf(i));
        }

        return allowedValues;
    }
}
