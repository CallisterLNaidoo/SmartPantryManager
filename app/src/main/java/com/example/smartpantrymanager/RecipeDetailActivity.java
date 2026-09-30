package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView txtRecipeDetailName;
    private TextView txtRecipeIngredients;
    private TextView txtRecipeSteps;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

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

        // CONNECT VIEWS
        txtRecipeDetailName =
                findViewById(R.id.txtRecipeDetailName);

        txtRecipeIngredients =
                findViewById(R.id.txtRecipeIngredients);

        txtRecipeSteps =
                findViewById(R.id.txtRecipeSteps);

        databaseHelper = new DatabaseHelper(this);

        // RECEIVE RECIPE DETAILS
        int recipeId =
                getIntent().getIntExtra("recipe_id", -1);

        String recipeName =
                getIntent().getStringExtra("recipe_name");

        String recipeSteps =
                getIntent().getStringExtra("recipe_steps");

        // DISPLAY RECIPE NAME AND METHOD
        txtRecipeDetailName.setText(recipeName);
        txtRecipeSteps.setText(recipeSteps);

        // LOAD AND DISPLAY INGREDIENTS
        String ingredients =
                databaseHelper.getRecipeIngredientsText(recipeId);

        txtRecipeIngredients.setText(ingredients);
    }
}