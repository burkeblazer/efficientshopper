package bblazer.com.efficientshopper.meal.ingredient;

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

public class EditPantryActivity extends AppCompatActivity {
    private RelativeLayout emptyView;
    private ListView listView;

    private ArrayList<Ingredient> ingredients;
    private IngredientAdapter ingredientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pantry);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Pantry");
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
                addNewIngredient();
            }
        });

        ingredients = Ingredient.getIngredients(this);

        emptyView         = (RelativeLayout)findViewById(R.id.empty_view);
        listView          = (ListView)findViewById(R.id.list_view);
        ingredientAdapter = new IngredientAdapter(this, ingredients);
        listView.setAdapter(ingredientAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient selIngredient = ingredientAdapter.getItem(position);
                editIngredient(selIngredient);
            }
        });

        registerForContextMenu(listView);

        checkEmpty();
    }

    private void checkEmpty() {
        if (ingredients.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void editIngredient(Ingredient ingredient) {
        Intent intent = new Intent(this, EditIngredientActivity.class);
        EditIngredientActivity.ingredient    = ingredient;
        EditIngredientActivity.activity = this;
        startActivity(intent);
    }

    private void addNewIngredient() {
        Intent intent = new Intent(this, EditIngredientActivity.class);
        EditIngredientActivity.ingredient    = null;
        EditIngredientActivity.activity = this;
        startActivity(intent);
    }

    public void updateIngredient(Ingredient ingredient, String previousName) {
        // Find the previous ingredient and update it
        for (Ingredient previousIngredient :
                ingredients) {
            if (previousName.equals(previousIngredient.getName())) {
                previousIngredient.updateFrom(ingredient);
            }
        }

        if (!previousName.equals(ingredient.getName())) {
            Meal.checkIngredientUpdate(previousName, ingredient.getName(), this);
        }

        Ingredient.removeIngredient(this, previousName);
        Ingredient.addIngredient(this, ingredient);
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
        Ingredient ingredient = ingredients.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                editIngredient(ingredient);
                return true;
            case R.id.delete:
                Ingredient.removeIngredient(this, ingredient.getName());
                ingredientAdapter.ingredients.remove(ingredient);
                ingredientAdapter.notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void saveNewIngredient(Ingredient ingredient) {
        // Make sure they aren't trying to add a ingredient that already exists
        boolean bExists = false;
        for (Ingredient currentIngredient :
                ingredients) {
            if (currentIngredient.getName().equals(ingredient.getName())) {bExists = true;}
        }

        if (bExists) {
            Toast.makeText(this, "Found a duplicate ingredient, please make sure to add ingredients only once", Toast.LENGTH_LONG).show();return;}

        Ingredient.addIngredient(this, ingredient);
        ingredientAdapter.ingredients.add(ingredient);
        ingredientAdapter.notifyDataSetChanged();

        checkEmpty();
    }
}
