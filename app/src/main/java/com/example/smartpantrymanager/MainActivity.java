package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private Button btnSettings;

    private ListView listPantry;
    private TextView txtExpiryReminders;

    private DatabaseHelper databaseHelper;
    private IngredientAdapter ingredientAdapter;
    private List<Ingredient> ingredientList;

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSettings = findViewById(R.id.btnSettings);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);

        listPantry = findViewById(R.id.listPantry);

        txtExpiryReminders = findViewById(R.id.txtExpiryReminders);

        // BOTTOM NAVIGATION
        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.navPantry);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navPantry) {

                return true;

            } else if (itemId == R.id.navRecipes) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SuggestedRecipesActivity.class
                );

                startActivity(intent);
                return true;

            } else if (itemId == R.id.navSettings) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SettingsActivity.class
                );

                startActivity(intent);
                return true;
            }

            return false;
        });

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedRecipesIfNeeded();

        // ADD INGREDIENT BUTTON
        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(intent);
        });

        // SUGGESTED RECIPES BUTTON
        btnSuggestedRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // SETTINGS BUTTON
        btnSettings.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });

        // OPEN INGREDIENT FOR EDITING
        listPantry.setOnItemClickListener((parent, view, position, id) -> {

            Ingredient selectedIngredient = ingredientList.get(position);

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            intent.putExtra(
                    "ingredient_id",
                    selectedIngredient.getId()
            );

            intent.putExtra(
                    "ingredient_name",
                    selectedIngredient.getName()
            );

            intent.putExtra(
                    "ingredient_quantity",
                    selectedIngredient.getQuantity()
            );

            intent.putExtra(
                    "ingredient_unit",
                    selectedIngredient.getUnit()
            );

            intent.putExtra(
                    "ingredient_expiry",
                    selectedIngredient.getExpiryDate()
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.navPantry);

        loadIngredients();
    }


    // LOAD PANTRY INGREDIENTS
    private void loadIngredients() {

        ingredientList = databaseHelper.getAllIngredients();

        ingredientAdapter = new IngredientAdapter(
                this,
                ingredientList
        );

        listPantry.setAdapter(ingredientAdapter);

        // REFRESH REMINDERS WHEN PANTRY OPENS
        updateExpiryReminders();
    }

    // CHECK EXPIRY DATES AND SETTINGS
    private void updateExpiryReminders() {

        SharedPreferences preferences =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean remindersEnabled =
                preferences.getBoolean(KEY_EXPIRY_REMINDERS, true);

        // HIDE REMINDERS IF DISABLED IN SETTINGS
        if (!remindersEnabled) {

            txtExpiryReminders.setVisibility(View.GONE);
            return;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateFormat.setLenient(false);

        // GET TODAY'S DATE WITHOUT THE TIME
        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        // CALCULATE THE DATE 7 DAYS FROM NOW
        Calendar sevenDaysLater = (Calendar) today.clone();

        sevenDaysLater.add(Calendar.DAY_OF_MONTH, 7);

        Date todayDate = today.getTime();
        Date reminderLimit = sevenDaysLater.getTime();

        StringBuilder reminderText = new StringBuilder();

        int reminderCount = 0;

        // CHECK EACH PANTRY INGREDIENT
        for (Ingredient ingredient : ingredientList) {

            String expiryDate = ingredient.getExpiryDate();

            // SKIP INGREDIENTS WITHOUT AN EXPIRY DATE
            if (expiryDate == null || expiryDate.trim().isEmpty()) {
                continue;
            }

            // SKIP OLD RECORDS WITH INVALID DATE FORMATS
            if (!expiryDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                continue;
            }

            try {

                Date expiry = dateFormat.parse(expiryDate);

                if (expiry == null) {
                    continue;
                }

                String ingredientName = ingredient.getName();

                // ALREADY EXPIRED
                if (expiry.before(todayDate)) {

                    reminderText.append("EXPIRED: ")
                            .append(ingredientName)
                            .append(" (")
                            .append(expiryDate)
                            .append(")\n");

                    reminderCount++;

                    // EXPIRES TODAY
                } else if (expiry.equals(todayDate)) {

                    reminderText.append("EXPIRES TODAY: ")
                            .append(ingredientName)
                            .append("\n");

                    reminderCount++;

                    // EXPIRES WITHIN THE NEXT 7 DAYS
                } else if (!expiry.after(reminderLimit)) {

                    reminderText.append("EXPIRING SOON: ")
                            .append(ingredientName)
                            .append(" (")
                            .append(expiryDate)
                            .append(")\n");

                    reminderCount++;
                }

            } catch (ParseException e) {

                // IGNORE INVALID DATES IN OLDER RECORDS
            }
        }

        // SHOW OR HIDE THE REMINDER BOX
        if (reminderCount > 0) {

            txtExpiryReminders.setText(
                    "Expiry Reminders\n\n" + reminderText.toString().trim()
            );

            txtExpiryReminders.setVisibility(View.VISIBLE);

        } else {

            txtExpiryReminders.setVisibility(View.GONE);
        }
    }
}
