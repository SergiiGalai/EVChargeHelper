package com.example.dell.chargetimer.controls;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.widget.NumberPicker;

import com.example.dell.chargetimer.helpers.Convert;

import java.util.List;

public class StepNumberPicker
{
    private final NumberPicker picker;
    private String[] values;

    public StepNumberPicker(Activity activity, @IdRes int viewId) {
        this((NumberPicker)activity.findViewById(viewId));
    }
    private StepNumberPicker(NumberPicker picker) {
        this.picker = picker;
    }

    public void setValues(List<String> values){
        setValues(Convert.toArray(values));
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
