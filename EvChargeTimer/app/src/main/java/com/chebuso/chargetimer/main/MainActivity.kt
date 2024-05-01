package com.chebuso.chargetimer.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.NumberPicker.OnValueChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.chebuso.chargetimer.shared.Factory
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.shared.UserMessage
import com.chebuso.chargetimer.controls.BaseActivity
import com.chebuso.chargetimer.calendar.*
import com.chebuso.chargetimer.calendar.dal.CalendarRepository
import com.chebuso.chargetimer.calendar.dal.ICalendarRepository
import com.chebuso.chargetimer.charge.*
import com.chebuso.chargetimer.controls.StepNumberPicker
import com.chebuso.chargetimer.permissions.PermissionHelper
import com.chebuso.chargetimer.notifications.NotificationScheduler
import com.chebuso.chargetimer.permissions.PermissionActivityResultLauncher
import com.chebuso.chargetimer.settings.*
import com.chebuso.chargetimer.settings.ui.SettingsActivity
import com.google.android.material.snackbar.Snackbar


class MainActivity : BaseActivity() {
    private lateinit var remainingEnergySeekBar: SeekBar
    private lateinit var remainingEnergyTitle: TextView
    private lateinit var chargedInTitle: TextView
    private lateinit var remindButton: Button
    private lateinit var amperagePicker: StepNumberPicker
    private lateinit var voltagePicker: StepNumberPicker

    private lateinit var viewModel: ViewModel
    private lateinit var settingsReader: ISettingsReader
    private lateinit var notificationScheduler: NotificationScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeVariables()
        updateControls()
        initializeChangeListeners()

        if (settingsReader.firstApplicationRun())
            startChargingSettingsActivity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.settings -> {
                startSettingsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeVariables(){
        settingsReader = Factory.settingsReader(this)
        notificationScheduler = Factory.notificationScheduler(this,
            PermissionActivityResultLauncher(this, requestPermissionLauncherMultiple)
        )

        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar)
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle)
        chargedInTitle = findViewById(R.id.chargedInTitle)
        remindButton = findViewById(R.id.remindButton)

        val defaultAmperage = settingsReader.getDefaultAmperage()
        amperagePicker = StepNumberPicker(this, R.id.amperageValue,
            ChargeValuesProvider.getAllowedAmperage(defaultAmperage),
            defaultAmperage.toString())

        val defaultVoltage = settingsReader.getDefaultVoltage()
        voltagePicker = StepNumberPicker(this, R.id.voltageValue,
            ChargeValuesProvider.getAllowedVoltage(defaultVoltage),
            defaultVoltage.toString())
    }

    private fun updateControls(){
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
        viewModel = ViewModel(this, powerLine, battery)
        remainingEnergyTitle.text = viewModel.remainingEnergyText
        chargedInTitle.text = viewModel.chargedInText
        remindButton.text = viewModel.remindButtonText
    }

    private val requestPermissionLauncherMultiple = this.registerForActivityResult(
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

    private fun initializeChangeListeners() {
        voltagePicker.setOnValueChangedListener(textWatcher)
        amperagePicker.setOnValueChangedListener(textWatcher)

        remainingEnergySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progressValue: Int,
                fromUser: Boolean
            ) {
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

    private val textWatcher = OnValueChangeListener { numberPicker, _, _ ->
        Log.d(TAG, numberPicker.id.toString() + ".onValueChange")
        updateControls()
    }

    private fun startSettingsActivity() {
        Log.i(TAG, "start SettingsActivity")
        val intent = Intent(this, SettingsActivity::class.java)
        startSettingsActivityForResult.launch(intent)
    }

    private fun startChargingSettingsActivity() {
        Log.i(TAG, "start ChargingSettingsActivity")
        val intent = Intent(this, SettingsActivity::class.java)
        intent.putExtra(
            SettingsActivity.EXTRA_LOAD_FRAGMENT_MESSAGE_ID,
            R.string.first_time_settings_activity_message
        )
        startSettingsActivityForResult.launch(intent)
    }

    private val startSettingsActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                initializeVariables()
                updateControls()
                if (settingsReader.firstApplicationRun()){
                    Factory.settingsWriter(this).setFirstApplicationRunCompleted()
                    UserMessage.toMultilineSnackbar(
                        UserMessage.getSnackbar(this, R.string.first_time_main_activity_message),
                        4
                    ).show()
                }
            }
        }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
