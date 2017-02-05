package bblazer.com.efficientshopper.meal.log;

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
 * Created by bblazer on 2/1/2017.
 */
public class MealLogAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<MealLog> mealLogs;
    private static LayoutInflater inflater;

    public MealLogAdapter(Activity activity, ArrayList<MealLog> mealLogs) {
        this.activity  = activity;
        this.mealLogs = mealLogs;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mealLogs.size();
    }

    @Override
    public MealLog getItem(int position) {
        return mealLogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView mealLogName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        MealLog mealLog = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.meal_log_row, null);

            holder              = new ViewHolder();
            holder.mealLogName = (TextView) vi.findViewById(R.id.meal_log_name);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        holder.mealLogName.setText(mealLog.getName());

        return vi;
    }
}
