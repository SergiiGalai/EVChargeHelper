package com.chebuso.chargetimer;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chebuso.chargetimer.bugreport.BaseActivity;
import com.chebuso.chargetimer.charge.Battery;
import com.chebuso.chargetimer.charge.ChargeValuesProvider;
import com.chebuso.chargetimer.charge.IChargeTimeResolver;
import com.chebuso.chargetimer.charge.LiionChargeTimeResolver;
import com.chebuso.chargetimer.charge.PowerLine;
import com.chebuso.chargetimer.controls.StepNumberPicker;
import com.chebuso.chargetimer.helpers.TimeHelper;
import com.chebuso.chargetimer.notifications.CalendarAdvancedNotificator;
import com.chebuso.chargetimer.notifications.NotificationScheduler;
import com.chebuso.chargetimer.settings.ISettingsReader;

import java.util.Date;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private final static String TAG = "MainActivity";
    private final static int SETTINGS_REQUEST_CODE = 9000;

    private SeekBar remainingEnergySeekBar;
    private TextView remainingEnergyTitle;
    private StepNumberPicker amperagePicker;
    private StepNumberPicker voltagePicker;
    private TextView chargedInTitle;
    private Button remindButton;

    private ViewModel viewModel = new ViewModel(this);
    private ISettingsReader settingsProvider;
    private NotificationScheduler scheduler;

    private void initializeVariables() {
        settingsProvider = Factory.createSettingsReader(this);
        scheduler = Factory.createScheduler(this);

        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar);
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle);
        chargedInTitle = findViewById(R.id.chargedInTitle);
        remindButton = findViewById(R.id.remindButton);

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
                UserMessage.showMultilineSnackbar(this, R.string.first_time_main_activity_message, 4);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private class ViewModel{
        private Context context;
        private String remainingEnergyText;
        private String chargedInText;
        private String remindButtonText;

        private long millisToCharge;
        private final PowerLine powerLine;
        private final Battery battery;
        private IChargeTimeResolver chargeTimeResolver;

        ViewModel(Context context) {
            this.context = context;
            powerLine = new PowerLine();
            battery = new Battery();
            chargeTimeResolver = new LiionChargeTimeResolver(powerLine, battery);
        }

        void refresh() {
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
            return (byte) (remainingEnergySeekBar.getProgress() * 5);
        }

        private double getRemainingEnergyKWh(byte remainingEnergyPct){
            return battery.UsableCapacityKWh * remainingEnergyPct / 100;
        }

        long getMillisToCharge() { return millisToCharge; }
        String getRemainingEnergyText() { return remainingEnergyText; }
        String getChargedInText() { return chargedInText; }
        String getRemindButtonText() { return remindButtonText; }
    }

    private void initializeChangeListeners(){
        voltagePicker.setOnValueChangedListener(textWatcher);
        amperagePicker.setOnValueChangedListener(textWatcher);

        remainingEnergySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
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
                updateControls();
                scheduler.schedule(viewModel.getMillisToCharge());
            }
        });
    }

    private NumberPicker.OnValueChangeListener textWatcher = new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            updateControls();
        }
    };

    private void updateControls()
    {
        viewModel.refresh();
        remainingEnergyTitle.setText(viewModel.getRemainingEnergyText());
        chargedInTitle.setText(viewModel.getChargedInText());
        remindButton.setText(viewModel.getRemindButtonText());
    }

    private void startSettingsActivity(){
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, SETTINGS_REQUEST_CODE);
    }

    private void startChargingSettingsActivity(){
        Intent i = new Intent(this, SettingsActivity.class);
        i.putExtra(SettingsActivity.EXTRA_LOAD_FRAGMENT_MESSAGE_ID, R.string.first_time_settings_activity_message);
        startActivityForResult(i, SETTINGS_REQUEST_CODE);
    }
}
