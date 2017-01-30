package bblazer.com.efficientshopper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import bblazer.com.efficientshopper.meal.Meal;
import bblazer.com.efficientshopper.meal.MealAdapter;

public class EditMealsActivity extends AppCompatActivity {
    private RelativeLayout emptyView;
    public ArrayList<Meal> meals;
    private ListView listView;
    public MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meals);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Meals");

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
                addNewMeal();
            }
        });

        meals = Meal.getMeals(this);

        emptyView = (RelativeLayout)findViewById(R.id.empty_view);
        listView = (ListView)findViewById(R.id.list_view);
        mealAdapter = new MealAdapter(this, meals);
        listView.setAdapter(mealAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Meal selMeal = mealAdapter.getItem(position);
                editMeal(selMeal);
            }
        });

        registerForContextMenu(listView);

        checkEmpty();
    }

    private void checkEmpty() {
        if (meals.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void editMeal(Meal meal) {
        Intent intent = new Intent(this, AddNewMealActivity.class);
        AddNewMealActivity.meal     = meal;
        AddNewMealActivity.activity = this;
        startActivity(intent);
    }

    private void addNewMeal() {
        Intent intent = new Intent(this, AddNewMealActivity.class);
        AddNewMealActivity.meal     = null;
        AddNewMealActivity.activity = this;
        startActivity(intent);
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
        Meal meal = meals.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                editMeal(meal);
                return true;
            case R.id.delete:
                Meal.removeMeal(this, meal.getName());
                mealAdapter.meals.remove(meal);
                mealAdapter.notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void updateMeal(Meal meal, String previousName) {
        // Find the previous meal and update it
        for (Meal previousMeal :
                meals) {
            if (previousName.equals(previousMeal.getName())) {
                previousMeal.updateFrom(meal);
            }
        }

        Meal.removeMeal(this, previousName);
        Meal.addMeal(this, meal);
    }

    public void saveNewMeal(Meal meal) {
        // Make sure they aren't trying to add a meal that already exists
        boolean bExists = false;
        for (Meal currentMeal :
                meals) {
            if (currentMeal.getName().equals(meal.getName())) {bExists = true;}
        }

        if (bExists) {
            Toast.makeText(this, "Found a duplicate meal, please make sure to add meals only once", Toast.LENGTH_LONG).show();return;}

        Meal.addMeal(this, meal);
        mealAdapter.meals.add(meal);
        mealAdapter.notifyDataSetChanged();

        checkEmpty();
    }
}
