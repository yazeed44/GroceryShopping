package net.yazeed44.groceryshopping.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.util.Log;

import net.yazeed44.groceryshopping.utils.Item;

import java.util.ArrayList;

/**
 * Created by yazeed44 on 12/31/14.
 */
public class ItemsDB {

    private static ItemsDBHelper mHelper;
    private static ItemsDB mInstance;
    private SQLiteDatabase mDB;
    private int mOpenCounter;

    public static synchronized void initInstance(ItemsDBHelper helper) {

        if (mInstance == null) {
            mInstance = new ItemsDB();
        }
        mHelper = helper;

    }

    public static synchronized ItemsDB getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(ItemsDB.class.getName() +
                    " is not initialized, call initInstance(..) method first.");
        }
        if (Thread.currentThread().getName().equals(Looper.getMainLooper().getThread().getName())) {
            throw new IllegalStateException("Don't use main thread to access the database !!");
        }
        Log.i("ItemsDB : getInstance", Thread.currentThread().getName() + " is gonna take an instance!");
        return mInstance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if (mOpenCounter == 1) {
// Opening new database
            mDB = mHelper.getWritableDatabase();
        }
        Log.i("ItemsDB : openDatabase", Thread.currentThread().getName() + " is gonna open it's database");
        return mDB;
    }

    public synchronized void closeDatabase() {
        mOpenCounter--;
        if (mOpenCounter == 0) {
// Closing database
            mDB.close();
            Log.i("ItemsDB : closeDatabase", Thread.currentThread().getName() + " did close it's database");
        }
    }

    public ArrayList<Item> getItems(final String categoryName) {
        final ArrayList<Item> items = new ArrayList<>();

        final Cursor cursor = mDB.rawQuery("SELECT * FROM " + categoryName, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            items.add(getItem(cursor));

            cursor.moveToNext();
        }

        Log.d("Local DB Version", mDB.getVersion() + "");

        return items;
    }

    private Item getItem(final Cursor cursor) {
        final String name = cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_NAME));
        final String combination = cursor.getString(cursor.getColumnIndex(ItemsDBHelper.COLUMN_COMBINATION));

        return new Item(name, combination);
    }
}
