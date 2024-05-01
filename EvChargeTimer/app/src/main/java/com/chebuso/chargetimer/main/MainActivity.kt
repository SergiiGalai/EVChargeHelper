package com.chebuso.chargetimer.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.controls.BaseActivity
import com.chebuso.chargetimer.settings.ISettingsReader
import com.chebuso.chargetimer.settings.ui.SettingsActivity
import com.chebuso.chargetimer.shared.Factory
import com.chebuso.chargetimer.shared.UserMessage


class MainActivity : BaseActivity() {
    private lateinit var settingsReader: ISettingsReader
    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingsReader = Factory.settingsReader(this)
        mainActivityViewModel = MainActivityViewModel(this, settingsReader).apply {
            initializeVariables()
            updateControls()
            initializeChangeListeners()
        }
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

    private val startSettingsActivityForResult = this.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        mainActivityViewModel.initializeVariables()
        mainActivityViewModel.updateControls()
        if (settingsReader.firstApplicationRun()){
            Factory.settingsWriter(this).setFirstApplicationRunCompleted()
            UserMessage.toMultilineSnackbar(
                UserMessage.getSnackbar(this, R.string.first_time_main_activity_message),
                4
            ).show()
        }
    }

    companion object {
        private val TAG = this::class.java.simpleName
    }
}
