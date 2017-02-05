package bblazer.com.efficientshopper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

/**
 * Created by bblazer on 2/5/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent i = new Intent(context, NotificationService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        am.cancel(pi);

        PendingIntent servicePendingIntent =
                PendingIntent.getService(context,
                        999,
                        i,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        am.setRepeating(
                AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
                0,
                1*60*1000,
                servicePendingIntent
        );

        wl.release();

        context.startService(new Intent(context, NotificationService.class));;
    }
}
