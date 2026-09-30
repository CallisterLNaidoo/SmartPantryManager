package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchExpiryReminders;

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // KEEP CONTENT CLEAR OF STATUS BAR AND CAMERA CUTOUT
        View mainView = findViewById(R.id.main);

        int originalLeft = mainView.getPaddingLeft();
        int originalTop = mainView.getPaddingTop();
        int originalRight = mainView.getPaddingRight();
        int originalBottom = mainView.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(
                mainView,
                (view, windowInsets) -> {

                    Insets insets = windowInsets.getInsets(
                            WindowInsetsCompat.Type.statusBars()
                                    | WindowInsetsCompat.Type.displayCutout()
                    );

                    view.setPadding(
                            originalLeft,
                            originalTop + insets.top,
                            originalRight,
                            originalBottom
                    );

                    return windowInsets;
                }
        );

        ViewCompat.requestApplyInsets(mainView);

        // SETTINGS PREFERENCES
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

        // BOTTOM NAVIGATION
        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.navSettings);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navPantry) {

                Intent intent = new Intent(
                        SettingsActivity.this,
                        MainActivity.class
                );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);
                return true;

            } else if (itemId == R.id.navRecipes) {

                Intent intent = new Intent(
                        SettingsActivity.this,
                        SuggestedRecipesActivity.class
                );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);
                return true;

            } else if (itemId == R.id.navSettings) {

                return true;
            }

            return false;
        });
    }
}

