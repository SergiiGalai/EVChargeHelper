package com.chebuso.chargetimer.main

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.settings.Factory
import com.chebuso.chargetimer.settings.ISettingsReader

class MainActivity : ComponentActivity() {

    private var remainingEnergySeekBar: SeekBar? = null
    private var remainingEnergyTitle: TextView? = null
    private var chargedInTitle: TextView? = null
    private var remindButton: Button? = null
    private var showCalendarsButton: Button? = null

    private var settingsProvider: ISettingsReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeVariables()
    }

    private fun initializeVariables(){
        settingsProvider = Factory.createSettingsReader(this)

        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar)
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle)
        chargedInTitle = findViewById(R.id.chargedInTitle)
        remindButton = findViewById(R.id.remindButton)
        showCalendarsButton = findViewById(R.id.showCalendarsButton)

    }

}
