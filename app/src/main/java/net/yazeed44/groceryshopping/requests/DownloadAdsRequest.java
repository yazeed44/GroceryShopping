package net.yazeed44.groceryshopping.requests;

import android.content.Context;
import android.util.Log;

import com.octo.android.robospice.exception.NoNetworkException;
import com.octo.android.robospice.request.SpiceRequest;

import net.yazeed44.groceryshopping.ui.AdRecyclerView;
import net.yazeed44.groceryshopping.utils.Ad;
import net.yazeed44.groceryshopping.utils.AdList;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.LoadUtil;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yazeed44 on 3/2/15.
 */
public class DownloadAdsRequest extends SpiceRequest<AdList> {


    private final Context mContext;
    private BufferedReader mAdFileReader;

    public DownloadAdsRequest(Class<AdList> clazz, final Context context) {
        super(clazz);
        mContext = context;
    }

    @Override
    public AdList loadDataFromNetwork() throws Exception {


        final AdList adsList = new AdList();

        try {
            downloadAdTxt();
        } catch (final NoNetworkException ex) {
            return adsList;
        }


        addAdsToList(adsList);

        for (final Ad ad : adsList) {

            if (ad.hasBeenDownloaded()) {
                ad.setPdfPath(ad.getExceptedPdfPath());
                ad.setImagePath(ad.getExceptedImagePath());
            } else {
                ad.deleteOldFilesIfExists();
                try {
                    loadAdFiles(ad);
                } catch (NoNetworkException ex) {
                    return adsList;
                }
            }
        }

        Ad.sCount = 0;
        mAdFileReader.close();


        return adsList;
    }

    private void downloadAdTxt() throws NoNetworkException {
        if (LoadUtil.fileExists(DBUtil.AD_TXT_DIR)) {

        } else {
            if (!LoadUtil.isNetworkAvailable(mContext)) {
                throw new NoNetworkException();

            }
            LoadUtil.downloadFile(DBUtil.AD_TXT_DOWNLOAD_URL, DBUtil.AD_TXT_DIR);
        }

    }

    private void loadAdFiles(final Ad ad) throws NoNetworkException {
        if (!LoadUtil.isNetworkAvailable(mContext)) {
            throw new NoNetworkException();

        }


        loadImage(ad);
        loadPdf(ad);
    }

    private void loadPdf(final Ad ad) {


        final String pdfPath = LoadUtil.downloadFile(ad.pdfUrl, ad.getExceptedPdfPath());

        if (!LoadUtil.isDownloadedFileValid(pdfPath)) {
            ad.deleteOldFilesIfExists();
            throw new IllegalStateException("Pdf file for ad " + ad.id + " haven't downloaded successfully , deleting file");
        }


        Log.d(AdRecyclerView.TAG, "Downloaded pdf successfully for  ad " + ad.id);

        ad.setPdfPath(pdfPath);
    }

    private void loadImage(final Ad ad) {
        final String imagePath = LoadUtil.downloadFile(ad.imageUrl, ad.getExceptedImagePath());

        if (!LoadUtil.isDownloadedFileValid(imagePath)) {
            ad.deleteOldFilesIfExists();
            throw new IllegalStateException("Image for ad " + ad.id + " haven't downloaded successfully , deleting file");
        }

        Log.d(AdRecyclerView.TAG, "Downloaded image successfully for  ad " + ad.id);
        ad.setImagePath(imagePath);

    }

    private void addAdsToList(final ArrayList<Ad> ads) throws IOException {
        while (true) {
            final Ad ad = fetchAd();

            if (ad != null && ad.isValid()) {
                ads.add(ad);
            } else {
                break;
            }
        }
    }

    private Ad fetchAd() throws IOException {

        if (!LoadUtil.fileExists(DBUtil.AD_TXT_DIR)) {
            throw new FileNotFoundException("There's no ad txt file !!");
        }

        if (mAdFileReader == null) {
            mAdFileReader = new BufferedReader(new FileReader(DBUtil.AD_TXT_DIR));
        }


        final Ad ad = new Ad(mAdFileReader.readLine(), mAdFileReader.readLine());

        if (ad.isValid()) {
            return ad;
        } else {
            return null;
        }

    }


    public static interface AdLoadingCallback {

        void adsDownloadedSuccessfully(final ArrayList<Ad> ads);

        void adFailedToDownload(Throwable error);
    }
}
