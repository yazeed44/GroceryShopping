package net.yazeed44.groceryshopping.utils;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;


/**
 * Created by yazeed44 on 12/31/14.
 */
public final class Item {
    public static final String DEFAULT_UNITS = "كيلو";
    public static final String DEFAULT_AMOUNT = "1";
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
        setAmount(defaultAmount);
        key = mCount++;
    }

    public Item(String name) {
        this.name = name;
        units = generateUnits(DEFAULT_UNITS);
        setChosenUnit(units.get(0));
        setAmount(DEFAULT_AMOUNT);
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
        if (TextUtils.isEmpty(mAmount)) {
            mAmount = DEFAULT_AMOUNT;
        }
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
