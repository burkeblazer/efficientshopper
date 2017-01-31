package bblazer.com.efficientshopper.meal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;
import bblazer.com.efficientshopper.meal.ingredient.IngredientAdapter;

public class AddNewMealActivity extends AppCompatActivity {
    private EditText mealName;
    private ListView ingredients;
    private Spinner ingredientsSpinner;
    private ImageButton addIngredientButton;
    private RelativeLayout emptyView;
    private EditText notes;
    private Spinner mealTypeSpinner;

    public static Meal meal;
    public static EditMealsActivity activity;
    private boolean isEdit = false;
    private String previousName = "";
    private ArrayAdapter<String> mealTypeAdapterSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_meal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Create or set a store object (this is how we know if we are editting or adding)
        if (meal == null) {
            meal = new Meal("");
            getSupportActionBar().setTitle("Add Meal");
        }
        else {
            isEdit       = true;
            meal         = Meal.clone(meal);
            previousName = meal.getName();
            getSupportActionBar().setTitle("Edit Meal");
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mealName            = (EditText)findViewById(R.id.meal_name);
        ingredients         = (ListView)findViewById(R.id.list_view);
        ingredientsSpinner  = (Spinner)findViewById(R.id.ingredients_spinner);
        addIngredientButton = (ImageButton)findViewById(R.id.add_ingredients_button);
        emptyView           = (RelativeLayout)findViewById(R.id.empty_view);
        notes               = (EditText)findViewById(R.id.notes);
        mealTypeSpinner     = (Spinner)findViewById(R.id.meal_type_spinner);

        // Add a listener for the ingredient amount numberfield on change
        mealName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                meal.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                meal.setNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Create string adapter for meal type dropdown
        ArrayList<String> mealTypeString = Meal.getMealTypes();
        mealTypeString.add(0, "");
        mealTypeAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mealTypeString);
        mealTypeAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(mealTypeAdapterSpinner);

        mealTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String mealTypeName = mealTypeAdapterSpinner.getItem(position);
                meal.setMealType(mealTypeName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        // Create string adapter for dept dropdown
        ArrayList<String> ingredientsStr  = new ArrayList<String>();
        ArrayList<Ingredient> ingredientsArray = Ingredient.getIngredients(this);
        for (Ingredient ingredient :
                ingredientsArray) {
            ingredientsStr.add(ingredient.getName());
        }

        ArrayAdapter<String> ingredientAdapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ingredientsStr);
        ingredientAdapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ingredientsSpinner.setAdapter(ingredientAdapterSpinner);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addIngredient();
            }
        });

        IngredientAdapter ingredientAdapter = new IngredientAdapter(this, meal.getIngredients());
        ingredients.setAdapter(ingredientAdapter);

        if (isEdit) {
            loadMealData();
        }

        registerForContextMenu(ingredients);

        checkEmpty();
    }

    private void checkEmpty() {
        if (meal.getIngredients().size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void loadMealData() {
        mealName.setText(meal.getName());
        notes.setText(meal.getNotes());
        mealTypeSpinner.setSelection(mealTypeAdapterSpinner.getPosition(meal.getMealType()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_meal_menu, menu);
        MenuItem update = menu.findItem(R.id.update_meal);
        MenuItem save = menu.findItem(R.id.save_meal);
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
            case R.id.update_meal:
            case R.id.save_meal:
                // Make sure to validate the new meal to make sure they've at least entered a name...
                if (meal.getName() == null || meal.getName().equals("")) {
                    Toast.makeText(AddNewMealActivity.this, "Please make sure to at least enter a name before trying to save.", Toast.LENGTH_LONG).show();return true;}

                if (isEdit) {
                    activity.updateMeal(meal, previousName);
                }
                else {
                    activity.saveNewMeal(meal);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        Ingredient ingredient = meal.getIngredients().get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                return true;
            case R.id.delete:
                ((IngredientAdapter)ingredients.getAdapter()).ingredients.remove(ingredient);
                ((IngredientAdapter)ingredients.getAdapter()).notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void addIngredient() {
        // Create a new meal object from the string selected in the spinner
        ArrayList<Ingredient> ingredientsArray = Ingredient.getIngredients(this);
        String selectedIngredient              = ingredientsSpinner.getSelectedItem().toString();
        Ingredient ingredientObj = new Ingredient("");
        for (Ingredient ingredient :
                ingredientsArray) {
            if (ingredient.getName().equals(selectedIngredient)) {
                ingredientObj = ingredient;
            }
        }

        // Make sure this ingredient doesn't already exist
        boolean bExists = false;
        for (Ingredient ingredient :
                meal.getIngredients()) {
            if (ingredient.getName().equals(ingredientObj.getName())) {bExists = true;}
        }

        // Display error
        if (bExists) {Toast.makeText(this, "Ingredient already exists, please modify the currently added one.", Toast.LENGTH_LONG).show();return;}

        // Add the ingredient to the meal
        meal.addIngredient(ingredientObj);

        // Add the ingredient to the listview
        ((IngredientAdapter)ingredients.getAdapter()).notifyDataSetChanged();
        checkEmpty();
    }
}
