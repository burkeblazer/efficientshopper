package bblazer.com.efficientshopper.meal.ingredient;

import java.util.ArrayList;

/**
 * Created by bblazer on 2/28/2017.
 */
public class UOM {
    private String name;
    private String abbr;

    public UOM(String name, String abbr) {
        this.name = name;
        this.abbr = abbr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public static ArrayList<UOM> getAll() {
        ArrayList<UOM> uoms = new ArrayList<>();
        uoms.add(new UOM("Pound", "lb"));

        return uoms;
    }
}
