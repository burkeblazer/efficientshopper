package bblazer.com.efficientshopper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import bblazer.com.efficientshopper.meal.ingredient.EditPantryActivity;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;

/**
 * Created by bblazer on 2/5/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Check for expired ingredients
        checkExpiredIngredients(context);
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

    private void showExpiredNotification(ArrayList<Ingredient> expired, Context context) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, EditPantryActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(0)
                        .setContentTitle("Efficient Shopper")
                        .setContentText("You have "+expired.size()+" item(s) in your pantry that have expired. Click here to view them.");
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}
