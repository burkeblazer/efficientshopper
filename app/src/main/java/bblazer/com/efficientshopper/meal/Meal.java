package bblazer.com.efficientshopper.meal;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bblazer.com.efficientshopper.R;

/**
 * Created by bblazer on 1/29/2017.
 */
public class Meal {
    private String notes;
    private String name;
    private ArrayList<Ingredient> ingredients = new ArrayList<>();

    public Meal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> departments) {
        this.ingredients = departments;
    }

    public boolean removeIngredient(Ingredient removeIngredient) {
        int index = -1;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getName().equals(removeIngredient.getName())) {index = ct;}
        }

        if (index == -1) {return false;}

        ingredients.remove(index);

        return true;
    }

    public boolean addIngredient(Ingredient addIngredient) {
        // Make sure it doesn't already exist
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getName().equals(addIngredient.getName())) {return false;}
        }

        ingredients.add(addIngredient);

        return true;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static ArrayList<Meal> getMeals(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String mealsJSON        = preferences.getString(context.getApplicationContext().getResources().getString(R.string.meals_json), "");
        if (mealsJSON == null || mealsJSON.equals("")) {return new ArrayList<Meal>();}

        Type listOfTestObject = new TypeToken<ArrayList<Meal>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Meal> meals = gson.fromJson(mealsJSON, listOfTestObject);

        return meals;
    }

    public static void addMeal(Activity context, Meal meal) {
        Gson gson = new Gson();
        ArrayList<Meal> meals = getMeals(context);
        meals.add(meal);
        Type listOfTestObject = new TypeToken<ArrayList<Meal>>(){}.getType();
        String json = gson.toJson(meals, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meals_json), json);
        editor.commit();
    }

    public static void removeMeal(Activity context, String mealName) {
        Gson gson = new Gson();
        ArrayList<Meal> meals = getMeals(context);

        // Find the meal
        int index = -1;
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getName().equals(mealName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        meals.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<Meal>>(){}.getType();
        String json = gson.toJson(meals, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meals_json), json);
        editor.commit();
    }

    public static Meal clone(Meal meal) {
        Meal newMeal        = new Meal(meal.getName());
        newMeal.ingredients = cloneIngredients(meal);
        newMeal.notes       = meal.notes;

        return newMeal;
    }

    private static ArrayList<Ingredient> cloneIngredients(Meal meal) {
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (Ingredient ingredient :
                meal.getIngredients()) {
            ingredients.add(Ingredient.clone(ingredient));
        }

        return ingredients;
    }

    public void updateFrom(Meal meal) {
        this.name        = meal.name;
        this.ingredients = meal.ingredients;
        this.notes       = meal.notes;
    }

    public static void checkIngredientUpdate(String previousName, String name, Activity activity) {
        ArrayList<Meal> meals = Meal.getMeals(activity);
        for (Meal meal :
                meals) {
            for (Ingredient ingredient :
                    meal.getIngredients()) {
                if (ingredient.getName().equals(previousName)) {
                    ingredient.setName(name);
                }
            }
        }

        Gson gson = new Gson();
        Type listOfTestObject = new TypeToken<ArrayList<Meal>>(){}.getType();
        String json = gson.toJson(meals, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(activity.getString(R.string.meals_json), json);
        editor.commit();
    }
}
