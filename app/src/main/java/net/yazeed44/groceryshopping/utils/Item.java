package net.yazeed44.groceryshopping.utils;

import java.util.Arrays;
import java.util.List;


/**
 * Created by yazeed44 on 12/31/14.
 */
public final class Item {
    private static int mCount = 0;
    public final String name;
    public final List<String> units;
    public final int key;
    private String mAmount;
    private String mChosenUnit;

    public Item(String name, String units, String defaultAmount) {
        this.name = name;
        this.units = generateUnits(units);
        setChosenUnit(this.units.get(0));
        this.mAmount = defaultAmount;
        key = mCount++;
    }

    private List<String> generateUnits(String units) {
        //Units will be something like this   unit1,unit2,unit3 .  so we need to fetch every unit then create an array to store them in
        return Arrays.asList(units.split("\\s*,\\s*"));
    }


    public String getAmount() {
        return mAmount;
    }

    public void setAmount(String amount) {
        this.mAmount = amount;
    }


    public String getChosenUnit() {
        return mChosenUnit;
    }

    public void setChosenUnit(String newUnit) {
        this.mChosenUnit = newUnit;
    }

    @Override
    public String toString() {
        return name;
    }
}
