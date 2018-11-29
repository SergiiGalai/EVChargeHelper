package com.example.dell.chargetimer.charge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChargeValuesProvider {

    public static List<String> getAllowedAmperage(int defaultAmperage){

        List<String> values;

        if (defaultAmperage > 16){
            values = generateSequence(8, 16, 4);
            List<String> additionalValues = generateSequence(defaultAmperage - 10, defaultAmperage + 10, 2);

            for (String tmpValue : additionalValues) {
                if (!values.contains(tmpValue))
                    values.add(tmpValue);
            }
        }else{
            values = generateSequence(6, 16, 2);

            if (!values.contains(String.valueOf(defaultAmperage)))
                values.add(String.valueOf(defaultAmperage));

            values.add("32");
        }

        Collections.sort(values, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });

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
