package net.yazeed44.groceryshopping.requests;

import android.content.Context;
import android.os.Environment;

import com.octo.android.robospice.request.SpiceRequest;

import net.yazeed44.groceryshopping.utils.LoadUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by yazeed44 on 3/6/15.
 */
public class CheckAppStatusRequest extends SpiceRequest<CheckAppStatusRequest.Result> {

    public static final String TAG = "CheckAppStatusRequest";
    public static final String APP_STATUS_TEXT_URL = "https://www.dropbox.com/s/fx2aizj0k7p5m9x/app_status.txt?dl=1";
    public static final String APP_STATUS_FILE_NAME = "app_status.txt";
    public static final String APP_STATUS_TEXT_DIR = Environment.getExternalStorageDirectory() + "/" + APP_STATUS_FILE_NAME;
    private final Context mContext;

    public CheckAppStatusRequest(final Context context) {
        super(Result.class);
        mContext = context;
    }

    @Override
    public Result loadDataFromNetwork() throws Exception {

        if (!LoadUtil.isNetworkAvailable(mContext)) {
            return Result.SHOULD_CONTINUE;
        }


        final String appStatusTextFilePath = LoadUtil.downloadFile(APP_STATUS_TEXT_URL, APP_STATUS_TEXT_DIR);

        if (!LoadUtil.isDownloadedFileValid(appStatusTextFilePath)) {
            return Result.SHOULD_CONTINUE;
        }

        final BufferedReader statusReader = new BufferedReader(new FileReader(new File(appStatusTextFilePath)));

        final String statusText = statusReader.readLine();

        statusReader.close();
        LoadUtil.deleteFile(new File(appStatusTextFilePath));

        if (statusText.equals("1")) {
            return Result.SHOULD_CONTINUE;
        } else if (statusText.equals("0")) {
            return Result.SHOULD_STOP;
        }


        return null;
    }

    public enum Result {

        SHOULD_STOP, SHOULD_CONTINUE
    }


}
