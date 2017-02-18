package bblazer.com.efficientshopper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bblazer.com.efficientshopper.event.Event;
import bblazer.com.efficientshopper.event.EventAdapter;
import bblazer.com.efficientshopper.meal.EditMealsActivity;
import bblazer.com.efficientshopper.meal.ingredient.EditPantryActivity;
import bblazer.com.efficientshopper.meal.log.ViewMealLogsActivity;
import bblazer.com.efficientshopper.meal.plan.ViewMealPlansActivity;
import bblazer.com.efficientshopper.store.EditStoresActivity;

public class MainActivity extends AppCompatActivity {
    private ListView eventList;
    private RelativeLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventList = (ListView) findViewById(R.id.event_list);
        emptyView = (RelativeLayout) findViewById(R.id.empty_view);

        ArrayList<Event> events = Event.getEvents(this);
        eventList.setAdapter(new EventAdapter(this, events));

        if (events.size() > 0) {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings_menu:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onResume() {
        super.onResume();

        Intent alarm                = new Intent(this, BootReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarm, 0);
        AlarmManager alarmManager   = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        ArrayList<Event> events = Event.getEvents(this);
        eventList.setAdapter(new EventAdapter(this, events));
    }

    public void showEditMealLogs(View view) {
        Intent intent = new Intent(this, ViewMealLogsActivity.class);
        startActivity(intent);
    }

    public void showEditStores(View view) {
        Intent intent = new Intent(this, EditStoresActivity.class);
        startActivity(intent);
    }

    public void showEditMeals(View view) {
        Intent intent = new Intent(this, EditMealsActivity.class);
        startActivity(intent);
    }

    public void showEditPantry(View view) {
        Intent intent = new Intent(this, EditPantryActivity.class);
        startActivity(intent);
    }

    public void showCreateList(View view) {
        Intent intent = new Intent(this, ViewMealPlansActivity.class);
        startActivity(intent);
    }
}
