package com.example.dell.chargetimer;

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

import com.example.dell.chargetimer.bugreport.BaseActivity;
import com.example.dell.chargetimer.cars.Car;
import com.example.dell.chargetimer.cars.CarsProvider;
import com.example.dell.chargetimer.charge.Battery;
import com.example.dell.chargetimer.charge.IChargeTimeResolver;
import com.example.dell.chargetimer.charge.LiionChargeTimeResolver;
import com.example.dell.chargetimer.charge.ChargeValuesProvider;
import com.example.dell.chargetimer.charge.PowerLine;
import com.example.dell.chargetimer.controls.StepNumberPicker;
import com.example.dell.chargetimer.helpers.TimeHelper;
import com.example.dell.chargetimer.notifications.GoogleCalendarAdvancedNotificator;
import com.example.dell.chargetimer.notifications.NotificationScheduler;
import com.example.dell.chargetimer.settings.ISettingsReader;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private final static int SETTINGS_REQUEST_CODE = 9000;

    private ViewModel viewModel = new ViewModel();

    private SeekBar remainingEnergySeekBar;
    private TextView remainingEnergyTitle;
    private StepNumberPicker amperagePicker;
    private StepNumberPicker voltagePicker;
    private TextView chargedInTitle;
    private Button remindButton;
    private ISettingsReader settingsProvider;
    private NotificationScheduler scheduler;

    private void initializeVariables() {
        CarsProvider carsProvider = new CarsProvider(this);
        ArrayList<Car> cars = carsProvider.Get();
        int len = cars.size();

        settingsProvider = Factory.createSettingsReader(this);
        scheduler = Factory.createScheduler(this);

        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar);
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle);
        chargedInTitle = findViewById(R.id.chargedInTitle);
        remindButton = findViewById(R.id.remindButton);

        Integer defaultAmperage = settingsProvider.getDefaultAmperage();
        amperagePicker = new StepNumberPicker(this, R.id.amperageValue);
        amperagePicker.setValues(ChargeValuesProvider.getAllowedAmperage(defaultAmperage));
        amperagePicker.setValue(String.valueOf(defaultAmperage));

        Integer defaultVoltage = settingsProvider.getDefaultVoltage();
        voltagePicker = new StepNumberPicker(this, R.id.voltageValue);
        voltagePicker.setValues(ChargeValuesProvider.getAllowedVoltage(defaultVoltage));
        voltagePicker.setValue(String.valueOf(defaultVoltage));
    }

    private NumberPicker.OnValueChangeListener textWatcher = new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            updateControls();
        }
    };

    private class ViewModel{
        private String remainingEnergyText;
        private String chargedInText;
        private String remindButtonText;

        private long millisToCharge;
        private final PowerLine powerLine;
        private final Battery battery;
        private IChargeTimeResolver chargeTimeResolver;

        ViewModel() {
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
            remainingEnergyText = String.format(getString(R.string.remaining_energy_title), remainingEnergyPct, remainingEnergyKWt, battery.UsableCapacityKWh);

            millisToCharge = chargeTimeResolver.getMillisToCharge(remainingEnergyPct);
            Date dateChargedAt = TimeHelper.toDate(TimeHelper.now() + millisToCharge);
            Time time = TimeHelper.getHoursAndMinutes(millisToCharge);

            chargedInText = String.format(getString(R.string.should_be_charged_prefix_title), time.hours, time.minutes);
            remindButtonText = String.format(getString(R.string.remind_me_button_title), TimeHelper.formatAsHoursWithMinutes(dateChargedAt));
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

    private void updateControls()
    {
        viewModel.refresh();
        remainingEnergyTitle.setText(viewModel.getRemainingEnergyText());
        chargedInTitle.setText(viewModel.getChargedInText());
        remindButton.setText(viewModel.getRemindButtonText());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();
        updateControls();

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
                startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GoogleCalendarAdvancedNotificator.REQUEST_CALENDAR){
            scheduler.schedule(grantResults, viewModel.getMillisToCharge());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST_CODE){
            initializeVariables();
            updateControls();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
