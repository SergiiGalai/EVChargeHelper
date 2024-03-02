package com.chebuso.chargetimer.controls

import android.app.Activity
import android.view.View
import android.widget.NumberPicker
import android.widget.NumberPicker.OnValueChangeListener
import androidx.annotation.IdRes

class StepNumberPicker private constructor(private val picker: NumberPicker) {
    private var values: Array<String?>? = null

    constructor(
        activity: Activity,
        @IdRes viewId: Int,
        values: List<String?>,
        selectedValue: String
    ) : this(activity.findViewById<View>(viewId) as NumberPicker){
        setValues(values.toTypedArray())
        value = selectedValue
    }

    fun setValues(values: Array<String?>) {
        this.values = values
        picker.setDisplayedValues(null)
        picker.setMinValue(0)
        picker.setMaxValue(values.size - 1)
        picker.setDisplayedValues(values)
    }

    fun setOnValueChangedListener(listener: OnValueChangeListener?) {
        picker.setOnValueChangedListener(listener)
    }

    var value: String
        get() {
            requireNotNull(values) { "Allowed values not set" }
            val index = picker.value
            val result = values!![index]
            return result!!
        }
        set(value) {
            requireNotNull(values) { "Allowed values not set" }
            val index = getValueIndex(value)
            require(index >= 0) { "Passed value $value out of range" }
            picker.value = index
        }

    private fun getValueIndex(s: String): Int {
        for (i in values!!.indices) {
            val item: String? = values!![i]
            if (item != null && item == s) return i
        }
        return -1
    }
}
