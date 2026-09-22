package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PANTRY = "pantry_items";

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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);

        onCreate(db);
    }

    public boolean addIngredient(String name,
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
    public boolean updateIngredient(int id, String name, double quantity,
                                    String unit, String expiryDate) {

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


    public boolean deleteIngredient(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_PANTRY,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }
}