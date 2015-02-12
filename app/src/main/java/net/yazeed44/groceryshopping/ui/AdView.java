package net.yazeed44.groceryshopping.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.yazeed44.groceryshopping.utils.Ad;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.LoadUtil;

import java.io.File;

/**
 * Created by yazeed44 on 2/4/15.
 *
 */
public class AdView extends ImageView implements View.OnClickListener, DBUtil.OnAdLoadingListener {

    public final static String TAG = "AdView";
    private Ad mAd;
    private String mPdfPath;

    public AdView(Context context) {
        super(context);
        init();
    }

    public AdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        openPdf();

    }

    private void openPdf() {

        final File pdfFile = new File(mPdfPath);

        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "");
        try {
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //TODO handle exception for not finding proper pdf reader
            // Instruct the user to install a PDF reader here, or something
        }

    }

    public void loadAd() {

        DBUtil.loadAd(this);


    }

    @Override
    public void beforeDownloadingAd() {
        hide();
    }

    @Override
    public void adDownloadedSuccessfully(Ad ad) {
        mAd = ad;

        if (!adImageExistsInStorage()) {
            Log.i(TAG, "Ad image dosen't exist   Downloading...");
            deleteImageFile();
            LoadUtil.loadFile(mAd.imageUrl, DBUtil.AD_IMAGE_PATH, new LoadUtil.OnLoadFileListener() {
                @Override
                public void onDownloadedFileSuccessfully(String filePath) {
                    displayAdImage();
                }

                @Override
                public void onFailedToDownloadFile() {
                    Log.e(TAG, "Failed to download image");
                    hide();
                }
            });

        }

        if (!adPdfExistsInStorage()) {
            Log.i(TAG, "Ad Pdf dosen't exist   Downloading...");
            deletePdfFile();
            LoadUtil.loadFile(mAd.pdfUrl, DBUtil.AD_PDF_PATH, new LoadUtil.OnLoadFileListener() {
                @Override
                public void onDownloadedFileSuccessfully(String filePath) {
                    mPdfPath = filePath;
                }

                @Override
                public void onFailedToDownloadFile() {
                    //TODO Handle failed to download pdf
                    Log.e(TAG, "Failed to download ad pdf");
                }
            });
        }

        Log.i(TAG, "Ad pdf and image exists");

        mPdfPath = ad.getPdfPath();
        displayAdImage();

    }

    private void hide() {
        setVisibility(GONE);
    }

    private void show() {
        setVisibility(VISIBLE);
    }


    private boolean adImageExistsInStorage() {

        final File imageFile = new File(DBUtil.AD_IMAGE_PATH);

        return imageFile.exists();
    }

    private void deleteImageFile() {
        new File(DBUtil.AD_IMAGE_PATH).delete();
    }

    private boolean adPdfExistsInStorage() {

        final File pdfFile = new File(DBUtil.AD_PDF_PATH);
        return pdfFile.exists();
    }

    private void deletePdfFile() {
        new File(DBUtil.AD_PDF_PATH).delete();
    }


    @Override
    public void adFailedToDownload() {
        Log.w(TAG, "Failed to download Ad");
        hide();
    }


    private void displayAdImage() {
        ImageLoader.getInstance().displayImage("file://" + mAd.getImagePath(), this, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.e(TAG, "Failed to load image  " + failReason.getCause().getMessage());
                hide();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                Log.i(TAG, "Displaying Ad image");
                show();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Log.w(TAG, "Loading Ad image canceled");
                hide();

            }
        });

    }


}
