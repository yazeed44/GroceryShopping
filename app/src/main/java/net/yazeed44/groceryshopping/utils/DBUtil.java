package net.yazeed44.groceryshopping.utils;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.database.ItemsDB;
import net.yazeed44.groceryshopping.database.ItemsDBHelper;
import net.yazeed44.groceryshopping.requests.DownloadAdsRequest;
import net.yazeed44.groceryshopping.requests.InstallDbRequest;
import net.yazeed44.groceryshopping.ui.AdRecyclerView;
import net.yazeed44.groceryshopping.ui.BaseActivity;
import net.yazeed44.groceryshopping.ui.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by yazeed44 on 12/31/14.
 */
public final class DBUtil {

    public static final String AD_TXT_DOWNLOAD_URL = "https://www.dropbox.com/s/expf2eaqdktser8/AdTest.txt?dl=1";
    public static final String AD_TXT_FILE_NAME = "Ad.txt";
    public static final String AD_TXT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_TXT_FILE_NAME;

    public static final String AD_TXT_COPY_FILE_NAME = "Ad_copy.txt";
    public static final String AD_TXT_COPY_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_TXT_COPY_FILE_NAME;

    public static final String AD_IMAGE_FILE_NAME = "Ad-image";
    public static final String AD_IMAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_IMAGE_FILE_NAME;
    public static final String AD_IMAGE_FILE_TYPE_SUFFIX = ".png";

    public static final String AD_PDF_FILE_NAME = "Ad-pdf";
    public static final String AD_PDF_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + AD_PDF_FILE_NAME;
    public static final String AD_PDT_FILE_TYPE_SUFFIX = ".pdf";
    private static Activity mActivity;
    private static ArrayList<Category> mCategories;
    private static ArrayList<Item> mItems;
    private static ArrayList<Ad> mAds = new ArrayList<>();

    private DBUtil() {
        throw new AssertionError();
    }

    public static void initInstance(final Activity activity) {


        mActivity = activity;
        getCategories();//Initialize

        ItemsDB.initInstance(ItemsDBHelper.createInstance(mActivity));


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
                new Category("BakeryProducts", "المخبوزات", R.drawable.bakery),
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
        } catch (Exception ex) {
            Log.e("getDBVersion", ex.getMessage());
            version = -1;

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


    public static void loadAd(final DownloadAdsRequest.AdLoadingCallback loadingListener) {


        if (mAds != null && !mAds.isEmpty()) {
            Log.i(AdRecyclerView.TAG, "Device already has ads ");
            loadingListener.adsDownloadedSuccessfully(mAds);

            return;
        }


        final DownloadAdsRequest downloadRequest = new DownloadAdsRequest(AdList.class, mActivity);

        if (mActivity instanceof BaseActivity) {
            ((BaseActivity) mActivity).spiceManager.execute(downloadRequest, new RequestListener<AdList>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    loadingListener.adFailedToDownload(spiceException);
                }

                @Override
                public void onRequestSuccess(AdList ads) {
                    if (!ads.isEmpty()) {
                        mAds = ads;
                        loadingListener.adsDownloadedSuccessfully(mAds);
                    }
                }
            });
        }









    }

    public static void installNewDb(final Context context, final OnInstallingDbListener listener) {


        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final android.app.ProgressDialog progressDialog = createDbLoadingDialog(context);
                progressDialog.show();
                final InstallDbRequest installRequest = new InstallDbRequest(context);

                ((BaseActivity) mActivity).spiceManager.execute(installRequest, new RequestListener<InstallDbRequest.InstallingResult>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {

                    }

                    @Override
                    public void onRequestSuccess(InstallDbRequest.InstallingResult result) {
                        progressDialog.hide();

                        switch (result) {
                            case NO_PROBLEM:
                                listener.onDbInstalledSuccessful((MainActivity) mActivity);
                                break;


                            case ERROR_NO_NETWORK:
                                showNoNetworkDialog(context, listener);
                                break;
                        }

                    }
                });

            }

        });

    }

    private static android.app.ProgressDialog createDbLoadingDialog(final Context context) {
        final android.app.ProgressDialog dialog = new android.app.ProgressDialog(context);
        dialog.setTitle(R.string.title_loading_db);
        dialog.setMessage(context.getResources().getString(R.string.content_loading_db));
        dialog.setCancelable(false);
        dialog.setProgressStyle(R.attr.progressBarStyle);


        return dialog;
    }


    private static void showNoNetworkDialog(final Context context, final OnInstallingDbListener listener) {

        Log.w("NoNetwork", "Failed to download db ");

        ViewUtil.createDialog(context)
                .iconRes(android.R.drawable.stat_notify_error)
                .content(R.string.title_error_no_network)
                .positiveText(R.string.pos_btn_error_no_network)
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        if (LoadUtil.isNetworkAvailable(context)) {
                            installNewDb(context, listener);
                            dialog.dismiss();

                        } else {
                            ViewUtil.toastShort(context, R.string.toast_error_no_network);
                            showNoNetworkDialog(context, listener);
                        }
                    }
                })
                .show();

    }

    public static void resetAds() {
        deleteOldAd();
        new File(AD_TXT_COPY_DIR).renameTo(new File(AD_TXT_DIR));
        mAds = null;
        Ad.sCount = 0;


    }

    private static void deleteOldAd() {
        LoadUtil.deleteFile(new File(DBUtil.AD_TXT_DIR));
        for (final Ad ad : mAds) {
            ad.deleteOldFilesIfExists();
        }


    }




    public static interface OnInstallingDbListener {
        void onDbInstalledSuccessful(final MainActivity activity);
    }


}
