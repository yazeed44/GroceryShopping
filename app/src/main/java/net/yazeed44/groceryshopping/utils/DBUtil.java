package net.yazeed44.groceryshopping.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.database.ItemsDB;
import net.yazeed44.groceryshopping.database.ItemsDBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by yazeed44 on 12/31/14.
 */
public final class DBUtil {

    public static final String AD_DOWNLOAD_URL = "";
    private static Context mContext;
    private static ArrayList<Category> mCategories;
    private static ArrayList<Item> mItems;
    private static Ad mAd;

    private DBUtil() {
        throw new AssertionError();
    }

    public static void initInstance(final Activity activity) {


        mContext = activity;
        getCategories();//Initialize


        ItemsDB.initInstance(ItemsDBHelper.createInstance(mContext));

        if (shouldInitialize(activity))
            new CheckDBTask(activity).execute();


    }

    private static boolean shouldInitialize(final Context context) {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final String hasInitializedKey = "hasInitializedKey";
        final boolean shouldInitialize = settings.getBoolean(hasInitializedKey, true);
        settings.edit().putBoolean(hasInitializedKey, false).apply();
        return shouldInitialize;
    }


    public static ArrayList<Category> getCategories() {
        if (mCategories != null) {
            return mCategories;
        }

        //I really really didn't want to hardcode this !!

        final Category[] categoriesArray = new Category[]{
                new Category("vegatablesFruit", "الخضروات والفواكه", R.drawable.vegatablesfruit),
                new Category("Grocery", "البقالة", R.drawable.grocery),
                new Category("Pharmacy", "الصيدلية", R.drawable.pharmacy),
                new Category("BakeryProducts", "المخبوزات", R.drawable.bakeryproducts),
                new Category("cleanTools", "أدوات التنظيف", R.drawable.cleantools),
                new Category("spices", "البهارات", R.drawable.spices),
                new Category("bookStore", "المكتبة", R.drawable.bookstore),

                new Category("Others", "أخرى", R.drawable.others)
        };

        mCategories = new ArrayList<>();


        Collections.addAll(mCategories, categoriesArray);

        return mCategories;

    }

    public static String getLocalDBPath(final Context context) {
        return context.getDatabasePath(ItemsDBHelper.DB_NAME).getAbsolutePath();
    }

    public static ArrayList<Item> getItems(final Category category) {
        return QueryCategoryThread.getItems(category);
    }

    public static ItemsDBHelper createEmptyDB() {
        final ItemsDBHelper helper = ItemsDBHelper.createInstance(mContext);
        helper.createEmptyDB();
        helper.openDataBase();
        return helper;
    }

    public static int getLocalDBVersion(final Context context) {

        return getDBVersion(getLocalDBPath(context));
    }

    public static int getDBVersion(final String path) {

        int version;

        try {
            final SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
            version = db.getVersion();
            db.close();
        } catch (SQLiteException ex) {
            version = -1;
            Log.e("getDBVersion", ex.getMessage());
        }

        return version;
    }

    public static boolean localDBExists(final Context context) {
        return new File(getLocalDBPath(context)).exists();
    }


    public static ArrayList<Item> getAllItems() {

        if (mItems != null) {
            return mItems;
        }

        mItems = new ArrayList<>();


        for (final Category category : getCategories()) {
            mItems.addAll(category.getItems());
        }

        Collections.sort(mItems, new Comparator<Item>() {
            @Override
            public int compare(Item lhs, Item rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });

        return mItems;
    }


    public static Ad getAd() {

        if (mAd != null) {
            return mAd;
        }
        //TODO Fetch image url and pdf url from txt file

        return null;
    }
}
