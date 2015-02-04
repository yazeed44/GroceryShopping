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
    private final Category mCategory;

    private QueryCategoryThread(final Category category) {
        mCategory = category;
        setName(mCategory.name + " thread");
    }

    public static ArrayList<Item> getItems(final Category category) {
        final QueryCategoryThread queryThread = new QueryCategoryThread(category);
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
        mItems = db.getItems(mCategory);
        db.closeDatabase();
    }
}
