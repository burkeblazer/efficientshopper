package bblazer.com.efficientshopper.shoppinglist;

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

public class ShoppingList {
    private String name;
    private ArrayList<Meal> meals = new ArrayList<>();

    public ArrayList<Meal> getMeals() {
        return meals;
    }

    public void setMeals(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    public ShoppingList(String name) {
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

    public static ArrayList<ShoppingList> getShoppingLists(Activity context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String listsJSON              = preferences.getString(context.getApplicationContext().getResources().getString(R.string.shopping_list_json), "");
        if (listsJSON == null || listsJSON.equals("")) {return new ArrayList<ShoppingList>();}

        Type listOfTestObject = new TypeToken<ArrayList<ShoppingList>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<ShoppingList> lists = gson.fromJson(listsJSON, listOfTestObject);

        return lists;
    }

    public static void addShoppingList(Activity context, ShoppingList shoppingList) {
        Gson gson = new Gson();
        ArrayList<ShoppingList> shoppingLists = getShoppingLists(context);
        shoppingLists.add(shoppingList);
        Type listOfTestObject = new TypeToken<ArrayList<ShoppingList>>(){}.getType();
        String json = gson.toJson(shoppingLists, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.shopping_list_json), json);
        editor.commit();
    }

    public static void removeShoppingList(Activity context, String listName) {
        Gson gson = new Gson();
        ArrayList<ShoppingList> shoppingLists = getShoppingLists(context);

        // Find the list
        int index = -1;
        for (int ct = 0; ct < shoppingLists.size(); ct++) {
            if (shoppingLists.get(ct).getName().equals(listName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        shoppingLists.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<ShoppingList>>(){}.getType();
        String json = gson.toJson(shoppingLists, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.shopping_list_json), json);
        editor.commit();
    }

    public static ShoppingList clone(ShoppingList shoppingList) {
        ShoppingList newShoppingList = new ShoppingList(shoppingList.getName());
        newShoppingList.meals        = cloneMeals(shoppingList);

        return newShoppingList;
    }

    private static ArrayList<Meal> cloneMeals(ShoppingList shoppingList) {
        ArrayList<Meal> meals = new ArrayList<Meal>();
        for (Meal meal :
                shoppingList.getMeals()) {
            meals.add(Meal.clone(meal));
        }

        return meals;
    }

    public void updateFrom(ShoppingList shoppingList) {
        this.meals = shoppingList.meals;
        this.name  = shoppingList.name;
    }
}
