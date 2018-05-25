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
import com.example.dell.chargehelper.charge.PowerLine;
import com.example.dell.chargehelper.notifications.CarChargedCalendarEventScheduler;
import com.example.dell.chargehelper.notifications.CarChargedDirectCalendarWriteScheduler;
import com.example.dell.chargehelper.notifications.ICarChargedNotificationScheduler;
import com.example.dell.chargehelper.notifications.NotificationSchedulerProvider;
import com.example.dell.chargehelper.notifications.PermissionUtils;

import java.util.Date;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private class ViewModel{
        private PowerLine powerLine = new PowerLine();
        private Battery battery = new Battery();

        PowerLine getPowerLine() {
            return powerLine;
        }

        Battery getBattery() {
            return battery;
        }
    }

    private NumberPicker.OnValueChangeListener textWatcher = new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                updateEditableViewModel();
                updateChargingHours();
        }
    };

    private ChargeTimeCalculator timeCalculator = new ChargeTimeCalculator();
    private ViewModel viewModel = new ViewModel();

    private SeekBar remainingEnergySeekBar;
    private TextView remainingEnergyTitle;
    private NumberPicker amperageText;
    private TextView chargedInTitle;
    private NumberPicker voltageText;
    private Button remindButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeVariables();
        updateEditableViewModel();
        updateChargingHours();

        voltageText.setOnValueChangedListener(textWatcher);
        amperageText.setOnValueChangedListener(textWatcher);

        remainingEnergySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                updateEditableViewModel();
                updateChargingHours();
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
                updateEditableViewModel();
                updateChargingHours();
                long msToCharge = calculateTimeToChargeMs();

                NotificationSchedulerProvider provider = new NotificationSchedulerProvider();
                for (ICarChargedNotificationScheduler scheduler : provider.getNotificationSchedulers(MainActivity.this)){
                    scheduler.scheduleNotification(msToCharge);
                }
            }
        });
    }

    private long calculateTimeToChargeMs() {
        return timeCalculator.calculateTimeInMsToCharge(viewModel.getPowerLine(), viewModel.getBattery());
    }

    private void updateEditableViewModel() {
        updatePowerLine(viewModel.getPowerLine());
        updateBattery(viewModel.getBattery());
    }

    private void updatePowerLine(PowerLine powerLine){
        powerLine.Voltage = voltageText.getValue();
        powerLine.Amperage = amperageText.getValue();
    }

    private void updateBattery(Battery battery){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        battery.RemainingEnergyPercents = remainingEnergySeekBar.getProgress() * 5;
        battery.UsefulCapacityKWh = Double.parseDouble(preferences.getString("battery_capacity", SettingsActivity.DEFAULT_CAPACITY));
        battery.ChargingLoss = Integer.parseInt(preferences.getString("charging_loss", SettingsActivity.DEFAULT_CHARGING_LOSS));
    }

    private void updateChargingHours()
    {
        long msToCharge = calculateTimeToChargeMs();
        Date dateChargedAt = TimeHelper.toDate(TimeHelper.addToNow(msToCharge));

        String remainingEnergyMessage = String.format(this.getString(R.string.remaining_energy_title), viewModel.getBattery().RemainingEnergyPercents);
        remainingEnergyTitle.setText(remainingEnergyMessage);

        Time time = TimeHelper.getHoursAndMinutes(msToCharge);
        chargedInTitle.setText(String.format(this.getString(R.string.should_be_charged_prefix_title), time.hours, time.minutes));
        remindButton.setText(String.format(this.getString(R.string.remind_me_button_title), TimeHelper.formatAsHoursWithMinutes(dateChargedAt)));
    }

    private void initializeVariables() {
        remainingEnergySeekBar = findViewById(R.id.remainingEnergySeekBar);
        remainingEnergyTitle = findViewById(R.id.remainingEnergyTitle);
        amperageText = findViewById(R.id.amperageText);
        chargedInTitle = findViewById(R.id.chargedInTitle);
        voltageText = findViewById(R.id.voltageText);
        remindButton = findViewById(R.id.remindButton);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        Integer defaultAmperage =Integer.parseInt(preferences.getString("default_amperage", SettingsActivity.DEFAULT_AMPERAGE));
        amperageText.setMinValue(6);
        amperageText.setMaxValue(defaultAmperage + 10);
        amperageText.setValue(defaultAmperage);

        Integer defaultVoltage = Integer.parseInt(preferences.getString("default_voltage", SettingsActivity.DEFAULT_VOLTAGE));
        voltageText.setMinValue(defaultVoltage-30);
        voltageText.setMaxValue(defaultVoltage+30);
        voltageText.setValue(defaultVoltage);
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
            long msToCharge = calculateTimeToChargeMs();
            scheduler.scheduleNotification(msToCharge);
        }
    }

    private void showSettings() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
