package bblazer.com.efficientshopper.meal.ingredient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.log.MealLog;
import bblazer.com.efficientshopper.store.Department;

/**
 * Created by bblazer on 1/29/2017.
 */
public class Ingredient {
    private String name;
    private Department department;
    private int amount;
    private int sortOrder;
    private boolean checked;
    private Date expirationDate;

    public Ingredient(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public static ArrayList<Ingredient> getIngredients(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String ingredientsJSON        = preferences.getString(context.getApplicationContext().getResources().getString(R.string.ingredients_json), "");
        if (ingredientsJSON == null || ingredientsJSON.equals("")) {return new ArrayList<Ingredient>();}

        Type listOfTestObject = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Ingredient> ingredients = gson.fromJson(ingredientsJSON, listOfTestObject);

        return ingredients;
    }

    public static ArrayList<Ingredient> getEmptyIngredients(Activity context) {
        ArrayList<Ingredient> ingredients      = getIngredients(context);
        ArrayList<Ingredient> emptyIngredients = new ArrayList<Ingredient>();
        for (Ingredient ingredient :
                ingredients) {
            if (ingredient.getAmount() <= 0) {emptyIngredients.add(ingredient);}
        }

        return emptyIngredients;
    }

    public static void addIngredient(Activity context, Ingredient ingredient) {
        Gson gson = new Gson();
        ArrayList<Ingredient> ingredients = getIngredients(context);
        ingredients.add(ingredient);
        Type listOfTestObject = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        String json = gson.toJson(ingredients, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.ingredients_json), json);
        editor.commit();
    }

    public static void removeIngredient(Activity context, String ingredientName) {
        Gson gson = new Gson();
        ArrayList<Ingredient> ingredients = getIngredients(context);

        // Find the ingredient
        int index = -1;
        for (int ct = 0; ct < ingredients.size(); ct++) {
            if (ingredients.get(ct).getName().equals(ingredientName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        ingredients.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        String json = gson.toJson(ingredients, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.ingredients_json), json);
        editor.commit();
    }

    public static Ingredient clone(Ingredient ingredient) {
        Ingredient newIngredient     = new Ingredient(ingredient.getName());
        newIngredient.department     = ingredient.department;
        newIngredient.amount         = ingredient.amount;
        newIngredient.expirationDate = ingredient.expirationDate;

        return newIngredient;
    }

    public void updateFrom(Ingredient ingredient) {
        this.name           = ingredient.name;
        this.department     = ingredient.department;
        this.amount         = ingredient.amount;
        this.expirationDate = ingredient.expirationDate;
    }

    public static void addIngredientsAmountsForMealLog(MealLog removeMealLog, Activity context) {
        ArrayList<Ingredient> ingredients = getIngredients(context);
        for (Ingredient currentMealLogIngredient :
                removeMealLog.getIngredientsEaten()) {
            for (Ingredient currentIngredient :
                    ingredients) {
                if (currentIngredient.getName().equals(currentMealLogIngredient.getName())) {
                    int newAmount = currentIngredient.getAmount() + currentMealLogIngredient.getAmount();
                    currentIngredient.setAmount(newAmount);}
            }
        }

        Gson gson = new Gson();
        Type listOfTestObject = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        String json = gson.toJson(ingredients, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.ingredients_json), json);
        editor.commit();
    }

    public static void removeIngredientsAmountsForMealLog(MealLog mealLog, Activity context) {
        ArrayList<Ingredient> ingredients = getIngredients(context);
        for (Ingredient currentMealLogIngredient :
                mealLog.getIngredientsEaten()) {
            for (Ingredient currentIngredient :
                    ingredients) {
                if (currentIngredient.getName().equals(currentMealLogIngredient.getName())) {
                    int newAmount = currentIngredient.getAmount() - currentMealLogIngredient.getAmount();
                    if (newAmount < 0) {newAmount = 0;}
                    currentIngredient.setAmount(newAmount);}
            }
        }

        Gson gson = new Gson();
        Type listOfTestObject = new TypeToken<ArrayList<Ingredient>>(){}.getType();
        String json = gson.toJson(ingredients, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.ingredients_json), json);
        editor.commit();
    }
}
