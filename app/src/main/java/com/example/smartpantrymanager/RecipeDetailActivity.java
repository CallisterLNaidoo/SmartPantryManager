package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtRecipeDetailName;
    private TextView txtRecipeIngredients;
    private TextView txtRecipeSteps;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        txtRecipeDetailName =
                findViewById(R.id.txtRecipeDetailName);

        txtRecipeIngredients =
                findViewById(R.id.txtRecipeIngredients);

        txtRecipeSteps =
                findViewById(R.id.txtRecipeSteps);

        databaseHelper = new DatabaseHelper(this);

        int recipeId =
                getIntent().getIntExtra("recipe_id", -1);

        String recipeName =
                getIntent().getStringExtra("recipe_name");

        String recipeSteps =
                getIntent().getStringExtra("recipe_steps");

        txtRecipeDetailName.setText(recipeName);

        txtRecipeSteps.setText(recipeSteps);

        String ingredients =
                databaseHelper.getRecipeIngredientsText(recipeId);

        txtRecipeIngredients.setText(ingredients);
    }
}