package bblazer.com.efficientshopper.meal.plan;

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
import bblazer.com.efficientshopper.meal.Meal;

public class ViewMealPlansActivity extends AppCompatActivity {
    private RelativeLayout emptyView;
    private ListView listView;

    private ArrayList<MealPlan> mealPlans;
    private MealPlanAdapter mealPlanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Meal Plans");
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
                addNewMealPlan();
            }
        });

        mealPlans           = MealPlan.getMealPlans(this);

        emptyView           = (RelativeLayout)findViewById(R.id.empty_view);
        listView            = (ListView)findViewById(R.id.list_view);
        mealPlanAdapter     = new MealPlanAdapter(this, mealPlans);
        listView.setAdapter(mealPlanAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MealPlan selMealPlan = mealPlanAdapter.getItem(position);
                editMealPlan(selMealPlan);
            }
        });

        registerForContextMenu(listView);

        checkEmpty();
    }

    private void checkEmpty() {
        if (mealPlans.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void editMealPlan(MealPlan mealPlan) {
        Intent intent                 = new Intent(this, EditMealPlanActivity.class);
        EditMealPlanActivity.mealPlan = mealPlan;
        EditMealPlanActivity.activity = this;
        startActivity(intent);
    }

    private void addNewMealPlan() {
        Intent intent                 = new Intent(this, EditMealPlanActivity.class);
        EditMealPlanActivity.mealPlan = null;
        EditMealPlanActivity.activity = this;
        startActivity(intent);
    }

    public void updateMealPlan(MealPlan mealPlan, String previousName) {
        // Find the previous mealPlan and update it
        for (MealPlan previousMealPlan :
                mealPlans) {
            if (previousName.equals(previousMealPlan.getName())) {
                previousMealPlan.updateFrom(mealPlan);
            }
        }

        MealPlan.updateEvent(mealPlan, this);
        MealPlan.removeMealPlan(this, previousName);
        MealPlan.addMealPlan(this, mealPlan);
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
        MealPlan mealPlan = mealPlans.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                editMealPlan(mealPlan);
                return true;
            case R.id.delete:
                MealPlan.deleteEvent(mealPlan, this);
                MealPlan.removeMealPlan(this, mealPlan.getName());
                mealPlanAdapter.mealPlans.remove(mealPlan);
                mealPlanAdapter.notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void saveNewMealPlan(MealPlan mealPlan) {
        // Make sure they aren't trying to add a mealPlan that already exists
        boolean bExists = false;
        for (MealPlan currentMealPlan :
                mealPlans) {
            if (currentMealPlan.getName().equals(mealPlan.getName())) {bExists = true;}
        }

        if (bExists) {
            Toast.makeText(this, "Found a duplicate meal plan, please make sure to add meal plans only once", Toast.LENGTH_LONG).show();return;}

        MealPlan.addEvent(mealPlan, this);
        MealPlan.addMealPlan(this, mealPlan);
        mealPlanAdapter.mealPlans.add(mealPlan);
        mealPlanAdapter.notifyDataSetChanged();

        checkEmpty();
    }
}
