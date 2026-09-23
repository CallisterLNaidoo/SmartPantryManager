package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createPantryTable =
                "CREATE TABLE " + TABLE_PANTRY + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT" +
                        ")";

        db.execSQL(createPantryTable);

        createRecipeTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            createRecipeTables(db);
        }
    }

    // CREATE INGREDIENT
    public boolean addIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        long result = db.insert(
                TABLE_PANTRY,
                null,
                values
        );

        return result != -1;
    }

    // READ INGREDIENTS
    public List<Ingredient> getAllIngredients() {

        List<Ingredient> ingredientList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PANTRY + " ORDER BY name ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                String expiryDate = cursor.getString(
                        cursor.getColumnIndexOrThrow("expiry_date")
                );

                Ingredient ingredient = new Ingredient(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );

                ingredientList.add(ingredient);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return ingredientList;
    }

    // UPDATE INGREDIENT
    public boolean updateIngredient(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("quantity", quantity);
        values.put("unit", unit);
        values.put("expiry_date", expiryDate);

        int result = db.update(
                TABLE_PANTRY,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // DELETE INGREDIENT
    public boolean deleteIngredient(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_PANTRY,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // CREATE RECIPE TABLES
    private void createRecipeTables(SQLiteDatabase db) {

        String createRecipesTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "steps TEXT NOT NULL" +
                        ")";

        String createRecipeIngredientsTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_RECIPE_INGREDIENTS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL" +
                        ")";

        db.execSQL(createRecipesTable);
        db.execSQL(createRecipeIngredientsTable);
    }

    private long insertRecipe(
            SQLiteDatabase db,
            String name,
            String steps) {

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("steps", steps);

        return db.insert(
                TABLE_RECIPES,
                null,
                values
        );
    }

    private void insertRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        ContentValues values = new ContentValues();

        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName);
        values.put("quantity", quantity);
        values.put("unit", unit);

        db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }

    // ADD STARTER RECIPES
    public void seedRecipesIfNeeded() {

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_RECIPES,
                null
        );

        int recipeCount = 0;

        if (cursor.moveToFirst()) {
            recipeCount = cursor.getInt(0);
        }

        cursor.close();

        if (recipeCount > 0) {
            return;
        }

        long recipeId;

        // 1. Scrambled Eggs
        recipeId = insertRecipe(
                db,
                "Scrambled Eggs",
                "Beat the eggs. Melt butter in a pan. Add the eggs and cook while stirring until soft. Add salt."
        );

        insertRecipeIngredient(db, recipeId, "egg", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");
        insertRecipeIngredient(db, recipeId, "salt", 1, "g");

        // 2. Cheese Omelette
        recipeId = insertRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs. Melt butter in a pan. Add the eggs and cook until almost set. Add cheese, fold and serve."
        );

        insertRecipeIngredient(db, recipeId, "egg", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "cheese", 50, "g");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 3. Tomato Omelette
        recipeId = insertRecipe(
                db,
                "Tomato Omelette",
                "Beat the eggs. Chop the tomato. Cook the tomato briefly in butter, add the eggs and cook until set."
        );

        insertRecipeIngredient(db, recipeId, "egg", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "tomato", 1, "pcs");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 4. Cheese Toast
        recipeId = insertRecipe(
                db,
                "Cheese Toast",
                "Butter the bread, add cheese and toast until the bread is crisp and the cheese has melted."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "slices");
        insertRecipeIngredient(db, recipeId, "cheese", 40, "g");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 5. Tomato Toast
        recipeId = insertRecipe(
                db,
                "Tomato Toast",
                "Toast the bread. Slice the tomato and place it on the toast. Add butter before serving."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "slices");
        insertRecipeIngredient(db, recipeId, "tomato", 1, "pcs");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 6. Egg Toast
        recipeId = insertRecipe(
                db,
                "Egg Toast",
                "Toast the bread. Fry the egg in butter and place it on top of the toast."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "slices");
        insertRecipeIngredient(db, recipeId, "egg", 1, "pcs");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 7. Simple Tomato Pasta
        recipeId = insertRecipe(
                db,
                "Simple Tomato Pasta",
                "Boil the pasta. Cook chopped tomato in oil. Add the pasta, season with salt and mix."
        );

        insertRecipeIngredient(db, recipeId, "pasta", 100, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "oil", 10, "ml");
        insertRecipeIngredient(db, recipeId, "salt", 1, "g");

        // 8. Cheesy Pasta
        recipeId = insertRecipe(
                db,
                "Cheesy Pasta",
                "Boil the pasta. Melt butter, add milk and cheese, then mix the sauce through the pasta."
        );

        insertRecipeIngredient(db, recipeId, "pasta", 100, "g");
        insertRecipeIngredient(db, recipeId, "cheese", 50, "g");
        insertRecipeIngredient(db, recipeId, "milk", 50, "ml");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 9. Tomato Rice
        recipeId = insertRecipe(
                db,
                "Tomato Rice",
                "Cook the rice. Fry chopped tomato in oil, add the rice and season with salt."
        );

        insertRecipeIngredient(db, recipeId, "rice", 100, "g");
        insertRecipeIngredient(db, recipeId, "tomato", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "oil", 10, "ml");
        insertRecipeIngredient(db, recipeId, "salt", 1, "g");

        // 10. Egg Fried Rice
        recipeId = insertRecipe(
                db,
                "Egg Fried Rice",
                "Cook the rice. Fry the eggs in oil, add the rice and season with salt."
        );

        insertRecipeIngredient(db, recipeId, "rice", 100, "g");
        insertRecipeIngredient(db, recipeId, "egg", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "oil", 10, "ml");
        insertRecipeIngredient(db, recipeId, "salt", 1, "g");

        // 11. Mashed Potatoes
        recipeId = insertRecipe(
                db,
                "Mashed Potatoes",
                "Boil the potatoes until soft. Mash them with milk and butter, then add salt."
        );

        insertRecipeIngredient(db, recipeId, "potato", 3, "pcs");
        insertRecipeIngredient(db, recipeId, "milk", 50, "ml");
        insertRecipeIngredient(db, recipeId, "butter", 20, "g");
        insertRecipeIngredient(db, recipeId, "salt", 1, "g");

        // 12. Cheesy Potatoes
        recipeId = insertRecipe(
                db,
                "Cheesy Potatoes",
                "Boil or bake the potatoes. Add butter and cheese while hot and allow the cheese to melt."
        );

        insertRecipeIngredient(db, recipeId, "potato", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "cheese", 50, "g");
        insertRecipeIngredient(db, recipeId, "butter", 10, "g");

        // 13. Banana Milkshake
        recipeId = insertRecipe(
                db,
                "Banana Milkshake",
                "Blend the banana, milk and sugar until smooth. Serve immediately."
        );

        insertRecipeIngredient(db, recipeId, "banana", 1, "pcs");
        insertRecipeIngredient(db, recipeId, "milk", 250, "ml");
        insertRecipeIngredient(db, recipeId, "sugar", 10, "g");

        // 14. Peanut Butter Banana Toast
        recipeId = insertRecipe(
                db,
                "Peanut Butter Banana Toast",
                "Toast the bread. Spread peanut butter on the toast and top with sliced banana."
        );

        insertRecipeIngredient(db, recipeId, "bread", 2, "slices");
        insertRecipeIngredient(db, recipeId, "peanut butter", 30, "g");
        insertRecipeIngredient(db, recipeId, "banana", 1, "pcs");

        // 15. Chicken Rice
        recipeId = insertRecipe(
                db,
                "Chicken Rice",
                "Cook the rice. Fry the chicken in oil until fully cooked, season with salt and serve with the rice."
        );

        insertRecipeIngredient(db, recipeId, "chicken", 150, "g");
        insertRecipeIngredient(db, recipeId, "rice", 100, "g");
        insertRecipeIngredient(db, recipeId, "oil", 10, "ml");
        insertRecipeIngredient(db, recipeId, "salt", 1, "g");
    }

    // STRICT RECIPE MATCHING
    public List<Recipe> getSuggestedRecipes() {

        List<Recipe> suggestedRecipes = new ArrayList<>();
        List<Ingredient> pantryItems = getAllIngredients();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor recipeCursor = db.rawQuery(
                "SELECT * FROM " + TABLE_RECIPES + " ORDER BY name ASC",
                null
        );

        if (recipeCursor.moveToFirst()) {

            do {

                int recipeId = recipeCursor.getInt(
                        recipeCursor.getColumnIndexOrThrow("id")
                );

                String recipeName = recipeCursor.getString(
                        recipeCursor.getColumnIndexOrThrow("name")
                );

                String steps = recipeCursor.getString(
                        recipeCursor.getColumnIndexOrThrow("steps")
                );

                boolean canMakeRecipe = true;

                Cursor ingredientCursor = db.rawQuery(
                        "SELECT * FROM " +
                                TABLE_RECIPE_INGREDIENTS +
                                " WHERE recipe_id = ?",
                        new String[]{String.valueOf(recipeId)}
                );

                if (ingredientCursor.moveToFirst()) {

                    do {

                        String requiredName =
                                ingredientCursor.getString(
                                        ingredientCursor.getColumnIndexOrThrow(
                                                "ingredient_name"
                                        )
                                );

                        double requiredQuantity =
                                ingredientCursor.getDouble(
                                        ingredientCursor.getColumnIndexOrThrow(
                                                "quantity"
                                        )
                                );

                        String requiredUnit =
                                ingredientCursor.getString(
                                        ingredientCursor.getColumnIndexOrThrow(
                                                "unit"
                                        )
                                );

                        if (!hasEnoughIngredient(
                                pantryItems,
                                requiredName,
                                requiredQuantity,
                                requiredUnit)) {

                            canMakeRecipe = false;
                            break;
                        }

                    } while (ingredientCursor.moveToNext());
                }

                ingredientCursor.close();

                if (canMakeRecipe) {

                    suggestedRecipes.add(
                            new Recipe(
                                    recipeId,
                                    recipeName,
                                    steps
                            )
                    );
                }

            } while (recipeCursor.moveToNext());
        }

        recipeCursor.close();

        return suggestedRecipes;
    }

    private boolean hasEnoughIngredient(
            List<Ingredient> pantryItems,
            String requiredName,
            double requiredQuantity,
            String requiredUnit) {

        double totalAvailable = 0;

        for (Ingredient pantryItem : pantryItems) {

            String pantryName =
                    normalizeIngredientName(pantryItem.getName());

            String recipeName =
                    normalizeIngredientName(requiredName);

            if (pantryName.equals(recipeName)) {

                double convertedQuantity =
                        convertQuantity(
                                pantryItem.getQuantity(),
                                pantryItem.getUnit(),
                                requiredUnit
                        );

                if (convertedQuantity >= 0) {
                    totalAvailable += convertedQuantity;
                }
            }
        }

        return totalAvailable >= requiredQuantity;
    }

    private String normalizeIngredientName(String name) {

        String result = name.toLowerCase().trim();

        if (result.endsWith("ies")) {

            result = result.substring(
                    0,
                    result.length() - 3
            ) + "y";

        } else if (result.endsWith("oes")) {

            result = result.substring(
                    0,
                    result.length() - 2
            );

        } else if (result.endsWith("s")
                && !result.endsWith("ss")) {

            result = result.substring(
                    0,
                    result.length() - 1
            );
        }

        return result;
    }

    private String normalizeUnit(String unit) {

        String result = unit.toLowerCase().trim();

        if (result.equals("gram") ||
                result.equals("grams")) {
            return "g";
        }

        if (result.equals("kilogram") ||
                result.equals("kilograms")) {
            return "kg";
        }

        if (result.equals("millilitre") ||
                result.equals("millilitres") ||
                result.equals("milliliter") ||
                result.equals("milliliters")) {
            return "ml";
        }

        if (result.equals("litre") ||
                result.equals("litres") ||
                result.equals("liter") ||
                result.equals("liters")) {
            return "l";
        }

        if (result.equals("piece") ||
                result.equals("pieces") ||
                result.equals("pc")) {
            return "pcs";
        }

        if (result.equals("slice")) {
            return "slices";
        }

        return result;
    }

    private double convertQuantity(
            double quantity,
            String pantryUnit,
            String requiredUnit) {

        String from = normalizeUnit(pantryUnit);
        String to = normalizeUnit(requiredUnit);

        if (from.equals(to)) {
            return quantity;
        }

        if (from.equals("kg") && to.equals("g")) {
            return quantity * 1000;
        }

        if (from.equals("g") && to.equals("kg")) {
            return quantity / 1000;
        }

        if (from.equals("l") && to.equals("ml")) {
            return quantity * 1000;
        }

        if (from.equals("ml") && to.equals("l")) {
            return quantity / 1000;
        }

        return -1;
    }
    public String getRecipeIngredientsText(int recipeId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM " + TABLE_RECIPE_INGREDIENTS +
                        " WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        StringBuilder ingredientsText = new StringBuilder();

        if (cursor.moveToFirst()) {

            do {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("ingredient_name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                String quantityText;

                if (quantity == (int) quantity) {
                    quantityText = String.valueOf((int) quantity);
                } else {
                    quantityText = String.valueOf(quantity);
                }

                ingredientsText
                        .append("• ")
                        .append(quantityText)
                        .append(" ")
                        .append(unit)
                        .append(" ")
                        .append(name)
                        .append("\n");

            } while (cursor.moveToNext());
        }

        cursor.close();

        return ingredientsText.toString();
    }
}