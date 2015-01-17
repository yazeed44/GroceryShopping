package net.yazeed44.groceryshopping.utils;

import java.io.Serializable;

/**
 * Created by yazeed44 on 12/31/14.
 */
public final class Item implements Serializable {

    private static int count = 0;
    public final String name;
    public final String combination;
    public final int key;

    public Item(String name, String combination) {
        this.name = name;
        this.combination = combination;
        key = count++;
    }

    @Override
    public String toString() {
        return name;
    }


}
