package bblazer.com.efficientshopper.meal.log;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.Meal;

/**
 * Created by bblazer on 2/1/2017.
 */
public class MealLog {
    private String name;
    private Time timeEaten;
    private Time timeRecorded;
    private Meal mealEaten;

    public MealLog() {
        return;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Time getTimeEaten() {
        return timeEaten;
    }

    public void setTimeEaten(Time timeEaten) {
        this.timeEaten = timeEaten;
    }

    public Time getTimeRecorded() {
        return timeRecorded;
    }

    public void setTimeRecorded(Time timeRecorded) {
        this.timeRecorded = timeRecorded;
    }

    public Meal getMealEaten() {
        return mealEaten;
    }

    public void setMealsEaten(Meal mealEaten) {
        this.mealEaten = mealEaten;
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

        mealLogs.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<MealLog>>(){}.getType();
        String json = gson.toJson(mealLogs, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.meal_logs_json), json);
        editor.commit();
    }

    public static MealLog clone(MealLog mealLog) {
        MealLog newMealLog      = new MealLog();
        newMealLog.name         = mealLog.name;
        newMealLog.timeEaten    = mealLog.timeEaten;
        newMealLog.timeRecorded = mealLog.timeRecorded;
        newMealLog.mealEaten    = Meal.clone(mealLog.mealEaten);

        return newMealLog;
    }

    public void updateFrom(MealLog mealLog) {
        this.name         = mealLog.name;
        this.timeEaten    = mealLog.timeEaten;
        this.timeRecorded = mealLog.timeRecorded;
        this.mealEaten    = Meal.clone(mealLog.mealEaten);
    }
}
