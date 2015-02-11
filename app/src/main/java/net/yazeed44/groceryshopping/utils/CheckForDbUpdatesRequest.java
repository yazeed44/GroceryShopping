package net.yazeed44.groceryshopping.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;

import net.yazeed44.groceryshopping.database.ItemsDBHelper;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by yazeed44 on 1/6/15.
 */
public class CheckForDbUpdatesRequest extends SpiceRequest<CheckForDbUpdatesRequest.Result> {

    public static final String DB_DOWNLOAD_URL = "https://www.dropbox.com/s/miid75944sge2lg/shoppingItems.db?dl=1";
    public static final String DB_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + ItemsDBHelper.DB_NAME;
    private static String mLocalDBPath;
    private WeakReference<Context> mWeakReferenceContext;

    public CheckForDbUpdatesRequest(final Context context) {
        super(CheckForDbUpdatesRequest.Result.class);
        init(context);
    }

    private void init(final Context context) {
        mWeakReferenceContext = new WeakReference<>(context);
        mLocalDBPath = DBUtil.getLocalDBPath(mWeakReferenceContext.get());

    }

    @Override
    public Result loadDataFromNetwork() throws Exception {


        if (DBUtil.localDBExists(mWeakReferenceContext.get())) {
            if (LoadUtil.isNetworkAvailable(mWeakReferenceContext.get()) && newUpdateExists()) {
                //There's new update
                return Result.NEW_UPDATE;
            }
        } else {
            //Download and install database
            return Result.NO_DB;
        }


        return Result.NO_NEW_UPDATE;
    }

    private void deleteDownloadedDB() {
        new File(DB_DOWNLOAD_PATH).delete();
    }

    private boolean newUpdateExists() {

        //Download new database then compare versions between the current one and the downloaded one
        //TODO Think of new method


        final String newDatabasePath = downloadDatabase();

        boolean newUpdateExists;
        if (LoadUtil.isDownloadedFileValid(newDatabasePath)) {
            return false;
        }

        newUpdateExists = getDBVersion(newDatabasePath) > getDBVersion(mLocalDBPath);
        Log.d("isThereNewUpdate", newUpdateExists + "");

        if (!newUpdateExists) {
            //Delete the downloaded db
            deleteDownloadedDB();
        }


        return newUpdateExists;

    }

    private int getDBVersion(final String dbPath) {
        final int dbVersion = DBUtil.getDBVersion(dbPath);
        Log.d("getDBVersion", dbPath + "   version is " + dbVersion);
        return dbVersion;
    }

    private String downloadDatabase() {
        return LoadUtil.downloadFile(DB_DOWNLOAD_URL, DB_DOWNLOAD_PATH);

    }

    public enum Result {
        NEW_UPDATE, NO_DB, NO_NEW_UPDATE
    }

}
