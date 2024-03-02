package com.chebuso.chargetimer.settings.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chebuso.chargetimer.R
import com.chebuso.chargetimer.UserMessage.getSnackbar

/**
 * Android Design Settings: https://m2.material.io/design/platform-guidance/android-settings.html
 * Settings UI: https://developer.android.com/develop/ui/views/components/setting
 * Settings organization: https://developer.android.com/develop/ui/views/components/settings/organize-your-settings
 */
class SettingsActivity: AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager
            .beginTransaction()
            .replace(android.R.id.content, HeadersPreferenceFragment())
            .commit()

        val extras = intent.extras
        if (extras != null && extras.containsKey(EXTRA_LOAD_FRAGMENT_MESSAGE_ID)) {
            val messageId = extras.getInt(EXTRA_LOAD_FRAGMENT_MESSAGE_ID)
            getSnackbar(this, messageId).show()
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, ChargingPreferenceFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                resetActivityTitle()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment.
        val args = pref.extras
        if (pref.fragment == null)
            return false

        val fragment = supportFragmentManager.fragmentFactory
            .instantiate(classLoader, pref.fragment!!)
        fragment.arguments = args
        fragment.setTargetFragment(caller, 0)

        // Replace the existing Fragment with the new Fragment.
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, fragment)
            .addToBackStack(null)
            .commit()
        setActivityTitle(pref.title)
        return true
    }

    private fun setActivityTitle(value: CharSequence?) = setTitle(value)
    private fun resetActivityTitle() = setTitle(resources.getText(R.string.title_activity_settings))

    companion object {
        const val EXTRA_LOAD_FRAGMENT_MESSAGE_ID = "frgToLoad"
    }

}