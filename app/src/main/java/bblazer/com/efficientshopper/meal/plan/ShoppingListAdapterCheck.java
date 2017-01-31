package bblazer.com.efficientshopper.meal.plan;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import bblazer.com.efficientshopper.R;
import bblazer.com.efficientshopper.meal.ingredient.Ingredient;

/**
 * Created by bblazer on 1/29/2017.
 */
public class ShoppingListAdapterCheck extends BaseAdapter {
    private Activity activity;
    public ArrayList<Ingredient> ingredients;
    private static LayoutInflater inflater;

    public ShoppingListAdapterCheck(Activity activity, ArrayList<Ingredient> ingredients) {
        this.activity    = activity;
        this.ingredients = ingredients;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        public CheckBox checkbox;
        public TextView amount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ShoppingListAdapterCheck.ViewHolder holder;
        final Ingredient ingredient = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.check_ingredient_row, null);

            holder                = new ShoppingListAdapterCheck.ViewHolder();
            holder.ingredientName = (TextView) vi.findViewById(R.id.ingredient_name);
            holder.checkbox       = (CheckBox) vi.findViewById(R.id.checkbox);
            holder.amount         = (TextView) vi.findViewById(R.id.amount);

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    ingredient.setChecked(isChecked);
                }
            });

            vi.setTag(holder);
        }
        else {
            holder = (ShoppingListAdapterCheck.ViewHolder)vi.getTag();
        }

        holder.ingredientName.setText(ingredient.getName());
        holder.amount.setText(Integer.toString(ingredient.getAmount()));

        return vi;
    }
}