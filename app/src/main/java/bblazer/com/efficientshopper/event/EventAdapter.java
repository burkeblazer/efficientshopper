package bblazer.com.efficientshopper.event;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import bblazer.com.efficientshopper.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import bblazer.com.efficientshopper.R;

/**
 * Created by bblazer on 1/29/2017.
 */

public class EventAdapter extends BaseAdapter {
    private Activity activity;
    public ArrayList<Event> events;
    private static LayoutInflater inflater;

    public EventAdapter(Activity activity, ArrayList<Event> events) {
        this.activity = activity;
        this.events   = events;
        Collections.sort(this.events, new EventComparator());
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class EventComparator implements Comparator<Event> {
        @Override
        public int compare(Event o1, Event o2) {
            return o2.getCreated().compareTo(o1.getCreated());
        }
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder{
        public ImageView eventTypeImage;
        public TextView  description;
        public TextView  time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        EventAdapter.ViewHolder holder;
        final Event event = getItem(position);

        if (convertView == null) {
            vi = inflater.inflate(R.layout.event_row, parent, false);

            holder                = new EventAdapter.ViewHolder();
            holder.eventTypeImage = (ImageView) vi.findViewById(R.id.event_type_icon);
            holder.description    = (TextView) vi.findViewById(R.id.event_description);
            holder.time           = (TextView) vi.findViewById(R.id.event_time);

            vi.setTag(holder);
        }
        else {
            holder = (EventAdapter.ViewHolder)vi.getTag();
        }

        if (event.getType().equals("Ingredient")) {
            holder.eventTypeImage.setBackgroundResource(R.drawable.pantry_icon);
        }
        else if (event.getType().equals("Meal Plan")) {
            holder.eventTypeImage.setBackgroundResource(R.drawable.meal_plan_icon);
        }
        else if (event.getType().equals("Meal Log")) {
            holder.eventTypeImage.setBackgroundResource(R.drawable.meal_log_icon);
        }
        else if (event.getType().equals("Store")) {
            holder.eventTypeImage.setBackgroundResource(R.drawable.cart_icon);
        }
        holder.description.setText(event.getDescription());
        holder.time.setText(event.getReadableTime());

        return vi;
    }
}