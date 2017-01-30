package bblazer.com.efficientshopper;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import bblazer.com.efficientshopper.meal.Meal;
import bblazer.com.efficientshopper.meal.MealAdapter;
import bblazer.com.efficientshopper.shoppinglist.ShoppingList;

public class AddNewShoppingListActivity extends AppCompatActivity {
    private EditText listName;
    private Spinner mealsSpinner;
    private ListView listView;
    private ImageButton addMealsButton;
    private RelativeLayout emptyView;
    private Button generateListButton;

    public static ShoppingList shoppingList;
    public static EditListActivity activity;
    private boolean isEdit;
    private String previousName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listName           = (EditText)findViewById(R.id.list_name);
        mealsSpinner       = (Spinner) findViewById(R.id.meals_spinner);
        listView           = (ListView)findViewById(R.id.list_view);
        addMealsButton     = (ImageButton)findViewById(R.id.add_meals_button);
        emptyView          = (RelativeLayout)findViewById(R.id.empty_view);
        generateListButton = (Button)findViewById(R.id.generate_list);

        // Create or set a store object (this is how we know if we are editting or adding)
        if (shoppingList == null) {
            shoppingList = new ShoppingList("");
            getSupportActionBar().setTitle("Add Shopping List");

            // If it's a new one, add a list name default as this week
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date dt              = new Date();
            String S             = sdf.format(dt);
            listName.setText("Week of "+S);
            shoppingList.setName("Week of "+S);
        }
        else {
            isEdit       = true;
            shoppingList = ShoppingList.clone(shoppingList);
            previousName = shoppingList.getName();
            getSupportActionBar().setTitle("Edit Shopping List");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                shoppingList.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Create string adapter for meals dropdown
        ArrayList<String> mealsStr  = new ArrayList<String>();
        ArrayList<Meal> mealsArray  = Meal.getMeals(this);
        for (Meal meal :
                mealsArray) {
            mealsStr.add(meal.getName());
        }

        ArrayAdapter<String> mealAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mealsStr);
        mealAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsSpinner.setAdapter(mealAdapterSpinner);

        MealAdapter mealAdapter = new MealAdapter(this, shoppingList.getMeals());
        listView.setAdapter(mealAdapter);

        addMealsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMeal();
            }
        });

        generateListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewShoppingListActivity();
            }
        });

        registerForContextMenu(listView);

        if (isEdit) {
            loadShoppingListData();
        }

        checkEmpty();
    }

    private void showViewShoppingListActivity() {
        Intent intent = new Intent(this, ViewShoppingListActivity.class);
        ViewShoppingListActivity.shoppingList = shoppingList;
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listview_menu_list, menu);
            menu.getItem(0).setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Meal meal = shoppingList.getMeals().get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.delete:
                ((MealAdapter)listView.getAdapter()).meals.remove(meal);
                ((MealAdapter)listView.getAdapter()).notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void checkEmpty() {
        if (shoppingList.getMeals().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void loadShoppingListData() {
        listName.setText(shoppingList.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_shopping_list_menu, menu);
        MenuItem update = menu.findItem(R.id.update_shopping_list);
        MenuItem save = menu.findItem(R.id.save_shopping_list);
        if (isEdit) {
            save.setVisible(false);
        }
        else {
            update.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.update_shopping_list:
            case R.id.save_shopping_list:
                // Make sure to validate the new meal to make sure they've at least entered a name...
                if (shoppingList.getName() == null || shoppingList.getName().equals("")) {
                    Toast.makeText(AddNewShoppingListActivity.this, "Please make sure to at least enter a name before trying to save.", Toast.LENGTH_LONG).show();return true;}

                if (isEdit) {
                    activity.updateShoppingList(shoppingList, previousName);
                }
                else {
                    activity.saveNewShoppingList(shoppingList);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addMeal() {
        // Create a new meal object from the string selected in the spinner
        ArrayList<Meal> meals = Meal.getMeals(this);
        String selectedMeal = mealsSpinner.getSelectedItem().toString();
        Meal mealObj = new Meal("");
        for (Meal meal :
                meals) {
            if (meal.getName().equals(selectedMeal)) {
                mealObj = meal;
            }
        }

        // Make sure this meal doesn't already exist
        boolean bExists = false;
        for (Meal meal :
                shoppingList.getMeals()) {
            if (meal.getName().equals(mealObj.getName())) {bExists = true;}
        }

        // Display error
        if (bExists) {Toast.makeText(this, "Meal already exists, please modify the currently added one.", Toast.LENGTH_LONG).show();return;}

        // Add the meal to the shopping list
        shoppingList.addMeal(mealObj);

        // Add the meal to the listview
        ((MealAdapter)listView.getAdapter()).notifyDataSetChanged();
        checkEmpty();
    }
}
