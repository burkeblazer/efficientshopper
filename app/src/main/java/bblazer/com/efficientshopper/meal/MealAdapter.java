package bblazer.com.efficientshopper.meal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import bblazer.com.efficientshopper.R;


/**
 * Created by bblazer on 1/29/2017.
 */

public class MealAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<Meal> meals;
    private static LayoutInflater inflater;

    public MealAdapter(Activity activity, ArrayList<Meal> meals) {
        this.activity = activity;
        this.meals    = meals;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return meals.size();
    }

    @Override
    public Meal getItem(int position) {
        return meals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView mealName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        Meal meal = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.meal_row, null);

            holder = new ViewHolder();
            holder.mealName = (TextView) vi.findViewById(R.id.meal_name);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        holder.mealName.setText(meal.getName());

        return vi;
    }
}
