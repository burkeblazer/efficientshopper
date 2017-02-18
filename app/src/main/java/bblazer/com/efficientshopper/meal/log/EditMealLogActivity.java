package bblazer.com.efficientshopper.meal.log;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.Meal;
import bblazer.com.efficientshopper.meal.MealSpinnerAdapter;
import bblazer.com.efficientshopper.meal.ingredient.DatePickerFragment;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;
import bblazer.com.efficientshopper.meal.ingredient.IngredientAdapter;

public class EditMealLogActivity extends AppCompatActivity implements
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {
    private EditText logName;
    private Button logTimeButton;
    private Spinner mealsSpinner;
    private Spinner ingredientTypeSpinner;
    private ListView listView;
    private RelativeLayout emptyView;

    public static MealLog mealLog;
    public static ViewMealLogsActivity activity;
    private IngredientAdapter ingredientsAdapter;

    private boolean isEdit;
    private String previousName;
    private Calendar calendar;
    FragmentManager fm;
    private boolean logNameChanged = false;
    private boolean setProgrammatically = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meal_log);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        logName       = (EditText) findViewById(R.id.log_name);
        logTimeButton = (Button) findViewById(R.id.logTimeButton);
        mealsSpinner  = (Spinner) findViewById(R.id.meals_spinner);
        listView      = (ListView)findViewById(R.id.list_view);
        emptyView     = (RelativeLayout)findViewById(R.id.empty_view);
        ingredientTypeSpinner = (Spinner) findViewById(R.id.ingredient_type_spinner);

        ingredientTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = ingredientTypeSpinner.getAdapter().getItem(position).toString();
                if (type.equals("Meals")) {
                    loadMeals();
                }
                else {
                    loadIngredients();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        calendar = Calendar.getInstance();

        // Create or set a store object (this is how we know if we are editting or adding)
        if (mealLog == null) {
            mealLog = new MealLog();
            getSupportActionBar().setTitle("Add Meal Log");

            SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy 'at' h:mm a");
            logTimeButton.setText(format.format(calendar.getTime()));

            mealLog.setTimeEaten(calendar);
        }
        else {
            isEdit       = true;
            mealLog      = MealLog.clone(mealLog);
            previousName = mealLog.getName();
            getSupportActionBar().setTitle("Edit Meal Log");
            logNameChanged = true;
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fm = getSupportFragmentManager();

        // Add a listener for the ingredient amount numberfield on change
        logName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mealLog.setName(s.toString());
                if (!setProgrammatically) {
                    logNameChanged = true;
                }
                else {
                    setProgrammatically = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ingredientsAdapter = new IngredientAdapter(this, new ArrayList<Ingredient>());
        listView.setAdapter(ingredientsAdapter);
        registerForContextMenu(listView);

        if (isEdit) {
            loadMealLogData();
        }

        setLogName();

        checkEmpty();
    }

    private void loadMealLogData() {
        // Name
        logName.setText(mealLog.getName());
        // Ingredients list view
        ingredientsAdapter = new IngredientAdapter(this, mealLog.getIngredientsEaten());
        listView.setAdapter(ingredientsAdapter);
        // Time button
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy 'at' h:mm a");
        logTimeButton.setText(format.format(mealLog.getTimeEaten().getTime()));
    }

    private void setLogName() {
        if (logNameChanged) {return;}
        Calendar cal            = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy 'at' h:mm a");
        try {
            cal.setTime(format.parse(logTimeButton.getText().toString()));

            Calendar breakfast      = Calendar.getInstance();
            breakfast.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            breakfast.set(Calendar.HOUR_OF_DAY, 6);
            breakfast.set(Calendar.MINUTE, 0);
            breakfast.set(Calendar.SECOND, 0);

            Calendar lunch      = Calendar.getInstance();
            lunch.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            lunch.set(Calendar.HOUR_OF_DAY, 10);
            lunch.set(Calendar.MINUTE, 30);
            lunch.set(Calendar.SECOND, 0);

            Calendar dinner      = Calendar.getInstance();
            dinner.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dinner.set(Calendar.HOUR_OF_DAY, 16);
            dinner.set(Calendar.MINUTE, 0);
            dinner.set(Calendar.SECOND, 0);

            if (cal.getTimeInMillis() < breakfast.getTimeInMillis() || (cal.getTimeInMillis() >= breakfast.getTimeInMillis() && cal.getTimeInMillis() < lunch.getTimeInMillis())) {
                setProgrammatically = true;
                logName.setText("Breakfast");
            }
            else if (cal.getTimeInMillis() >= lunch.getTimeInMillis() && cal.getTimeInMillis() < dinner.getTimeInMillis()) {
                setProgrammatically = true;
                logName.setText("Lunch");
            }
            else {
                setProgrammatically = true;
                logName.setText("Dinner");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void addIngredient (Ingredient ingredient) {
        if (ingredient.getName().equals("")) {return;}

        // Check to see if ingredient already exists
        ArrayList<Ingredient> ingredients = ((IngredientAdapter)listView.getAdapter()).ingredients;
        boolean bExists = false;
        for (Ingredient currentIngredient :
                ingredients) {
            if (currentIngredient.getName().equals(ingredient.getName())) {bExists = true;}
        }
        if (bExists) {return;}

        ((IngredientAdapter)listView.getAdapter()).ingredients.add(ingredient);
        ((IngredientAdapter) listView.getAdapter()).notifyDataSetChanged();

        mealLog.getIngredientsEaten().add(ingredient);

        checkEmpty();
    }

    private void addIngredients (Meal meal) {
        ingredientsAdapter = new IngredientAdapter(this, meal.getIngredients());
        listView.setAdapter(ingredientsAdapter);

        mealLog.setIngredientsEaten(meal.getIngredients());

        checkEmpty();
    }

    private void checkEmpty() {
        if (((IngredientAdapter)listView.getAdapter()).ingredients.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void loadIngredients() {
        // Create string adapter for meal dropdown
        ArrayList<String> ingredientStrings    = new ArrayList<>();
        ArrayList<Ingredient> ingredientsArray = Ingredient.getIngredients(this);
        for (Ingredient ingredient :
                ingredientsArray) {
            ingredientStrings.add(ingredient.getName());
        }

        ArrayAdapter<String> ingredientsAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ingredientStrings);
        ingredientsAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsSpinner.setAdapter(ingredientsAdapterSpinner);
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
        Ingredient ingredient = ingredientsAdapter.ingredients.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.delete:
                ingredientsAdapter.ingredients.remove(ingredient);
                ingredientsAdapter.notifyDataSetChanged();
                mealLog.getIngredientsEaten().remove(ingredient);

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
                // Make sure to validate the new meal log to make sure they've at least entered a name...
                if (mealLog.getName() == null || mealLog.getName().equals("")) {
                    Toast.makeText(EditMealLogActivity.this, "Please make sure to at least enter a name before trying to save.", Toast.LENGTH_LONG).show();return true;}

                if (isEdit) {
                    activity.updateMealLog(mealLog, previousName);
                }
                else {
                    activity.saveNewMealLog(mealLog);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadMeals() {
        MealSpinnerAdapter mealSpinnerAdapterSpinner = new MealSpinnerAdapter(this, Meal.getMeals(this));
        mealsSpinner.setAdapter(mealSpinnerAdapterSpinner);
    }

    public void toggleDropdown(View view) {
        if (ingredientTypeSpinner.getAdapter().getItem(ingredientTypeSpinner.getSelectedItemPosition()).toString().equals("Meals")) {
                String mealName       = ((Meal)mealsSpinner.getAdapter().getItem(mealsSpinner.getSelectedItemPosition())).getName();
                ArrayList<Meal> meals = Meal.getMeals(EditMealLogActivity.this);
                Meal meal = new Meal("");
                for (Meal currentMeal :
                        meals) {
                    if (currentMeal.getName().equals(mealName)) {meal = currentMeal;}
                }
                addIngredients(meal);
        }
        else {
                String ingredientName = (String)mealsSpinner.getAdapter().getItem(mealsSpinner.getSelectedItemPosition());
                ArrayList<Ingredient> ingredients = Ingredient.getIngredients(EditMealLogActivity.this);
                Ingredient ingredient = new Ingredient("");
                for (Ingredient currentIngredient :
                        ingredients) {
                    if (currentIngredient.getName().equals(ingredientName)) {ingredient = currentIngredient;}
                }
                addIngredient(ingredient);
        }
    }

    public void showDatePickerDialog(View view) {
        DatePickerFragment dateDialog = new DatePickerFragment();
        dateDialog.show(fm, "fragment_date");
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        calendar.set(year, month, day);

        // Start Time dialog
        TimePickerFragment timeDialog = new TimePickerFragment();
        timeDialog.show(fm, "fragment_time");
    }

    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE,      minute);

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy 'at' h:mm a");
        logTimeButton.setText(format.format(calendar.getTime()));

        mealLog.setTimeEaten(calendar);

        setLogName();
    }
}

