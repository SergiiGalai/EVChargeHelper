package com.example.dell.chargetimer;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.chargetimer.helpers.PreferenceHelper;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity
{
    private static Preference.OnPreferenceChangeListener listSummaryToValueListener = new Preference.OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference)
            {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);

            } else
            {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isLargeScreen(this);
    }

    private static boolean isLargeScreen(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        int screenMask = configuration.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        return screenMask >= Configuration.SCREENLAYOUT_SIZE_LARGE
                || (screenMask == Configuration.SCREENLAYOUT_SIZE_NORMAL
                    && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || ChargingPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ChargingPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_charging);
            setHasOptionsMenu(true);

            PreferenceHelper.setChangeListenerAndTriggerChange(findPreference("charging_loss"), listSummaryToValueListener);
            PreferenceHelper.setChangeListenerAndTriggerChange(findPreference("battery_capacity"), listSummaryToValueListener);
            PreferenceHelper.setChangeListenerAndTriggerChange(findPreference("default_voltage"), listSummaryToValueListener);
            PreferenceHelper.setChangeListenerAndTriggerChange(findPreference("default_amperage"), listSummaryToValueListener);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            PreferenceHelper.setChangeListenerAndTriggerChange(findPreference("calendar_permission_reminder_minutes"), maxValueValidationChangeListener);
            PreferenceHelper.setChangeListenerAndTriggerChange(findPreference("app_notification_reminder_minutes"), maxValueValidationChangeListener);
        }

        private final static int maxReminderMinutes = 480;
        private final static Preference.OnPreferenceChangeListener maxValueValidationChangeListener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String newValueStr = (String) newValue;
                int newValueInt = Integer.parseInt(newValueStr.equals("") ? "0" : newValueStr);

                if( newValueInt >= 0 && newValueInt <= maxReminderMinutes){
                    preference.setSummary(newValueStr);
                    return true;
                }else{
                    String str = preference.getContext().getString(R.string.pref_title_reminder_validation_error);
                    String formatted = String.format(str, maxReminderMinutes);
                    Toast.makeText(preference.getContext(), formatted, Toast.LENGTH_LONG).show();
                    String actualValue = PreferenceHelper.getValue(preference);
                    preference.setSummary(actualValue);
                    return false;
                }
            }
        };

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home)
            {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
