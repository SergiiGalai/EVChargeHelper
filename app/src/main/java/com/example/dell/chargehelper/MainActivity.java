package com.example.dell.chargehelper;

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

import com.example.dell.chargehelper.bugreport.BaseActivity;
import com.example.dell.chargehelper.charge.Battery;
import com.example.dell.chargehelper.charge.IChargeTimeResolver;
import com.example.dell.chargehelper.charge.LiionChargeTimeResolver;
import com.example.dell.chargehelper.charge.ChargeValuesProvider;
import com.example.dell.chargehelper.charge.PowerLine;
import com.example.dell.chargehelper.controls.StepNumberPicker;
import com.example.dell.chargehelper.helpers.TimeHelper;
import com.example.dell.chargehelper.notifications.GoogleCalendarAdvancedNotificator;
import com.example.dell.chargehelper.notifications.INotificator;
import com.example.dell.chargehelper.notifications.NotificatorFactory;
import com.example.dell.chargehelper.helpers.PermissionHelper;

import java.util.Date;

public class MainActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback
{
    private ViewModel viewModel = new ViewModel();

    private SeekBar remainingEnergySeekBar;
    private TextView remainingEnergyTitle;
    private StepNumberPicker amperagePicker;
    private StepNumberPicker voltagePicker;
    private TextView chargedInTitle;
    private Button remindButton;
    private NotificatorFactory notificatorFactory;
    private ISettingsProvider settingsProvider;

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
            battery.UsefulCapacityKWh = settingsProvider.getBatteryCapacity();
            battery.ChargingLoss = settingsProvider.getChargingLossPct();
            byte remainingEnergyPct = getRemainingEnergyPercentage();
            millisToCharge = chargeTimeResolver.getMillisToCharge(remainingEnergyPct);

            Date dateChargedAt = TimeHelper.toDate(TimeHelper.addToNow(millisToCharge));
            Time time = TimeHelper.getHoursAndMinutes(millisToCharge);

            remainingEnergyText = String.format(getString(R.string.remaining_energy_title), remainingEnergyPct);
            chargedInText = String.format(getString(R.string.should_be_charged_prefix_title), time.hours, time.minutes);
            remindButtonText = String.format(getString(R.string.remind_me_button_title), TimeHelper.formatAsHoursWithMinutes(dateChargedAt));
        }

        private byte getRemainingEnergyPercentage(){
            return (byte) (remainingEnergySeekBar.getProgress() * 5);
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
                for (INotificator notificator : notificatorFactory.createNotificators(MainActivity.this)){
                    notificator.scheduleCarChargedNotification(viewModel.getMillisToCharge());
                }
            }
        });
    }

    private void initializeVariables() {
        settingsProvider = new SharedPreferenceSettingsProvider(this);
        notificatorFactory = new NotificatorFactory(settingsProvider);

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
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GoogleCalendarAdvancedNotificator.REQUEST_CALENDAR){
            INotificator notificator = notificatorFactory.tryCreate(PermissionHelper.isPermissionsGranted(grantResults), MainActivity.this);
            if (notificator != null)
                scheduleNotificator(notificator);
        }
    }

    private void scheduleNotificator(INotificator notificator) {
        long msToCharge = viewModel.getMillisToCharge();
        notificator.scheduleCarChargedNotification(msToCharge);
    }
}
