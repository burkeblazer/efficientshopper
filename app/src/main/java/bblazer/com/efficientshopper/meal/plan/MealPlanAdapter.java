package bblazer.com.efficientshopper.meal.plan;

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

public class MealPlanAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<MealPlan> mealPlans;
    private static LayoutInflater inflater;

    public MealPlanAdapter(Activity activity, ArrayList<MealPlan> mealPlans) {
        this.activity  = activity;
        this.mealPlans = mealPlans;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mealPlans.size();
    }

    @Override
    public MealPlan getItem(int position) {
        return mealPlans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView mealPlanName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        MealPlan mealPlan = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_row, null);

            holder              = new ViewHolder();
            holder.mealPlanName = (TextView) vi.findViewById(R.id.meal_plan_name);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        holder.mealPlanName.setText(mealPlan.getName());

        return vi;
    }
}
