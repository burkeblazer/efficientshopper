package bblazer.com.efficientshopper.event;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import bblazer.com.efficientshopper.R;

/**
 * Created by bblazer on 2/16/2017.
 */

public class Event {
    private String type;
    private Calendar created;
    private String description;

    public Event() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static ArrayList<Event> getEvents(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        String eventsJSON             = preferences.getString(context.getApplicationContext().getResources().getString(R.string.events_json), "");
        if (eventsJSON == null || eventsJSON.equals("")) {return new ArrayList<Event>();}

        Type listOfTestObject = new TypeToken<ArrayList<Event>>(){}.getType();
        Gson gson = new Gson();
        ArrayList<Event> events = gson.fromJson(eventsJSON, listOfTestObject);

        return events;
    }

    public static void addEvent(Context context, Event event) {
        Gson gson = new Gson();
        ArrayList<Event> events = getEvents(context);
        events.add(event);
        Type listOfTestObject = new TypeToken<ArrayList<Event>>(){}.getType();
        String json = gson.toJson(events, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.events_json), json);
        editor.commit();
    }

    public static void removeEvent(Activity context, String eventName) {
        Gson gson = new Gson();
        ArrayList<Event> events = getEvents(context);

        // Find the event
        int index = -1;
        for (int ct = 0; ct < events.size(); ct++) {
            if (events.get(ct).getDescription().equals(eventName)) {
                index = ct;
            }
        }

        if (index == -1) {return;}

        events.remove(index);
        Type listOfTestObject = new TypeToken<ArrayList<Event>>(){}.getType();
        String json = gson.toJson(events, listOfTestObject);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(context.getString(R.string.events_json), json);
        editor.commit();
    }

    public String getReadableTime() {
        return String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", this.created);
    }
}
