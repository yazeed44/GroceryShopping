package net.yazeed44.groceryshopping.requests;

import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.request.SpiceRequest;

import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.LoadUtil;

import java.io.File;

/**
 * Created by yazeed44 on 2/16/15.
 */
public class CheckForAdUpdatesRequest extends SpiceRequest<CheckForAdUpdatesRequest.Result> {

    public static final String TAG = "CheckAdUpdate";
    private Context mContext;

    public CheckForAdUpdatesRequest(final Context context) {

        super(Result.class);
        mContext = context;
    }

    @Override
    public Result loadDataFromNetwork() throws Exception {

        if (!LoadUtil.fileExists(DBUtil.AD_TXT_DIR)) {
            return Result.NO_NEW_UPDATE;

        }

        if (LoadUtil.isNetworkAvailable(mContext)) {

            if (newUpdateExists()) {
                return Result.NEW_UPDATE;
            } else {
                return Result.NO_NEW_UPDATE;
            }

        } else {
            return Result.NO_NEW_UPDATE;
        }


    }

    private boolean newUpdateExists() {

        final String newAdPath = LoadUtil.downloadFile(DBUtil.AD_TXT_DOWNLOAD_URL, DBUtil.AD_TXT_COPY_DIR);


        if (!LoadUtil.isDownloadedFileValid(newAdPath)) {
            return false;
        }

        final File newAdTxt = new File(newAdPath);
        final File oldAdTxt = new File(DBUtil.AD_TXT_DIR);


        final boolean newUpdateExists = newAdTxt.length() != oldAdTxt.length();

        Log.d(TAG, "new Update exists   " + newUpdateExists);

        if (!newUpdateExists) {
            deleteNewAd();
        }


        return newUpdateExists;
    }

    private void deleteNewAd() {
        LoadUtil.deleteFile(new File(DBUtil.AD_TXT_COPY_DIR));
    }


    public enum Result {
        NEW_UPDATE, NO_NEW_UPDATE
    }


}
