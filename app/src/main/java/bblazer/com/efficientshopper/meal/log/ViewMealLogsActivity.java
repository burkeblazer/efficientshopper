package bblazer.com.efficientshopper.meal.log;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import bblazer.com.efficientshopper.R;

public class ViewMealLogsActivity extends AppCompatActivity {
    private RelativeLayout emptyView;
    private ListView listView;

    private ArrayList<MealLog> mealLogs;
    private MealLogAdapter mealLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meal_logs);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Meal Logs");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMealLog();
            }
        });

        mealLogs       = MealLog.getMealLogs(this);
        emptyView      = (RelativeLayout)findViewById(R.id.empty_view);
        listView       = (ListView)findViewById(R.id.list_view);
        mealLogAdapter = new MealLogAdapter(this, mealLogs);
        listView.setAdapter(mealLogAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MealLog selMealLog = mealLogAdapter.getItem(position);
                editMealLog(selMealLog);
            }
        });

        registerForContextMenu(listView);

        checkEmpty();
    }

    private void checkEmpty() {
        if (mealLogs.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void editMealLog(MealLog mealLog) {
        Intent intent = new Intent(this, EditMealLogActivity.class);
        EditMealLogActivity.mealLog    = mealLog;
        EditMealLogActivity.activity = this;
        startActivity(intent);
    }

    private void addNewMealLog() {
        Intent intent = new Intent(this, EditMealLogActivity.class);
        EditMealLogActivity.mealLog    = null;
        EditMealLogActivity.activity = this;
        startActivity(intent);
    }

    public void updateMealLog(MealLog mealLog, String previousName) {
        // Find the previous mealLog and update it
        for (MealLog previousMealLog :
                mealLogs) {
            if (previousName.equals(previousMealLog.getName())) {
                previousMealLog.updateFrom(mealLog);
            }
        }

        // Probably here, remove pantry item amounts based on the meals eaten

        MealLog.updateEvent(mealLog, this);
        MealLog.removeMealLog(this, previousName);
        MealLog.addMealLog(this, mealLog);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listview_menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MealLog mealLog = mealLogs.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                editMealLog(mealLog);
                return true;
            case R.id.delete:
                MealLog.deleteEvent(mealLog, this);
                MealLog.removeMealLog(this, mealLog.getName());
                mealLogAdapter.mealLogs.remove(mealLog);
                mealLogAdapter.notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void saveNewMealLog(MealLog mealLog) {
        // Make sure they aren't trying to add a mealLog that already exists
        boolean bExists = false;
        for (MealLog currentMealLog :
                mealLogs) {
            if (currentMealLog.getName().equals(mealLog.getName())) {bExists = true;}
        }

        if (bExists) {
            Toast.makeText(this, "Found a duplicate meal log, please make sure to add meal logs only once", Toast.LENGTH_LONG).show();return;}

        MealLog.addEvent(mealLog, this);
        MealLog.addMealLog(this, mealLog);
        mealLogAdapter.mealLogs.add(mealLog);
        mealLogAdapter.notifyDataSetChanged();

        checkEmpty();
    }
}

