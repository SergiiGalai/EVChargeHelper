package com.example.dell.chargehelper.controls;

import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class StepNumberPicker
{
    private final NumberPicker picker;
    private String[] values;

    public StepNumberPicker(NumberPicker picker) {
        this.picker = picker;
    }

    public void setValues(List<String> values){
        String[] arr = values.toArray(new String[values.size()]);
        setValues(arr);
    }

    public void setValues(String[] values){
        this.values = values;
        picker.setDisplayedValues(null);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setDisplayedValues(values);
    }

    public void setOnValueChangedListener(NumberPicker.OnValueChangeListener listener){
        picker.setOnValueChangedListener(listener);
    }

    public String getValue(){
        if (values == null)
            throw new IllegalArgumentException("Allowed values not set");

        int index = picker.getValue();
        return values[index];
    }
    public void setValue(String value){
        if (values == null)
            throw new IllegalArgumentException("Allowed values not set");

        int index = getValueIndex(value);
        if (index < 0)
            throw new IllegalArgumentException("Passed value " + value + " out of range");

        picker.setValue(index);
    }

    private int getValueIndex(String s) {
        for (int i = 0; i < values.length; i++)
        {
            String item = values[i];
            if (item.equals(s))
                return i;
        }
        return -1;
    }
}
