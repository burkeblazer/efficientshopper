package bblazer.com.efficientshopper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button editStores;
    private Button editMeals;
    private Button editPantry;
    private Button createList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editStores = (Button)findViewById(R.id.EditStores);
        editMeals  = (Button)findViewById(R.id.EditMeals);
        editPantry  = (Button)findViewById(R.id.EditPantry);
        createList  = (Button)findViewById(R.id.CreateList);

        editStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditStores();
            }
        });
        editMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditMeals();
            }
        });
        editPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPantry();
            }
        });
        createList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateList();
            }
        });
    }

    public void showEditStores() {
        Intent intent = new Intent(this, EditStoresActivity.class);
        startActivity(intent);
    }

    public void showEditMeals() {
        Intent intent = new Intent(this, EditMealsActivity.class);
        startActivity(intent);
    }

    public void showEditPantry() {
        Intent intent = new Intent(this, EditPantryActivity.class);
        startActivity(intent);
    }

    public void showCreateList() {
        Intent intent = new Intent(this, EditListActivity.class);
        startActivity(intent);
    }
}
