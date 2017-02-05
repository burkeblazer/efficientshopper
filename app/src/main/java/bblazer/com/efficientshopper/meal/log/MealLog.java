package bblazer.com.efficientshopper.meal.log;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;

/**
 * Created by bblazer on 2/1/2017.
 */
public class MealLog {
    private String name;
    private Calendar timeEaten;
    private ArrayList<Ingredient> ingredientsEaten;

    public MealLog() {
        return;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getTimeEaten() {
        return timeEaten;
    }

    public void setTimeEaten(Calendar timeEaten) {
        this.timeEaten = timeEaten;
    }

    public ArrayList<Ingredient> getIngredientsEaten() {
        return ingredientsEaten;
    }

    public void setIngredientsEaten(ArrayList<Ingredient> ingredientsEaten) {
        this.ingredientsEaten = ingredientsEaten;
    }

    public static ArrayList<MealLog> getMealLogs(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String mealLogsJSON        = preferences.getString(context.getApplicationContext().getResources().getString(R.string.meal_logs_json), "");
        if (mealLogsJSON == null || mealLogsJSON.equals("")) {return new ArrayList<MealLog>();}

        Type listOfTestObject = new TypeToken<ArrayList<MealLog>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<MealLog> mealLogs = gson.fromJson(mealLogsJSON, listOfTestObject);

        return mealLogs;
    }

    public static void addMealLog(Activity context, MealLog mealLog) {
        Gson gson = new Gson();
        ArrayList<MealLog> mealLogs = getMealLogs(context);
        mealLogs.add(mealLog);
        Type listOfTestObject = new TypeToken<ArrayList<MealLog>>(){}.getType();
        String json = gson.toJson(mealLogs, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meal_logs_json), json);
        editor.commit();

        // Add ingredients back into pantry
        Ingredient.removeIngredientsAmountsForMealLog(mealLog, context);
    }

    public static void removeMealLog(Activity context, String mealLogName) {
        Gson gson = new Gson();
        ArrayList<MealLog> mealLogs = getMealLogs(context);

        // Find the mealLog
        int index = -1;
        for (int ct = 0; ct < mealLogs.size(); ct++) {
            if (mealLogs.get(ct).getName().equals(mealLogName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        MealLog removeMealLog = MealLog.clone(mealLogs.get(index));
        mealLogs.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<MealLog>>(){}.getType();
        String json = gson.toJson(mealLogs, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meal_logs_json), json);
        editor.commit();

        // Add ingredients back into pantry
        Ingredient.addIngredientsAmountsForMealLog(removeMealLog, context);
    }

    public static MealLog clone(MealLog mealLog) {
        MealLog newMealLog          = new MealLog();
        newMealLog.name             = mealLog.name;
        newMealLog.timeEaten        = mealLog.timeEaten;
        newMealLog.ingredientsEaten = cloneIngredients(mealLog);

        return newMealLog;
    }

    private static ArrayList<Ingredient> cloneIngredients(MealLog mealLog) {
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (Ingredient ingredient :
                mealLog.getIngredientsEaten()) {
            ingredients.add(Ingredient.clone(ingredient));
        }

        return ingredients;
    }

    public void updateFrom(MealLog mealLog) {
        this.name             = mealLog.name;
        this.timeEaten        = mealLog.timeEaten;
        this.ingredientsEaten = cloneIngredients(mealLog);
    }
}
