package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private ListView listSuggestedRecipes;
    private TextView txtNoRecipes;

    private DatabaseHelper databaseHelper;
    private List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        listSuggestedRecipes = findViewById(R.id.listSuggestedRecipes);
        txtNoRecipes = findViewById(R.id.txtNoRecipes);

        databaseHelper = new DatabaseHelper(this);

        // BOTTOM NAVIGATION
        BottomNavigationView bottomNavigation =
                findViewById(R.id.bottomNavigation);

        bottomNavigation.setSelectedItemId(R.id.navRecipes);

        bottomNavigation.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.navPantry) {

                Intent intent = new Intent(
                        SuggestedRecipesActivity.this,
                        MainActivity.class
                );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);
                return true;

            } else if (itemId == R.id.navRecipes) {

                return true;

            } else if (itemId == R.id.navSettings) {

                Intent intent = new Intent(
                        SuggestedRecipesActivity.this,
                        SettingsActivity.class
                );

                startActivity(intent);
                return true;
            }

            return false;
        });

        // OPEN RECIPE DETAILS
        listSuggestedRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Recipe selectedRecipe = recipeList.get(position);

                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra("recipe_id", selectedRecipe.getId());
                    intent.putExtra("recipe_name", selectedRecipe.getName());
                    intent.putExtra("recipe_steps", selectedRecipe.getSteps());

                    startActivity(intent);
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        recipeList = databaseHelper.getSuggestedRecipes();

        if (recipeList.isEmpty()) {

            txtNoRecipes.setVisibility(View.VISIBLE);
            listSuggestedRecipes.setVisibility(View.GONE);

        } else {

            txtNoRecipes.setVisibility(View.GONE);
            listSuggestedRecipes.setVisibility(View.VISIBLE);

            RecipeAdapter adapter = new RecipeAdapter(this, recipeList);
            listSuggestedRecipes.setAdapter(adapter);
        }
    }
}
