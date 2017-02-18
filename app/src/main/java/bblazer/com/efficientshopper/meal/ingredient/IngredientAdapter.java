package bblazer.com.efficientshopper.meal.ingredient;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import bblazer.com.efficientshopper.R;

/**
 * Created by bblazer on 1/29/2017.
 */

public class IngredientAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<Ingredient> ingredients;
    private static LayoutInflater inflater;

    public IngredientAdapter(Activity activity, ArrayList<Ingredient> ingredients) {
        this.activity    = activity;
        this.ingredients = ingredients;
        groupAndSortIngredients();
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void groupAndSortIngredients() {
        // Group them
        Map<String, ArrayList<Ingredient>> groupedIngredients = new TreeMap<String, ArrayList<Ingredient>>();
        for (Ingredient ingredient :
                this.ingredients) {
            if (groupedIngredients.containsKey(ingredient.getDepartment().getName())) {
                groupedIngredients.get(ingredient.getDepartment().getName()).add(ingredient);
            }
            else {
                ArrayList<Ingredient> newGroup = new ArrayList<>();
                newGroup.add(ingredient);
                groupedIngredients.put(ingredient.getDepartment().getName(), newGroup);
            }
        }

        this.ingredients = new ArrayList<>();

        // Sort the groups
        for (Map.Entry<String, ArrayList<Ingredient>> entry : groupedIngredients.entrySet())
        {
            ArrayList<Ingredient> currentGroup = entry.getValue();
            Collections.sort(currentGroup, new IngredientComparator());
            for (Ingredient sortedIngredient :
                    currentGroup) {
                this.ingredients.add(sortedIngredient);
            }
        }
    }

    public class IngredientComparator implements Comparator<Ingredient> {
        @Override
        public int compare(Ingredient o1, Ingredient o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public Ingredient getItem(int position) {
        return ingredients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView ingredientName;
        public TextView amount;
        public Button upArrow;
        public Button downArrow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        IngredientAdapter.ViewHolder holder;
        final Ingredient ingredient = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.ingredient_row, null);

            holder = new IngredientAdapter.ViewHolder();
            holder.ingredientName = (TextView) vi.findViewById(R.id.ingredient_name);
            holder.amount = (TextView) vi.findViewById(R.id.amount);
            holder.upArrow = (Button) vi.findViewById(R.id.up_arrow);
            holder.downArrow = (Button) vi.findViewById(R.id.down_arrow);

            holder.upArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ingredient.setAmount(ingredient.getAmount() + 1);
                    notifyDataSetChanged();

                    // Hack
                    if (activity.getClass().toString().equals("class bblazer.com.efficientshopper.meal.ingredient.EditPantryActivity")) {((EditPantryActivity)activity).updateIngredient(ingredient, ingredient.getName());}
                }
            });

            holder.downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ingredient.getAmount() == 0) {return;}
                    ingredient.setAmount(ingredient.getAmount() - 1);
                    notifyDataSetChanged();

                    // Hack
                    if (activity.getClass().toString().equals("class bblazer.com.efficientshopper.meal.ingredient.EditPantryActivity")) {((EditPantryActivity)activity).updateIngredient(ingredient, ingredient.getName());}
                }
            });

            vi.setTag(holder);
        }
        else {
            holder = (IngredientAdapter.ViewHolder)vi.getTag();
        }

        holder.ingredientName.setText(ingredient.getName());
        String amount = "0";
        if (ingredient.getAmount() > 0) {
            amount = Integer.toString(ingredient.getAmount());
        }
        holder.amount.setText(amount);

        return vi;
    }
}