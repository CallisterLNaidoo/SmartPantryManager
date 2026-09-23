package com.example.smartpantrymanager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchExpiryReminders;

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        switchExpiryReminders =
                findViewById(R.id.switchExpiryReminders);

        SharedPreferences preferences =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean expiryRemindersEnabled =
                preferences.getBoolean(KEY_EXPIRY_REMINDERS, true);

        switchExpiryReminders.setChecked(expiryRemindersEnabled);

        switchExpiryReminders.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    preferences.edit()
                            .putBoolean(KEY_EXPIRY_REMINDERS, isChecked)
                            .apply();
                }
        );
    }
}