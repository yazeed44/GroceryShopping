package net.yazeed44.groceryshopping.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by yazeed44 on 2/4/15.
 */
public final class LoadUtil {

    public static final int DOWNLOAD_BUFFER_SIZE = 16000;
    public static final String DOWNLOAD_FAILED = "DownloadFailed";
    private static final String TAG = "LoadUtil";

    private LoadUtil() {
        throw new AssertionError("This is util class !!");
    }

    public static String downloadFile(final String fileUrl, final String downloadPath) {

        String filePath;

        if (fileExists(downloadPath)) {
            return downloadPath;
        }

        final File outputFile = createOrReturnFile(downloadPath);


        try {
            final URLConnection connection = openConnection(fileUrl);
            startDownload(outputFile, connection);
            filePath = outputFile.getAbsolutePath();
            Log.i("downloadFile", "File successfully downloaded " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            filePath = DOWNLOAD_FAILED;
        }

        return filePath;

    }

    private static File createOrReturnFile(final String path) {
        final File outputFile = new File(path);

        if (outputFile.exists()) {
            return outputFile;
        } else {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                Log.e("downloadFile", e.getMessage());
                e.printStackTrace();
            }
        }

        return outputFile;
    }

    private static URLConnection openConnection(final String fileUrl) throws IOException {


        final URL url = new URL(fileUrl);
        final URLConnection urlConnection = url.openConnection();
        urlConnection.setUseCaches(false);


        return urlConnection;
    }

    private static void startDownload(final File outputFile, final URLConnection connection) throws IOException {

        final BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());

        final FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        final BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream, DOWNLOAD_BUFFER_SIZE);
        final byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = inputStream.read(data, 0, data.length)) >= 0) {
            outputStream.write(data, 0, bytesRead);
        }

        outputStream.close();
        fileOutputStream.close();
        inputStream.close();

    }


    public static void loadFile(final String fileUrl, final String downloadPath, final OnLoadFileListener listener) {

        new AsyncTask<Void, Void, Void>() {

            String filePath;

            @Override
            protected Void doInBackground(Void... params) {

                if (new File(downloadPath).exists()) {
                    filePath = downloadPath;
                    return null;
                }

                filePath = downloadFile(fileUrl, downloadPath);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (isDownloadedFileValid(filePath)) {
                    listener.onDownloadedFileSuccessfully(filePath);
                } else {
                    listener.onFailedToDownloadFile();
                }

            }
        }.execute();
    }


    public static boolean isDownloadedFileValid(final String filePath) {

        if (TextUtils.isEmpty(filePath) || DOWNLOAD_FAILED.equals(filePath)) {
            Log.e("isDownloadedFileValid", "There's problem with the downloaded file   " + filePath);
            return false;
        }

        return true;

    }

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void deleteFile(final File file) {


        if (!file.exists()) {
            return;
        }

        final boolean deleted = file.delete();

        if (deleted) {
            Log.i(TAG, "File deleted successfully  " + file.getName());

        } else {
            Log.w(TAG, "File failed to delete  " + file.getName());
        }
    }

    public static boolean fileExists(final String path) {
        return new File(path).exists();
    }

    public static interface OnLoadFileListener {
        void onDownloadedFileSuccessfully(final String filePath);

        void onFailedToDownloadFile();
    }

}
