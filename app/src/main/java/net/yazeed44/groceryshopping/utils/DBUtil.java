package net.yazeed44.groceryshopping.utils;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.database.ItemsDB;
import net.yazeed44.groceryshopping.database.ItemsDBHelper;
import net.yazeed44.groceryshopping.ui.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by yazeed44 on 12/31/14.
 */
public final class DBUtil {

    public static final String AD_TXT_DOWNLOAD_URL = "https://www.dropbox.com/s/expf2eaqdktser8/AdTest.txt?dl=1";
    public static final String AD_TXT_FILE_NAME = "Ad.txt";
    public static final String AD_TXT_DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_TXT_FILE_NAME;

    public static final String AD_IMAGE_FILE_NAME = "Ad-image.png";
    public static final String AD_IMAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_IMAGE_FILE_NAME;

    public static final String AD_PDF_FILE_NAME = "Ad-pdf.pdf";
    public static final String AD_PDF_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_PDF_FILE_NAME;
    private static Activity mActivity;
    private static ArrayList<Category> mCategories;
    private static ArrayList<Item> mItems;
    private static Ad mAd;

    private DBUtil() {
        throw new AssertionError();
    }

    public static void initInstance(final Activity activity) {


        mActivity = activity;
        getCategories();//Initialize

        ItemsDB.initInstance(ItemsDBHelper.createInstance(mActivity));


        new CheckDBTask(activity).execute();


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
        final ItemsDBHelper helper = ItemsDBHelper.createInstance(mActivity);
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


    public static void loadAd(final OnAdLoadingListener loadingListener) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingListener.beforeDownloadingAd();
            }

            @Override
            protected Void doInBackground(Void... params) {


                if (mAd != null) {
                    Log.i(AdView.TAG, "Got offline Ad");
                    return null;
                }

                final Ad ad = fetchAd(AD_TXT_DOWNLOAD_PATH);

                if (ad != null) {
                    Log.i(AdView.TAG, "Got Ad without need to download it");
                    mAd = ad;
                    return null;
                }

                if (!LoadUtil.isNetworkAvailable(mActivity)) {
                    Log.w(AdView.TAG, "There's no connection to internet");
                    return null;
                } else {
                    Log.i(AdView.TAG, "Downloading Ad");
                    final String adTxtFilePath = LoadUtil.downloadFile(AD_TXT_DOWNLOAD_URL, AD_TXT_DOWNLOAD_PATH);

                    mAd = fetchAd(adTxtFilePath);
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (loadingListener != null) {
                    if (mAd != null) {
                        loadingListener.adDownloadedSuccessfully(mAd);
                    } else {
                        loadingListener.adFailedToDownload();
                    }
                }


            }


        }.execute();


    }

    private static Ad fetchAd(final String adTxtPath) {

        if (!fileExists(adTxtPath)) {
            return null;
        }


        Ad ad;

        try {
            final BufferedReader adFileReader = new BufferedReader(new FileReader(adTxtPath));
            ad = new Ad(adFileReader.readLine(), adFileReader.readLine()); //The reader moves from line to line

            if (fileExists(DBUtil.AD_PDF_PATH) && fileExists(DBUtil.AD_IMAGE_PATH)) {
                Log.i(AdView.TAG, "Got ad image and pdf offline");
                ad.setImagePath(AD_IMAGE_PATH);
                ad.setPdfPath(AD_PDF_PATH);
            }

            adFileReader.close();


        } catch (IOException e) {
            e.printStackTrace();
            Log.e("fetchAd", e.getMessage());
            ad = null;
        }

        return ad;



    }

    public static boolean fileExists(final String path) {
        return new File(path).exists();
    }

    public static void forceDownloadNewDb(final Context context, final CheckDBTask.OnInstallingDb listener) {


        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new CheckDBTask(context, listener).execute(CheckDBTask.DatabaseAction.INSTALL_NEW_ONE);

            }

        });

    }

    public static interface OnAdLoadingListener {
        void beforeDownloadingAd();

        void adDownloadedSuccessfully(final Ad ad);

        void adFailedToDownload();
    }
}
