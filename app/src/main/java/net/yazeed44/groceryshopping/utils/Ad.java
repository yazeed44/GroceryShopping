package net.yazeed44.groceryshopping.utils;

/**
 * Created by yazeed44 on 2/4/15.
 */
public class Ad {

    public final String imageUrl;
    public final String pdfUrl;

    private String mImagePath;
    private String mPdfPath;

    public Ad(String imageUrl, String pdfUrl) {
        this.imageUrl = imageUrl;
        this.pdfUrl = pdfUrl;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String imagePath) {
        this.mImagePath = imagePath;
    }

    public String getPdfPath() {
        return mPdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.mPdfPath = pdfPath;
    }
}
