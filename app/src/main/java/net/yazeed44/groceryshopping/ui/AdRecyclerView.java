package net.yazeed44.groceryshopping.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.requests.DownloadAdsRequest;
import net.yazeed44.groceryshopping.utils.Ad;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import java.io.File;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by yazeed44 on 2/4/15.
 */
public class AdRecyclerView extends RecyclerView implements DownloadAdsRequest.AdLoadingCallback {

    public final static String TAG = "AdView";

    private ArrayList<Ad> mAdsList;


    public AdRecyclerView(Context context) {
        super(context);
        setupAdapter();
    }

    public AdRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAdapter();
    }

    public AdRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAdapter();
    }

    private void setupAdapter() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        gridLayoutManager.setOrientation(HORIZONTAL);
        setLayoutManager(gridLayoutManager);

        setAdapter(new AdsAdapter());

    }


    public void loadAd() {

        DBUtil.loadAd(this);

    }

    public void attachToRecyclerView(final RecyclerView recyclerView) {
        //TODO

    }


    private void hide() {
        setVisibility(GONE);
    }

    private void show() {
        setVisibility(VISIBLE);
    }


    @Override
    public void adsDownloadedSuccessfully(ArrayList<Ad> ads) {

        if (ads.isEmpty()) {
            hide();
            return;
        }

        mAdsList = ads;
        setupAdapter();
        show();

    }

    @Override
    public void adFailedToDownload(Throwable error) {
        Log.e(TAG, error.getMessage());

        hide();
    }


    private interface OnClickAdListener {
        void onClickAdImage(View adImage);
    }

    private class AdsAdapter extends Adapter<AdViewHolder> implements OnClickAdListener {

        @Override
        public AdViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View adLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_ad, parent, false);
            return new AdViewHolder(adLayout, this);
        }

        @Override
        public void onBindViewHolder(AdViewHolder holder, int position) {
            final Ad ad = mAdsList.get(position);
            displayAdImage(ad, holder);

        }

        @Override
        public int getItemCount() {
            return mAdsList.size();
        }

        @Override
        public void onClickAdImage(View adImage) {

            final int position = ViewUtil.getPositionOfChild(adImage, R.id.ad_layout, AdRecyclerView.this);
            final Ad ad = mAdsList.get(position);
            openPdf(ad.getPdfPath());
        }


        private void displayAdImage(final Ad ad, final AdViewHolder holder) {
            ImageLoader.getInstance().displayImage("file://" + ad.getImagePath(), holder.adImage, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    Log.e(TAG, "Failed to load image  " + failReason.getCause().getMessage());
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    Log.i(TAG, "Displaying Ad image");
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    Log.w(TAG, "Loading Ad image canceled");

                }
            });

        }


        private void openPdf(final String pdfPath) {

            final File pdfFile = new File(pdfPath);

            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(Uri.fromFile(pdfFile), "application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            Intent intent = Intent.createChooser(target, "");
            try {
                getContext().startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //TODO handle exception for not finding proper pdf reader
                // Instruct the user to install a PDF reader here, or something
                ViewUtil.toastShort(getContext(), R.string.toast_error_no_pdf_program);
            }

        }
    }

    class AdViewHolder extends ViewHolder {

        private final OnClickAdListener mListener;
        @InjectView(R.id.ad_image)
        ImageView adImage;

        public AdViewHolder(View itemView, final OnClickAdListener listener) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            mListener = listener;


        }

        @OnClick(R.id.ad_image)
        public void onClickAdImage(View adImage) {

            mListener.onClickAdImage(adImage);
        }
    }

}
