package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddIngredientActivity extends AppCompatActivity {

    private EditText txtIngredientName;
    private EditText txtQuantity;
    private EditText txtUnit;
    private EditText txtExpiryDate;

    private TextView txtFormTitle;

    private Button btnSaveIngredient;
    private Button btnDeleteIngredient;

    private DatabaseHelper databaseHelper;

    private int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        txtFormTitle = findViewById(R.id.txtFormTitle);
        txtIngredientName = findViewById(R.id.txtIngredientName);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtUnit = findViewById(R.id.txtUnit);
        txtExpiryDate = findViewById(R.id.txtExpiryDate);

        btnSaveIngredient = findViewById(R.id.btnSaveIngredient);
        btnDeleteIngredient = findViewById(R.id.btnDeleteIngredient);

        databaseHelper = new DatabaseHelper(this);

        ingredientId = getIntent().getIntExtra("ingredient_id", -1);

        if (ingredientId != -1) {

            txtFormTitle.setText("Edit Ingredient");
            btnSaveIngredient.setText("Update Ingredient");
            btnDeleteIngredient.setVisibility(View.VISIBLE);

            String name =
                    getIntent().getStringExtra("ingredient_name");

            double quantity =
                    getIntent().getDoubleExtra("ingredient_quantity", 0);

            String unit =
                    getIntent().getStringExtra("ingredient_unit");

            String expiry =
                    getIntent().getStringExtra("ingredient_expiry");

            txtIngredientName.setText(name);
            txtQuantity.setText(String.valueOf(quantity));
            txtUnit.setText(unit);
            txtExpiryDate.setText(expiry);
        }

        btnSaveIngredient.setOnClickListener(
                v -> saveOrUpdateIngredient()
        );

        btnDeleteIngredient.setOnClickListener(
                v -> deleteIngredient()
        );
    }

    private void saveOrUpdateIngredient() {

        String name =
                txtIngredientName.getText().toString().trim();

        String quantityText =
                txtQuantity.getText().toString().trim();

        String unit =
                txtUnit.getText().toString().trim();

        String expiryDate =
                txtExpiryDate.getText().toString().trim();

        // VALIDATE INGREDIENT NAME
        if (name.isEmpty()) {
            txtIngredientName.setError("Enter ingredient name");
            txtIngredientName.requestFocus();
            return;
        }

        // VALIDATE QUANTITY
        if (quantityText.isEmpty()) {
            txtQuantity.setError("Enter quantity");
            txtQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);

        } catch (NumberFormatException e) {
            txtQuantity.setError("Enter a valid quantity");
            txtQuantity.requestFocus();
            return;
        }

        if (Double.isNaN(quantity)
                || Double.isInfinite(quantity)
                || quantity <= 0) {

            txtQuantity.setError(
                    "Quantity must be a number greater than 0"
            );
            txtQuantity.requestFocus();
            return;
        }

        // VALIDATE UNIT
        if (unit.isEmpty()) {
            txtUnit.setError("Enter unit, e.g. pcs, g or ml");
            txtUnit.requestFocus();
            return;
        }

        // VALIDATE OPTIONAL EXPIRY DATE
        if (!expiryDate.isEmpty() && !isValidDate(expiryDate)) {

            txtExpiryDate.setError(
                    "Enter a valid date in yyyy-MM-dd format"
            );

            txtExpiryDate.requestFocus();
            return;
        }

        // ADD NEW INGREDIENT
        if (ingredientId == -1) {

            boolean inserted =
                    databaseHelper.addIngredient(
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            if (inserted) {

                Toast.makeText(
                        this,
                        "Ingredient saved successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to save ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } else {

            // UPDATE EXISTING INGREDIENT
            boolean updated =
                    databaseHelper.updateIngredient(
                            ingredientId,
                            name,
                            quantity,
                            unit,
                            expiryDate
                    );

            if (updated) {

                Toast.makeText(
                        this,
                        "Ingredient updated successfully",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

            } else {

                Toast.makeText(
                        this,
                        "Failed to update ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    // CHECK WHETHER THE DATE IS VALID
    private boolean isValidDate(String dateText) {

        // Check the required format first
        if (!dateText.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Do not automatically correct invalid dates
        dateFormat.setLenient(false);

        try {

            dateFormat.parse(dateText);
            return true;

        } catch (ParseException e) {

            return false;
        }
    }

    // DELETE EXISTING INGREDIENT
    private void deleteIngredient() {

        boolean deleted =
                databaseHelper.deleteIngredient(ingredientId);

        if (deleted) {

            Toast.makeText(
                    this,
                    "Ingredient deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to delete ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
