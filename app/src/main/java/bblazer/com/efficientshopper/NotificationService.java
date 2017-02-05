package bblazer.com.efficientshopper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bblazer on 2/5/2017.
 */
public class NotificationService extends WakefulIntentService {
    public AppService() {
        super("AppService");
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        Log.i("AppService", "I'm awake! I'm awake! (yawn)");
    }
}