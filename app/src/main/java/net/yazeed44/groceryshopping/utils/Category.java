package net.yazeed44.groceryshopping.utils;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yazeed44 on 12/31/14.
 */
public final class Category {

    public final String name;
    public final int thumbnailRes;
    public final String tableName;
    private ArrayList<Item> mItems;

    public Category(final String tableName, final String name, final int thumbnailRes) {
        this.tableName = tableName;
        this.name = name;
        this.thumbnailRes = thumbnailRes;

    }

    public ArrayList<Item> getItems() {

        if (mItems != null) {
            return mItems;
        }
        mItems = DBUtil.getItems(this);
        Log.d("getItems", mItems.toString());

        return mItems;
    }

    @Override
    public String toString() {
        return name;
    }

    public void resetItems() {
        if (mItems != null)
            mItems = null;
    }
}
