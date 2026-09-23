package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private ListView listPantry;

    private DatabaseHelper databaseHelper;
    private IngredientAdapter ingredientAdapter;
    private List<Ingredient> ingredientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        listPantry = findViewById(R.id.listPantry);

        databaseHelper = new DatabaseHelper(this);
        databaseHelper.seedRecipesIfNeeded();

        // Add Ingredient button
        btnAddIngredient.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            startActivity(intent);
        });

        // Suggested Recipes button
        btnSuggestedRecipes.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        // Open ingredient for editing
        listPantry.setOnItemClickListener((parent, view, position, id) -> {

            Ingredient selectedIngredient = ingredientList.get(position);

            Intent intent = new Intent(
                    MainActivity.this,
                    AddIngredientActivity.class
            );

            intent.putExtra("ingredient_id", selectedIngredient.getId());
            intent.putExtra("ingredient_name", selectedIngredient.getName());
            intent.putExtra(
                    "ingredient_quantity",
                    selectedIngredient.getQuantity()
            );
            intent.putExtra("ingredient_unit", selectedIngredient.getUnit());
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

        loadIngredients();
    }

    private void loadIngredients() {

        ingredientList = databaseHelper.getAllIngredients();

        ingredientAdapter = new IngredientAdapter(
                this,
                ingredientList
        );

        listPantry.setAdapter(ingredientAdapter);
    }
}