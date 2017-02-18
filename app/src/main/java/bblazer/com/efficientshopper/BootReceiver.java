package bblazer.com.efficientshopper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import bblazer.com.efficientshopper.meal.ingredient.EditPantryActivity;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;
import bblazer.com.efficientshopper.meal.plan.EditMealPlanActivity;

/**
 * Created by bblazer on 2/5/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check for expired ingredients
        checkExpiredIngredients(context);

        // Check for meal plans created
        checkForMealPlansCreated(context);
    }

    private void checkExpiredIngredients(Context context) {
        ArrayList<Ingredient> ingredients = Ingredient.getIngredients(context);
        ArrayList<Ingredient> expired     = new ArrayList<Ingredient>();
        Calendar c                        = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        Date today                        = c.getTime();
        for (Ingredient currentIngredient :
                ingredients) {
            if (today.after(currentIngredient.getExpirationDate())) {expired.add(currentIngredient);}
        }

        if (expired.size() > 0) {
            showExpiredNotification(expired, context);
        }
    }

    private void checkForMealPlansCreated(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showMealPlan    = prefs.getBoolean("meal_plan_reminder_notification", true);
        if (!showMealPlan) {
            return;
        }

        String notificationDay  = prefs.getString("pref_meal_plan_reminder_values", "-1");
        if (notificationDay.equals("-1")) {return;}

        String day = "";
        switch (notificationDay){
            case "0":
                day = "Sunday";
                break;
            case "1":
                day = "Monday";
                break;
            case "2":
                day = "Tuesday";
                break;
            case "3":
                day = "Wednesday";
                break;
            case "4":
                day = "Thursday";
                break;
            case "5":
                day = "Friday";
                break;
            case "6":
                day = "Saturday";
                break;
            default:
                break;
        }

        if (day.equals("")) {return;}

        Calendar calendar = Calendar.getInstance();
        Date date         = calendar.getTime();
        String todayDay   = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime());

        if (todayDay.equals(day)) {
            // Check to see if they've made a meal plan for today
//            MealPlan mealPlan = MealPlan.getThisWeeksMealPlan(context);
//
//            if (mealPlan != null) {return;}

            showMealPlanNotification(context);
        }
    }

    private void showMealPlanNotification(Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, EditMealPlanActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.meal_icon)
                        .setContentTitle("Efficient Shopper")
                        .setContentText("If you have not created a meal plan for the week, click here to make one.");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void showExpiredNotification(ArrayList<Ingredient> expired, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showExpired = prefs.getBoolean("expired_ingredients_notification", true);
        if (!showExpired) {return;}

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, EditPantryActivity.class), 0);

        String message = "";
        if (expired.size() == 1) {
            message = "You have 1 item in your pantry that has expired. Click here to view your pantry.";
        }
        else {
            message = "You have "+expired.size()+" items in your pantry that have expired. Click here to view your pantry.";
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.pantry_icon)
                        .setContentTitle("Efficient Shopper")
                        .setContentText(message);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
