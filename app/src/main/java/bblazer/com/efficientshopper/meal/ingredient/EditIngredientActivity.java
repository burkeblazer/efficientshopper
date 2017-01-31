package bblazer.com.efficientshopper.meal.ingredient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.store.Department;

public class EditIngredientActivity extends AppCompatActivity {
    private EditText name;
    private EditText amount;
    private Spinner deptSpinner;

    public static Ingredient ingredient;
    public static EditPantryActivity activity;

    private boolean isEdit;
    private String previousName;
    private ArrayAdapter<String> deptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ingredient);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Create or set a ingredient object (this is how we know if we are editting or adding)
        if (ingredient == null) {
            ingredient = new Ingredient("");
            getSupportActionBar().setTitle("Add Ingredient");
        }
        else {
            isEdit       = true;
            ingredient   = Ingredient.clone(ingredient);
            previousName = ingredient.getName();
            getSupportActionBar().setTitle("Edit Ingredient");
        }

        name        = (EditText)findViewById(R.id.ingredient_name);
        amount      = (EditText)findViewById(R.id.amount);
        deptSpinner = (Spinner)findViewById(R.id.dept_spinner);

        // Add a listener for the ingredient name textfield on change
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ingredient.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Add a listener for the ingredient amount numberfield on change
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {ingredient.setAmount(0);return;}
                ingredient.setAmount(Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Create string adapter for dept dropdown
        ArrayList<String> deptArray  = Department.getDefaultDepartments();
        deptArray.add(0, "");
        deptAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, deptArray);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptSpinner.setAdapter(deptAdapter);

        deptSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String departmentName = deptAdapter.getItem(position);
                ingredient.setDepartment(new Department(departmentName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        if (isEdit) {
            loadIngredientData();
        }
    }

    private void loadIngredientData() {
        name.setText(ingredient.getName());
        amount.setText(Integer.toString(ingredient.getAmount()));
        deptSpinner.setSelection(deptAdapter.getPosition(ingredient.getDepartment().getName()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_ingredient_menu, menu);
        MenuItem update = menu.findItem(R.id.update_ingredient);
        MenuItem save = menu.findItem(R.id.save_ingredient);
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
            case R.id.update_ingredient:
            case R.id.save_ingredient:
                // Make sure to validate the new ingredient to make sure they've at least entered a name...
                if (ingredient.getName() == null || ingredient.getName().equals("")) {
                    Toast.makeText(EditIngredientActivity.this, "Please make sure to at least enter a name before trying to save.", Toast.LENGTH_LONG).show();return true;}

                if (isEdit) {
                    activity.updateIngredient(ingredient, previousName);
                }
                else {
                    activity.saveNewIngredient(ingredient);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
