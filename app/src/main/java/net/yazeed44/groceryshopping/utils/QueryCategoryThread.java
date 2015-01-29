package net.yazeed44.groceryshopping.utils;

import net.yazeed44.groceryshopping.database.ItemsDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yazeed44 on 1/6/15.
 */
public class QueryCategoryThread extends Thread {

    private static ArrayList<Item> mItems;
    private final String mCategoryName;

    private QueryCategoryThread(final String categoryName) {
        mCategoryName = categoryName;
        setName(mCategoryName + " thread");
    }

    public static ArrayList<Item> getItems(final String categoryName) {
        final QueryCategoryThread queryThread = new QueryCategoryThread(categoryName);
        ThreadUtil.startAndJoin(queryThread);
        sortItems();
        return mItems;
    }

    private static void sortItems() {
        Collections.sort(mItems, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });
    }

    @Override
    public void run() {
        final ItemsDB db = ItemsDB.getInstance();
        db.openDatabase();
        mItems = db.getItems(mCategoryName);
        db.closeDatabase();
    }
}
