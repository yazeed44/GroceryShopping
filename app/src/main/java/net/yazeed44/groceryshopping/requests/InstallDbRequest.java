package net.yazeed44.groceryshopping.requests;

import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;

import net.yazeed44.groceryshopping.database.ItemsDB;
import net.yazeed44.groceryshopping.database.ItemsDBHelper;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.LoadUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by yazeed44 on 2/10/15.
 */
public class InstallDbRequest extends SpiceRequest<InstallDbRequest.InstallingResult> {


    private WeakReference<Context> mContextWeakReference;
    private String mLocalDBPath;

    public InstallDbRequest(final Context context) {
        super(InstallingResult.class);
        mContextWeakReference = new WeakReference<>(context);
        mLocalDBPath = DBUtil.getLocalDBPath(context);
    }

    @Override
    public InstallingResult loadDataFromNetwork() throws Exception {


        if (!LoadUtil.isNetworkAvailable(mContextWeakReference.get())) {
            return InstallingResult.ERROR_NO_NETWORK;
        }

        replaceLocalDB();

        return InstallingResult.NO_PROBLEM;
    }

    //Download a new db then replace the local db (If there's)
    private void replaceLocalDB() {

        final String newDatabasePath = downloadDatabase();

        if (!LoadUtil.isDownloadedFileValid(newDatabasePath)) {
            deleteDownloadedDB();
            throw new IllegalStateException("The database haven't downloaded successfully !!");
        }

        deleteLocalDB();
        final ItemsDBHelper dbHelper = DBUtil.createEmptyDB();

        copyNewDB(newDatabasePath);
        dbHelper.close();
        ItemsDB.initInstance(ItemsDBHelper.createInstance(mContextWeakReference.get()));
        deleteDownloadedDB();


        Log.i("replaceLocalDB", "Set the new DB Successfully");
    }

    private String downloadDatabase() {
        return LoadUtil.downloadFile(CheckForDbUpdatesRequest.DB_DOWNLOAD_URL, CheckForDbUpdatesRequest.DB_DOWNLOAD_PATH);

    }

    private void copyNewDB(final String newDBPath) {
        try {

            final InputStream inputStream = new FileInputStream(newDBPath);
            final OutputStream outputStream = new FileOutputStream(mLocalDBPath);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();

        } catch (IOException e) {
            Log.e("copyNewDB", e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteLocalDB() {
        new File(mLocalDBPath).getParentFile().delete();
    }

    private void deleteDownloadedDB() {
        new File(CheckForDbUpdatesRequest.DB_DOWNLOAD_PATH).delete();
    }

    public enum InstallingResult {

        ERROR_NO_NETWORK, NO_PROBLEM

    }


}
