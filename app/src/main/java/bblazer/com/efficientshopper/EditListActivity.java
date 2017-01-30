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

import bblazer.com.efficientshopper.shoppinglist.ShoppingList;
import bblazer.com.efficientshopper.shoppinglist.ShoppingListAdapter;

public class EditListActivity extends AppCompatActivity {
    private RelativeLayout emptyView;
    private ListView listView;

    private ArrayList<ShoppingList> lists;
    private ShoppingListAdapter shoppingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Shopping Lists");
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
                addNewShoppingList();
            }
        });

        lists               = ShoppingList.getShoppingLists(this);

        emptyView           = (RelativeLayout)findViewById(R.id.empty_view);
        listView            = (ListView)findViewById(R.id.list_view);
        shoppingListAdapter = new ShoppingListAdapter(this, lists);
        listView.setAdapter(shoppingListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ShoppingList selList = shoppingListAdapter.getItem(position);
                editShoppingList(selList);
            }
        });

        registerForContextMenu(listView);

        checkEmpty();
    }

    private void checkEmpty() {
        if (lists.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    private void editShoppingList(ShoppingList shoppingList) {
        Intent intent = new Intent(this, AddNewShoppingListActivity.class);
        AddNewShoppingListActivity.shoppingList    = shoppingList;
        AddNewShoppingListActivity.activity = this;
        startActivity(intent);
    }

    private void addNewShoppingList() {
        Intent intent = new Intent(this, AddNewShoppingListActivity.class);
        AddNewShoppingListActivity.shoppingList    = null;
        AddNewShoppingListActivity.activity = this;
        startActivity(intent);
    }

    public void updateShoppingList(ShoppingList shoppingList, String previousName) {
        // Find the previous shoppingList and update it
        for (ShoppingList previousShoppingList :
                lists) {
            if (previousName.equals(previousShoppingList.getName())) {
                previousShoppingList.updateFrom(shoppingList);
            }
        }

        ShoppingList.removeShoppingList(this, previousName);
        ShoppingList.addShoppingList(this, shoppingList);
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
        ShoppingList shoppingList = lists.get(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
        switch(item.getItemId()) {
            case R.id.edit:
                editShoppingList(shoppingList);
                return true;
            case R.id.delete:
                ShoppingList.removeShoppingList(this, shoppingList.getName());
                shoppingListAdapter.lists.remove(shoppingList);
                shoppingListAdapter.notifyDataSetChanged();

                checkEmpty();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void saveNewShoppingList(ShoppingList shoppingList) {
        // Make sure they aren't trying to add a shoppingList that already exists
        boolean bExists = false;
        for (ShoppingList currentShoppingList :
                lists) {
            if (currentShoppingList.getName().equals(shoppingList.getName())) {bExists = true;}
        }

        if (bExists) {
            Toast.makeText(this, "Found a duplicate shoppingList, please make sure to add shoppingLists only once", Toast.LENGTH_LONG).show();return;}

        ShoppingList.addShoppingList(this, shoppingList);
        shoppingListAdapter.lists.add(shoppingList);
        shoppingListAdapter.notifyDataSetChanged();

        checkEmpty();
    }
}
