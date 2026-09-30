package com.example.smartpantrymanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnAddIngredient;

    private ListView listPantry;
    private TextView txtExpiryReminders;
    private TextView txtEmptyPantry;

    private DatabaseHelper databaseHelper;
    private IngredientAdapter ingredientAdapter;
    private List<Ingredient> ingredientList;

    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_REMINDERS = "expiry_reminders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(
                        getWindow(),
                        getWindow().getDecorView()
                );

        controller.setAppearanceLightStatusBars(true);

        // CONNECT VIEWS
        btnAddIngredient = findViewById(R.id.btnAddIngredient);

        listPantry = findViewById(R.id.listPantry);

        txtExpiryReminders = findViewById(R.id.txtExpiryReminders);
        txtEmptyPantry = findViewById(R.id.txtEmptyPantry);

        listPantry.setEmptyView(txtEmptyPantry);

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

        // DATABASE
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedRecipesIfNeeded();

        // ADD INGREDIENT
        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
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

    private void loadIngredients() {

        ingredientList = databaseHelper.getAllIngredients();

        ingredientAdapter = new IngredientAdapter(
                this,
                ingredientList
        );

        listPantry.setAdapter(ingredientAdapter);

        updateExpiryReminders();
    }

    private void updateExpiryReminders() {

        SharedPreferences preferences =
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean remindersEnabled =
                preferences.getBoolean(KEY_EXPIRY_REMINDERS, true);

        if (!remindersEnabled) {

            txtExpiryReminders.setVisibility(View.GONE);
            return;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dateFormat.setLenient(false);

        Calendar today = Calendar.getInstance();

        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar sevenDaysLater = (Calendar) today.clone();

        sevenDaysLater.add(Calendar.DAY_OF_MONTH, 7);

        Date todayDate = today.getTime();
        Date reminderLimit = sevenDaysLater.getTime();

        StringBuilder reminderText = new StringBuilder();

        int reminderCount = 0;

        for (Ingredient ingredient : ingredientList) {

            String expiryDate = ingredient.getExpiryDate();

            if (expiryDate == null || expiryDate.trim().isEmpty()) {
                continue;
            }

            if (!expiryDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                continue;
            }

            try {

                Date expiry = dateFormat.parse(expiryDate);

                if (expiry == null) {
                    continue;
                }

                String ingredientName = ingredient.getName();

                if (expiry.before(todayDate)) {

                    reminderText.append("EXPIRED: ")
                            .append(ingredientName)
                            .append(" (")
                            .append(expiryDate)
                            .append(")\n");

                    reminderCount++;

                } else if (expiry.equals(todayDate)) {

                    reminderText.append("EXPIRES TODAY: ")
                            .append(ingredientName)
                            .append("\n");

                    reminderCount++;

                } else if (!expiry.after(reminderLimit)) {

                    reminderText.append("EXPIRING SOON: ")
                            .append(ingredientName)
                            .append(" (")
                            .append(expiryDate)
                            .append(")\n");

                    reminderCount++;
                }

            } catch (ParseException e) {

                // Ignore invalid dates in older records
            }
        }

        if (reminderCount > 0) {

            txtExpiryReminders.setText(
                    getString(
                            R.string.expiry_reminder_message,
                            reminderText.toString().trim()
                    )
            );

            txtExpiryReminders.setVisibility(View.VISIBLE);

        } else {

            txtExpiryReminders.setVisibility(View.GONE);
        }
    }
}

