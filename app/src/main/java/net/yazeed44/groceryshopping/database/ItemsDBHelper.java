package net.yazeed44.groceryshopping.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.yazeed44.groceryshopping.utils.DBUtil;

/**
 * Created by yazeed44 on 12/31/14.
 */
public class ItemsDBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "shoppingItems.db";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COMBINATION = "combination";
    private final Context mContext;
    private SQLiteDatabase db;


    private ItemsDBHelper(Context context, final int version) {
        super(context, DB_NAME, null, version);
        mContext = context;
    }

    public static ItemsDBHelper createInstance(final Context context) {

        int version = DBUtil.getLocalDBVersion(context);
        if (version <= 0) {
            Log.w("create Helper instance", "Local db version is smaller than 0");
            version = 1;
        }
        return new ItemsDBHelper(context, version);
    }

    public void createEmptyDB() {

        if (DBUtil.localDBExists(mContext)) {
            //DB Already created
        } else {
            getReadableDatabase();
        }

    }

    @Override
    public synchronized void close() {

        if (db != null)
            db.close();

        super.close();

    }

    public void openDataBase() {

        db = SQLiteDatabase.openDatabase(DBUtil.getLocalDBPath(mContext), null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

