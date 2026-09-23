package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        listSuggestedRecipes =
                findViewById(R.id.listSuggestedRecipes);

        txtNoRecipes =
                findViewById(R.id.txtNoRecipes);

        databaseHelper =
                new DatabaseHelper(this);

        listSuggestedRecipes.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Recipe selectedRecipe =
                            recipeList.get(position);

                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class
                    );

                    intent.putExtra(
                            "recipe_id",
                            selectedRecipe.getId()
                    );

                    intent.putExtra(
                            "recipe_name",
                            selectedRecipe.getName()
                    );

                    intent.putExtra(
                            "recipe_steps",
                            selectedRecipe.getSteps()
                    );

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

        recipeList =
                databaseHelper.getSuggestedRecipes();

        if (recipeList.isEmpty()) {

            txtNoRecipes.setVisibility(View.VISIBLE);
            listSuggestedRecipes.setVisibility(View.GONE);

        } else {

            txtNoRecipes.setVisibility(View.GONE);
            listSuggestedRecipes.setVisibility(View.VISIBLE);

            RecipeAdapter adapter =
                    new RecipeAdapter(
                            this,
                            recipeList
                    );

            listSuggestedRecipes.setAdapter(adapter);
        }
    }
}