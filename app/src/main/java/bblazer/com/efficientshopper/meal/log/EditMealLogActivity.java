package bblazer.com.efficientshopper.meal.log;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.media.Image;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.Meal;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;
import bblazer.com.efficientshopper.meal.ingredient.IngredientAdapter;

public class EditMealLogActivity extends AppCompatActivity implements
        DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener {
    private EditText logName;
    private Button logTimeButton;
    private Spinner mealsSpinner;
    private ListView listView;
    private RelativeLayout emptyView;
    private TextView dropdownLabel;

    public static MealLog mealLog;
    public static ViewMealLogsActivity activity;
    private IngredientAdapter ingredientsAdapter;

    private boolean isEdit;
    private String previousName;
    private Calendar calendar;
    FragmentManager fm;

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
        dropdownLabel = (TextView) findViewById(R.id.dropdown_label);

        // Create or set a store object (this is how we know if we are editting or adding)
        if (mealLog == null) {
            mealLog = new MealLog();
            getSupportActionBar().setTitle("Add Meal Log");
        }
        else {
            isEdit       = true;
            mealLog      = MealLog.clone(mealLog);
            previousName = mealLog.getName();
            getSupportActionBar().setTitle("Edit Meal Log");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        calendar = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy 'at' h:mm a");
        logTimeButton.setText(format.format(calendar.getTime()));

        fm = getSupportFragmentManager();

        // Add a listener for the ingredient amount numberfield on change
        logName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mealLog.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ingredientsAdapter = new IngredientAdapter(this, new ArrayList<Ingredient>());
        registerForContextMenu(listView);

        loadMeals();
    }

    private void addIngredient (Ingredient ingredient) {
        if (ingredient.getName().equals("")) {return;}
        ((IngredientAdapter)listView.getAdapter()).ingredients.add(ingredient);
        ((IngredientAdapter) listView.getAdapter()).notifyDataSetChanged();

        checkEmpty();
    }

    private void addIngredients (Meal meal) {
        ingredientsAdapter = new IngredientAdapter(this, meal.getIngredients());
        listView.setAdapter(ingredientsAdapter);

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
        ingredientStrings.add(0, "");
        ArrayList<Ingredient> ingredientsArray = Ingredient.getIngredients(this);
        for (Ingredient ingredient :
                ingredientsArray) {
            ingredientStrings.add(ingredient.getName());
        }

        ArrayAdapter<String> ingredientsAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ingredientStrings);
        ingredientsAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsSpinner.setAdapter(ingredientsAdapterSpinner);

        mealsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String ingredientName = (String)mealsSpinner.getAdapter().getItem(position);
                ArrayList<Ingredient> ingredients = Ingredient.getIngredients(EditMealLogActivity.this);
                Ingredient ingredient = new Ingredient("");
                for (Ingredient currentIngredient :
                        ingredients) {
                    if (currentIngredient.getName().equals(ingredientName)) {ingredient = currentIngredient;}
                }
                addIngredient(ingredient);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
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

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void loadMeals() {
        // Create string adapter for meal dropdown
        ArrayList<String> mealStrings = new ArrayList<>();
        mealStrings.add(0, "");
        ArrayList<Meal>   mealArray   = Meal.getMeals(this);
        for (Meal meal :
                mealArray) {
            mealStrings.add(meal.getName());
        }

        ArrayAdapter<String> mealsAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mealStrings);
        mealsAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsSpinner.setAdapter(mealsAdapterSpinner);

        mealsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String mealName = (String)mealsSpinner.getAdapter().getItem(position);
                ArrayList<Meal> meals = Meal.getMeals(EditMealLogActivity.this);
                Meal meal = new Meal("");
                for (Meal currentMeal :
                        meals) {
                    if (currentMeal.getName().equals(mealName)) {meal = currentMeal;}
                }
                addIngredients(meal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    public void toggleDropdown(View view) {
        if (dropdownLabel.getText().toString().equals("Meals:")) {
            loadIngredients();
            dropdownLabel.setText("Ingredients:");
        }
        else {
            loadMeals();
            dropdownLabel.setText("Meals:");
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
    }
}

class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener
{
    public interface DatePickerListener
    {
        public void onDateSet(int year, int month, int day);
    }

    private DatePickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (DatePickerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        listener.onDateSet(year, month, day);
    }
}

class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    public interface TimePickerListener
    {
        public void onTimeSet(int hourOfDay, int minute);
    }

    private TimePickerListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar c=Calendar.getInstance();
        int hour=c.get(Calendar.HOUR_OF_DAY);
        int minute=c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(),this,hour,minute, DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (TimePickerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        listener.onTimeSet(hourOfDay, minute);
    }
}
