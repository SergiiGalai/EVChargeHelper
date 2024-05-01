package com.chebuso.chargetimer.main

import android.Manifest
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.charge.Battery
import com.chebuso.chargetimer.charge.ChargeValuesProvider
import com.chebuso.chargetimer.charge.PowerLine
import com.chebuso.chargetimer.charge.ViewModel
import com.chebuso.chargetimer.controls.StepNumberPicker
import com.chebuso.chargetimer.notifications.NotificationScheduler
import com.chebuso.chargetimer.permissions.PermissionActivityResultLauncher
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.shared.Factory

class MainActivityViewModel(
    private val activity: MainActivity,
    private val settingsReader: ISettingsReader,
) {
    private lateinit var viewModel: ViewModel
    private lateinit var notificationScheduler: NotificationScheduler

    private lateinit var remainingEnergySeekBar: SeekBar
    private lateinit var remainingEnergyTitle: TextView
    private lateinit var chargedInTitle: TextView
    private lateinit var remindButton: Button
    private lateinit var amperagePicker: StepNumberPicker
    private lateinit var voltagePicker: StepNumberPicker

    fun initializeVariables(){
        notificationScheduler = Factory.notificationScheduler(
            activity,
            PermissionActivityResultLauncher(activity, requestPermissionLauncherMultiple)
        )

        remainingEnergySeekBar = activity.findViewById(R.id.remainingEnergySeekBar)
        remainingEnergyTitle = activity.findViewById(R.id.remainingEnergyTitle)
        chargedInTitle = activity.findViewById(R.id.chargedInTitle)
        remindButton = activity.findViewById(R.id.remindButton)

        val defaultAmperage = settingsReader.getDefaultAmperage()
        amperagePicker = StepNumberPicker(
            activity, R.id.amperageValue,
            ChargeValuesProvider.getAllowedAmperage(defaultAmperage),
            defaultAmperage.toString()
        )

        val defaultVoltage = settingsReader.getDefaultVoltage()
        voltagePicker = StepNumberPicker(
            activity, R.id.voltageValue,
            ChargeValuesProvider.getAllowedVoltage(defaultVoltage),
            defaultVoltage.toString()
        )
    }

    fun updateControls(){
        Log.d(TAG, "updateControls")
        val powerLine = PowerLine(
            voltagePicker.value.toInt(),
            amperagePicker.value.toInt()
        )
        val battery = Battery(
            settingsReader.getBatteryCapacity(),
            settingsReader.getChargingLossPct(),
            remainingEnergyPercentage
        )
        viewModel = ViewModel(activity, powerLine, battery)
        remainingEnergyTitle.text = viewModel.remainingEnergyText
        chargedInTitle.text = viewModel.chargedInText
        remindButton.text = viewModel.remindButtonText
    }

    private val requestPermissionLauncherMultiple = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            Log.d(TAG, "$permission = $isGranted")
            if (permission == Manifest.permission.WRITE_CALENDAR){
                notificationScheduler.scheduleCalendar(isGranted, viewModel.millisToCharge)
            }
        }
    }

    private val remainingEnergyPercentage: Byte
        get() {
            val value = (remainingEnergySeekBar.progress * 5).toByte()
            Log.d(TAG, "remainingEnergyPercentage=$value")
            return value
        }

    fun initializeChangeListeners() {
        voltagePicker.setOnValueChangedListener(textWatcher)
        amperagePicker.setOnValueChangedListener(textWatcher)

        remainingEnergySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progressValue: Int, fromUser: Boolean) {
                Log.d(TAG, "remainingEnergySeekBar.onProgressChanged")
                updateControls()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        remindButton.setOnClickListener {
            Log.d(TAG, "remindButton.onClick")
            updateControls()
            notificationScheduler.scheduleAll(viewModel.millisToCharge)
        }
    }

    private val textWatcher = NumberPicker.OnValueChangeListener { numberPicker, _, _ ->
        Log.d(TAG, numberPicker.id.toString() + ".onValueChange")
        updateControls()
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}