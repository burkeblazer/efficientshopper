package bblazer.com.efficientshopper.shoppinglist;

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

public class ShoppingListAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<ShoppingList> lists;
    private static LayoutInflater inflater;

    public ShoppingListAdapter(Activity activity, ArrayList<ShoppingList> lists) {
        this.activity = activity;
        this.lists    = lists;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public ShoppingList getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public TextView listName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        ShoppingList shoppingList = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.list_row, null);

            holder = new ViewHolder();
            holder.listName = (TextView) vi.findViewById(R.id.list_name);

            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder)vi.getTag();
        }

        holder.listName.setText(shoppingList.getName());

        return vi;
    }
}
