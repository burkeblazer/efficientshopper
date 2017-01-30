package bblazer.com.efficientshopper;

import android.os.DeadObjectException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import bblazer.com.efficientshopper.meal.Ingredient;
import bblazer.com.efficientshopper.meal.Meal;
import bblazer.com.efficientshopper.shoppinglist.ShoppingList;
import bblazer.com.efficientshopper.shoppinglist.ShoppingListAdapterCheck;
import bblazer.com.efficientshopper.store.Department;
import bblazer.com.efficientshopper.store.Store;

import static bblazer.com.efficientshopper.meal.AddNewIngredientActivity.ingredient;

public class ViewShoppingListActivity extends AppCompatActivity {
    private Spinner storeSpinner;
    private ListView listView;

    public static ShoppingList shoppingList;
    private ShoppingListAdapterCheck listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Generate Shopping List");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        storeSpinner = (Spinner) findViewById(R.id.store_spinner);
        listView     = (ListView)findViewById(R.id.list_view);

        listAdapter  = new ShoppingListAdapterCheck(this, new ArrayList<Ingredient>());
        listView.setAdapter(listAdapter);

        // Create string adapter for store dropdown
        ArrayList<Store> stores             = Store.getStores(this);
        ArrayList<String> storeArrayStrings = new ArrayList<String>();
        storeArrayStrings.add(0, "");

        for (Store store :
                stores) {
            storeArrayStrings.add(store.getName());
        }

        final ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, storeArrayStrings);
        storeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        storeSpinner.setAdapter(storeAdapter);

        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String storeName = storeAdapter.getItem(position);
                if (storeName == null || storeName.equals("")) {return;}
                setStore(storeName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void setStore(String storeName) {
        ArrayList<Store> stores = Store.getStores(this);
        Store store = new Store("");
        for (Store currentStore :
                stores) {
            if (currentStore.getName().equals(storeName)) {store = currentStore;}
        }

        // Grab the departments of the store and the ingredients of the meals
        ArrayList<Department> departments = store.getDepartments();
        ArrayList<Meal> meals             = shoppingList.getMeals();
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (Meal meal :
                meals) {
            ArrayList<Ingredient> mealIngredients = meal.getIngredients();
            for (Ingredient ingredient :
                    mealIngredients) {
                boolean bExists = false;
                for (Ingredient shoppListIngredient :
                        ingredients) {
                    if (shoppListIngredient.getName().equals(ingredient.getName())) {
                        bExists = true;
                    }
                }

                if (bExists) {
                    for (Ingredient shoppListIngredient :
                            ingredients) {
                        if (shoppListIngredient.getName().equals(ingredient.getName())) {
                            shoppListIngredient.setAmount(shoppListIngredient.getAmount()+ingredient.getAmount());
                        }
                    }
                }
                else {
                    Ingredient clonedIngredient = Ingredient.clone(ingredient);

                    for (Department department :
                            departments) {
                        if (department.getName().equals(clonedIngredient.getDepartment())) {clonedIngredient.setSortOrder(department.getSortNumber());}
                    }

                    ingredients.add(clonedIngredient);
                }
            }
        }

        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getSortOrder() - o2.getSortOrder();
            }
        });

        displayIngredients(ingredients);
    }

    private void displayIngredients(ArrayList<Ingredient> ingredients) {
        listAdapter  = new ShoppingListAdapterCheck(this, ingredients);
        listView.setAdapter(listAdapter);
    }
}
