package com.example.smartpantrymanager;

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
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        List<Recipe> recipes =
                databaseHelper.getSuggestedRecipes();

        if (recipes.isEmpty()) {

            txtNoRecipes.setVisibility(View.VISIBLE);
            listSuggestedRecipes.setVisibility(View.GONE);

        } else {

            txtNoRecipes.setVisibility(View.GONE);
            listSuggestedRecipes.setVisibility(View.VISIBLE);

            RecipeAdapter adapter =
                    new RecipeAdapter(
                            this,
                            recipes
                    );

            listSuggestedRecipes.setAdapter(adapter);
        }
    }
}