package bblazer.com.efficientshopper.meal.plan;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.Meal;

/**
 * Created by bblazer on 1/29/2017.
 */

public class MealPlan {
    private String name;
    private ArrayList<Meal> meals = new ArrayList<>();

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public MealPlan(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean removeMeal(Meal removeMeal) {
        int index = -1;
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getName().equals(removeMeal.getName())) {index = ct;}
        }

        if (index == -1) {return false;}

        meals.remove(index);

        return true;
    }

    public boolean addMeal(Meal addMeal) {
        // Make sure it doesn't already exist
        for (int ct = 0; ct < meals.size(); ct++) {
            if (meals.get(ct).getName().equals(addMeal.getName())) {return false;}
        }

        meals.add(addMeal);

        return true;
    }

    public static ArrayList<MealPlan> getMealPlans(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String mealsJSON              = preferences.getString(context.getApplicationContext().getResources().getString(R.string.meal_plan_json), "");
        if (mealsJSON == null || mealsJSON.equals("")) {return new ArrayList<MealPlan>();}

        Type listOfTestObject = new TypeToken<ArrayList<MealPlan>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<MealPlan> meals = gson.fromJson(mealsJSON, listOfTestObject);

        return meals;
    }

    public static void addMealPlan(Activity context, MealPlan mealPlan) {
        Gson gson = new Gson();
        ArrayList<MealPlan> mealPlans = getMealPlans(context);
        mealPlans.add(mealPlan);
        Type listOfTestObject = new TypeToken<ArrayList<MealPlan>>(){}.getType();
        String json = gson.toJson(mealPlans, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meal_plan_json), json);
        editor.commit();
    }

    public static void removeMealPlan(Activity context, String mealPlanName) {
        Gson gson = new Gson();
        ArrayList<MealPlan> mealPlans = getMealPlans(context);

        // Find the list
        int index = -1;
        for (int ct = 0; ct < mealPlans.size(); ct++) {
            if (mealPlans.get(ct).getName().equals(mealPlanName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        mealPlans.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<MealPlan>>(){}.getType();
        String json = gson.toJson(mealPlans, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meal_plan_json), json);
        editor.commit();
    }

    public static MealPlan clone(MealPlan mealPlan) {
        MealPlan newMealPlan = new MealPlan(mealPlan.getName());
        newMealPlan.meals    = cloneMeals(mealPlan);

        return newMealPlan;
    }

    private static ArrayList<Meal> cloneMeals(MealPlan mealPlan) {
        ArrayList<Meal> meals = new ArrayList<Meal>();
        for (Meal meal :
                mealPlan.getMeals()) {
            meals.add(Meal.clone(meal));
        }

        return meals;
    }

    public void updateFrom(MealPlan mealPlan) {
        this.meals = mealPlan.meals;
        this.name  = mealPlan.name;
    }
}
