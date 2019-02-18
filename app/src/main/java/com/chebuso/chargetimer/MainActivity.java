package com.chebuso.chargetimer;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chebuso.chargetimer.bugreport.BaseActivity;
import com.chebuso.chargetimer.charge.Battery;
import com.chebuso.chargetimer.charge.ChargeValuesProvider;
import com.chebuso.chargetimer.charge.IChargeTimeResolver;
import com.chebuso.chargetimer.charge.LiIonChargeTimeResolver;
import com.chebuso.chargetimer.charge.PowerLine;
import com.chebuso.chargetimer.controls.StepNumberPicker;
import com.chebuso.chargetimer.helpers.PermissionHelper;
import com.chebuso.chargetimer.helpers.TimeHelper;
import com.chebuso.chargetimer.models.CalendarEntity;
import com.chebuso.chargetimer.notifications.CalendarAdvancedNotificator;
import com.chebuso.chargetimer.repositories.CalendarRepository;
import com.chebuso.chargetimer.repositories.ICalendarRepository;
import com.chebuso.chargetimer.notifications.NotificationScheduler;
import com.chebuso.chargetimer.settings.ISettingsReader;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
{
    @SuppressWarnings("unused")
    private final static String TAG = "MainActivity";
    private final static int SETTINGS_REQUEST_CODE = 9000;

    private SeekBar remainingEnergySeekBar;
    private TextView remainingEnergyTitle;
    private StepNumberPicker amperagePicker;
    private StepNumberPicker voltagePicker;
    private TextView chargedInTitle;
    private Button remindButton;
    private Button showCalendarsButton;

    private final ViewModel viewModel = new ViewModel();
    private ISettingsReader settingsProvider;
    private NotificationScheduler scheduler;
    private ICalendarRepository calendarRepository;

    private void initializeVariables() {
        settingsProvider = Factory.createSettingsReader(this);
        scheduler = Factory.createScheduler(this);
        calendarRepository = new CalendarRepository(this);

        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar);
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle);
        chargedInTitle = findViewById(R.id.chargedInTitle);
        remindButton = findViewById(R.id.remindButton);
        showCalendarsButton = findViewById(R.id.showCalendarsButton);

        int defaultAmperage = settingsProvider.getDefaultAmperage();
        amperagePicker = new StepNumberPicker(this, R.id.amperageValue);
        amperagePicker.setValues(ChargeValuesProvider.getAllowedAmperage(defaultAmperage));
        amperagePicker.setValue(String.valueOf(defaultAmperage));

        int defaultVoltage = settingsProvider.getDefaultVoltage();
        voltagePicker = new StepNumberPicker(this, R.id.voltageValue);
        voltagePicker.setValues(ChargeValuesProvider.getAllowedVoltage(defaultVoltage));
        voltagePicker.setValue(String.valueOf(defaultVoltage));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();
        updateControls();
        initializeChangeListeners();

        if (settingsProvider.firstApplicationRun()){
            startChargingSettingsActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case R.id.settings:
                startSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CalendarAdvancedNotificator.REQUEST_CALENDAR){
            scheduler.schedule(grantResults, viewModel.getMillisToCharge());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST_CODE){
            initializeVariables();
            updateControls();
            if (settingsProvider.firstApplicationRun()){
                Factory.createSettingsWriter(this).setFirstApplicationRunCompleted();

                UserMessage.toMultilineSnackbar(
                        UserMessage.getSnackbar(this, R.string.first_time_main_activity_message),
                        4
                ).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ViewModel{
        private String remainingEnergyText;
        private String chargedInText;
        private String remindButtonText;

        private long millisToCharge;
        private final PowerLine powerLine;
        private final Battery battery;
        private final IChargeTimeResolver chargeTimeResolver;

        ViewModel() {
            powerLine = new PowerLine();
            battery = new Battery();
            chargeTimeResolver = new LiIonChargeTimeResolver(powerLine, battery);
        }

        void refresh() {
            Log.d(TAG, "ViewModel.refresh");
            powerLine.Amperage = Integer.valueOf(amperagePicker.getValue());
            powerLine.Voltage = Integer.valueOf(voltagePicker.getValue());
            battery.UsableCapacityKWh = settingsProvider.getBatteryCapacity();
            battery.ChargingLossPct = settingsProvider.getChargingLossPct();

            byte remainingEnergyPct = getRemainingEnergyPercentage();
            double remainingEnergyKWt = getRemainingEnergyKWh(remainingEnergyPct);
            remainingEnergyText = String.format(getString(R.string.remaining_energy_title),
                    remainingEnergyPct, remainingEnergyKWt, battery.UsableCapacityKWh);

            millisToCharge = chargeTimeResolver.getMillisToCharge(remainingEnergyPct);
            Date dateChargedAt = TimeHelper.toDate(TimeHelper.now() + millisToCharge);
            Time time = TimeHelper.toTime(millisToCharge);

            Log.d(TAG,"ViewModel.refresh." + time.toString());
            if (time.days > 0){
                chargedInText = String.format(getString(R.string.should_be_charged_in_days_title),
                        time.days, time.hours, time.minutes);
                remindButtonText = String.format(getString(R.string.remind_me_button_title),
                        TimeHelper.formatAsShortDateTime(dateChargedAt));
            } else {
                chargedInText = String.format(getString(R.string.should_be_charged_in_hours_title),
                        time.hours, time.minutes);
                remindButtonText = String.format(getString(R.string.remind_me_button_title),
                        TimeHelper.formatAsShortTime(dateChargedAt));
            }
        }

        private byte getRemainingEnergyPercentage(){
            byte value = (byte) (remainingEnergySeekBar.getProgress() * 5);
            Log.d(TAG, "ViewModel.getRemainingEnergyPercentage=" + value);
            return value;
        }

        private double getRemainingEnergyKWh(byte remainingEnergyPct){
            double value = battery.UsableCapacityKWh * remainingEnergyPct / 100;
            Log.d(TAG, "ViewModel.getRemainingEnergyKWh=" + value);
            return value;
        }

        long getMillisToCharge() { return millisToCharge; }
        String getRemainingEnergyText() { return remainingEnergyText; }
        String getChargedInText() { return chargedInText; }
        String getRemindButtonText() { return remindButtonText; }
    }

    private void initializeChangeListeners(){
        final Activity activity = this;

        voltagePicker.setOnValueChangedListener(textWatcher);
        amperagePicker.setOnValueChangedListener(textWatcher);

        remainingEnergySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                Log.d(TAG, "remainingEnergySeekBar.onProgressChanged");
                updateControls();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        remindButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "remindButton.onClick");
                updateControls();
                scheduler.schedule(viewModel.getMillisToCharge());
            }
        });

        showCalendarsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "showCalendarsButton.onClick");
                if (PermissionHelper.isFullCalendarPermissionsGranted(activity)){
                    deleteDebugCalendars();

                    List<CalendarEntity> calendars = calendarRepository.getAvailableCalendars();
                    String calendarsLog = calendarsToString(calendars);
                    int lineNumber = calendarsLog.length() / 20;

                    UserMessage.toMultilineSnackbar(
                            UserMessage.getSnackbar(activity, calendarsLog, Snackbar.LENGTH_INDEFINITE),
                            lineNumber
                    ).show();
                } else {
                    UserMessage.showToast(activity, R.string.error_no_primary_calendar, Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void deleteDebugCalendars(){
        Log.d(TAG, "deleteDebugCalendars");
        calendarRepository.deleteCalendar("Charge EV");
        calendarRepository.deleteCalendar("com.sergiigalai.chargeTimer");
    }

    private static String calendarsToString(List<CalendarEntity> calendars) {
        Log.d(TAG, "calendarsToString");

        StringBuilder sb = new StringBuilder();

        if (calendars.isEmpty()){
            sb.append("No calendars found");
        }else{
            for (CalendarEntity calendar : calendars) {
                sb.append(String.format(Locale.US,
                        "%d:name=%s, prim=%s, acc='%s', owner='%s', type='%s';  ",
                        calendar.id,
                        calendar.displayName,
                        calendar.isPrimary,
                        calendar.accountName,
                        calendar.ownerAccount,
                        calendar.accountType
                ));
            }
        }

        return sb.toString();
    }

    private final NumberPicker.OnValueChangeListener textWatcher = new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            Log.d(TAG, numberPicker.getId() + ".onValueChange");
            updateControls();
        }
    };

    private void updateControls()
    {
        Log.d(TAG, "updateControls");
        viewModel.refresh();
        remainingEnergyTitle.setText(viewModel.getRemainingEnergyText());
        chargedInTitle.setText(viewModel.getChargedInText());
        remindButton.setText(viewModel.getRemindButtonText());
    }

    private void startSettingsActivity(){
        Log.d(TAG, "startSettingsActivity");
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, SETTINGS_REQUEST_CODE);
    }

    private void startChargingSettingsActivity(){
        Log.d(TAG, "startChargingSettingsActivity");
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra(SettingsActivity.EXTRA_LOAD_FRAGMENT_MESSAGE_ID, R.string.first_time_settings_activity_message);
        startActivityForResult(i, SETTINGS_REQUEST_CODE);
    }
}
