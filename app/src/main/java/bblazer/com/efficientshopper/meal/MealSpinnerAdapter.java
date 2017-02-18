package bblazer.com.efficientshopper.meal;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
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

public class MealSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    public ArrayList<Meal> meals;
    private static LayoutInflater inflater;

    public MealSpinnerAdapter(Activity activity, ArrayList<Meal> meals) {
        this.meals = meals;
        groupAndSortMeals();
        inflater   = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void groupAndSortMeals() {
        // Group them
        Map<String, ArrayList<Meal>> groupedMeals = new TreeMap<String, ArrayList<Meal>>();
        for (Meal meal :
                this.meals) {
            if (groupedMeals.containsKey(meal.getMealType())) {
                groupedMeals.get(meal.getMealType()).add(meal);
            }
            else {
                ArrayList<Meal> newGroup = new ArrayList<>();
                newGroup.add(meal);
                groupedMeals.put(meal.getMealType(), newGroup);
            }
        }
        
        this.meals = new ArrayList<>();
        
        // Sort the groups
        for (Map.Entry<String, ArrayList<Meal>> entry : groupedMeals.entrySet())
        {
            ArrayList<Meal> currentGroup = entry.getValue();
            Collections.sort(currentGroup, new MealComparator());
            for (Meal sortedMeal :
                    currentGroup) {
                this.meals.add(sortedMeal);
            }
        }
    }

    public class MealComparator implements Comparator<Meal> {
        @Override
        public int compare(Meal o1, Meal o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    @Override
    public int getCount() {
        return meals.size();
    }

    @Override
    public void notifyDataSetChanged() {
        groupAndSortMeals();
        super.notifyDataSetChanged();
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
        public LinearLayout groupHeader;
        public TextView  groupName;
        public TextView  mealName;
        public ImageView notesImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        Meal meal = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.meal_row, null);

            holder = new ViewHolder();
            holder.groupName = (TextView) vi.findViewById(R.id.group_header_name);
            holder.mealName = (TextView) vi.findViewById(R.id.meal_name);
            holder.notesImage = (ImageView) vi.findViewById(R.id.notes_image);
            holder.groupHeader = (LinearLayout) vi.findViewById(R.id.group_header);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        holder.groupName.setText(meal.getMealType());
        holder.mealName.setText(meal.getName());

        if (meal.getNotes() == null || meal.getNotes().equals("")) {
            holder.notesImage.setVisibility(View.INVISIBLE);
        }
        else {
            holder.notesImage.setVisibility(View.VISIBLE);
        }

        holder.groupHeader.setVisibility(View.GONE);

        return vi;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        Meal meal = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.meal_row, null);

            holder = new ViewHolder();
            holder.groupName = (TextView) vi.findViewById(R.id.group_header_name);
            holder.mealName = (TextView) vi.findViewById(R.id.meal_name);
            holder.notesImage = (ImageView) vi.findViewById(R.id.notes_image);
            holder.groupHeader = (LinearLayout) vi.findViewById(R.id.group_header);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        // Show or hide group header
        boolean showHeader = true;
        if (position >= 1) {
            Meal previousMeal = getItem(position - 1);
            if (previousMeal.getMealType().equals(meal.getMealType())) {
                showHeader = false;
            }
        }

        holder.groupName.setText(meal.getMealType());
        holder.mealName.setText(meal.getName());

        if (meal.getNotes() == null || meal.getNotes().equals("")) {
            holder.notesImage.setVisibility(View.INVISIBLE);
        }
        else {
            holder.notesImage.setVisibility(View.VISIBLE);
        }

        if (!showHeader) {
            holder.groupHeader.setVisibility(View.GONE);
        }
        else {
            holder.groupHeader.setVisibility(View.VISIBLE);
        }

        return vi;
    }
}
