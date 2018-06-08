package com.example.dell.chargehelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.dell.chargehelper.bugreport.BaseActivity;
import com.example.dell.chargehelper.charge.Battery;
import com.example.dell.chargehelper.charge.ChargeTimeCalculator;
import com.example.dell.chargehelper.charge.ChargeValuesProvider;
import com.example.dell.chargehelper.charge.PowerLine;
import com.example.dell.chargehelper.controls.StepNumberPicker;
import com.example.dell.chargehelper.helpers.TimeHelper;
import com.example.dell.chargehelper.notifications.CarChargedCalendarEventScheduler;
import com.example.dell.chargehelper.notifications.CarChargedDirectCalendarWriteScheduler;
import com.example.dell.chargehelper.notifications.ICarChargedNotificationScheduler;
import com.example.dell.chargehelper.notifications.NotificationSchedulerProvider;
import com.example.dell.chargehelper.notifications.PermissionUtils;

import java.util.Date;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
{

    private ChargeTimeCalculator timeCalculator = new ChargeTimeCalculator();
    private ViewModel viewModel = new ViewModel();

    private SeekBar remainingEnergySeekBar;
    private TextView remainingEnergyTitle;
    private StepNumberPicker amperagePicker;
    private StepNumberPicker voltagePicker;
    private TextView chargedInTitle;
    private Button remindButton;


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
        private final PowerLine powerLine = new PowerLine();
        private final Battery battery = new Battery();

        void refresh() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

            powerLine.Amperage = Integer.valueOf(amperagePicker.getValue());
            powerLine.Voltage = Integer.valueOf(voltagePicker.getValue());
            battery.RemainingEnergyPercents = remainingEnergySeekBar.getProgress() * 5;
            battery.UsefulCapacityKWh = Double.parseDouble(preferences.getString("battery_capacity", SettingsActivity.DEFAULT_CAPACITY));
            battery.ChargingLoss = Integer.parseInt(preferences.getString("charging_loss", SettingsActivity.DEFAULT_CHARGING_LOSS));
            millisToCharge = timeCalculator.calculateTimeInMsToCharge(powerLine, battery);

            Date dateChargedAt = TimeHelper.toDate(TimeHelper.addToNow(millisToCharge));
            Time time = TimeHelper.getHoursAndMinutes(millisToCharge);

            remainingEnergyText = String.format(getString(R.string.remaining_energy_title), battery.RemainingEnergyPercents);
            chargedInText = String.format(getString(R.string.should_be_charged_prefix_title), time.hours, time.minutes);
            remindButtonText = String.format(getString(R.string.remind_me_button_title), TimeHelper.formatAsHoursWithMinutes(dateChargedAt));
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
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
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
                NotificationSchedulerProvider provider = new NotificationSchedulerProvider();
                for (ICarChargedNotificationScheduler scheduler : provider.getNotificationSchedulers(MainActivity.this)){
                    scheduler.scheduleNotification(viewModel.getMillisToCharge());
                }
            }
        });
    }

    private void initializeVariables() {
        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar);
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle);
        chargedInTitle = findViewById(R.id.chargedInTitle);
        remindButton = findViewById(R.id.remindButton);

        NumberPicker tmpAmperagePicker = findViewById(R.id.amperageValue);
        NumberPicker tmpVoltagePicker = findViewById(R.id.voltageValue);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        Integer defaultAmperage = Integer.parseInt(preferences.getString("default_amperage", SettingsActivity.DEFAULT_AMPERAGE));
        amperagePicker = new StepNumberPicker(tmpAmperagePicker);
        amperagePicker.setValues(ChargeValuesProvider.getAllowedAmperage(defaultAmperage));
        amperagePicker.setValue(String.valueOf(defaultAmperage));

        Integer defaultVoltage = Integer.parseInt(preferences.getString("default_voltage", SettingsActivity.DEFAULT_VOLTAGE));
        voltagePicker = new StepNumberPicker(tmpVoltagePicker);
        voltagePicker.setValues(ChargeValuesProvider.getAllowedVoltage(defaultVoltage));
        voltagePicker.setValue(String.valueOf(defaultVoltage));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CarChargedDirectCalendarWriteScheduler.REQUEST_CALENDAR){
            if(PermissionUtils.verifyPermissions(grantResults)){
                tryScheduleGoogleCalendar();
            }else{
                Snackbar.make(findViewById(R.id.main_layout), R.string.calendar_permissions_not_granted, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void tryScheduleGoogleCalendar() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (preferences.getBoolean("allow_calendar_notifications", SettingsActivity.DEFAULT_ALLOW_CALENDAR_NOTIFICATIONS)){
            CarChargedCalendarEventScheduler scheduler = new CarChargedCalendarEventScheduler(MainActivity.this);
            long msToCharge = viewModel.getMillisToCharge();
            scheduler.scheduleNotification(msToCharge);
        }
    }

    private void showSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
