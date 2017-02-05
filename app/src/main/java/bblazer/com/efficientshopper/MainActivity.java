package bblazer.com.efficientshopper;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;

import bblazer.com.efficientshopper.meal.EditMealsActivity;
import bblazer.com.efficientshopper.meal.ingredient.EditPantryActivity;
import bblazer.com.efficientshopper.meal.log.EditMealLogActivity;
import bblazer.com.efficientshopper.meal.log.ViewMealLogsActivity;
import bblazer.com.efficientshopper.meal.plan.ViewMealPlansActivity;
import bblazer.com.efficientshopper.store.EditStoresActivity;

public class MainActivity extends AppCompatActivity {
    private Button editStores;
    private Button editMeals;
    private Button editPantry;
    private Button createList;
    private Button editMealLogs;
    private NotificationService mSensorService;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editStores    = (Button)findViewById(R.id.EditStores);
        editMeals     = (Button)findViewById(R.id.EditMeals);
        editPantry    = (Button)findViewById(R.id.EditPantry);
        createList    = (Button)findViewById(R.id.CreateList);
        editMealLogs  = (Button)findViewById(R.id.ViewMealLogs);

        editStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditStores();
            }
        });
        editMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditMeals();
            }
        });
        editPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPantry();
            }
        });
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateList();
            }
        });
        editMealLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditMealLogs();
            }
        });
    }

    public void onResume() {
        super.onResume();

        mSensorService = new NotificationService(this);
        mServiceIntent = new Intent(this, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public void showEditMealLogs() {
        Intent intent = new Intent(this, ViewMealLogsActivity.class);
        startActivity(intent);
    }

    public void showEditStores() {
        Intent intent = new Intent(this, EditStoresActivity.class);
        startActivity(intent);
    }

    public void showEditMeals() {
        Intent intent = new Intent(this, EditMealsActivity.class);
        startActivity(intent);
    }

    public void showEditPantry() {
        Intent intent = new Intent(this, EditPantryActivity.class);
        startActivity(intent);
    }

    public void showCreateList() {
        Intent intent = new Intent(this, ViewMealPlansActivity.class);
        startActivity(intent);
    }
}
