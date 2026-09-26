# Smart Pantry Manager

Smart Pantry Manager is an Android application developed in Java for the Mobile App Development 700 practical assignment.

The application allows users to manage their pantry ingredients and view recipes they can prepare using the ingredients they already have.

## Features

### Pantry Management
- Add, view, edit and delete ingredients.
- Record ingredient names, quantities and units.
- Add optional expiry dates.
- Validate ingredient information before saving.

### Suggested Recipes
- Includes 15 preloaded recipes.
- Suggests recipes based on available pantry ingredients.
- Only displays recipes when all required ingredients are available in sufficient quantities.
- Handles basic ingredient-name and unit differences.
- Displays recipe ingredients and preparation methods.

### Expiry Reminders
- Identifies expired ingredients.
- Highlights ingredients expiring today or within the next seven days.
- Allows users to enable or disable reminders in Settings.
- Saves the selected preference using SharedPreferences.

Reminders are displayed inside the application rather than as phone notifications.

### Navigation
The application includes bottom navigation between Pantry, Recipes and Settings.

## Technologies Used

- Java
- Android Studio
- SQLite and SQLiteOpenHelper
- SharedPreferences
- XML layouts
- ListView and custom adapters
- Gradle with Kotlin DSL

## Installation and Setup

1. Clone or download the repository from GitHub.
2. Open the project folder in Android Studio.
3. Allow Gradle synchronization to complete.
4. Install any missing SDK components if prompted.
5. Select an Android emulator or connect an Android device.
6. Click Run to launch the application.

Minimum Android SDK: API 24.

## How to Use

### Managing Ingredients
1. Open the Pantry screen.
2. Select Add Ingredient.
3. Enter the ingredient name, quantity and unit.
4. Enter an optional expiry date using yyyy-MM-dd.
5. Select Save Ingredient.

To edit or delete an ingredient, tap it in the Pantry list.

### Viewing Recipes
1. Add ingredients to the pantry.
2. Open Suggested Recipes.
3. Select a recipe to view its ingredients and preparation method.

### Expiry Reminders
1. Open Settings.
2. Enable or disable Show expiry reminders.
3. Return to Pantry to view the updated reminder display.

## Database

The application uses SQLite for local data storage.

The database contains three main tables:

- pantry_items - Stores pantry ingredients.
- recipes - Stores recipe names and preparation methods.
- recipe_ingredients - Stores the ingredients required for each recipe.

The 15 starter recipes are added when the recipe collection is empty.

## Project Structure

- app/src/main/java - Java application classes
- app/src/main/res/layout - Screen layouts
- app/src/main/res/menu - Bottom navigation menu
- app/src/main/res/values - Application resources
- app/src/main/AndroidManifest.xml - Application configuration
- gradle - Gradle configuration and wrapper files

## Developer

Callister Lloyd Naidoo

BSc Information Technology  
Richfield Graduate Institute of Technology
