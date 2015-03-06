package net.yazeed44.groceryshopping.utils;

import java.io.File;

/**
 * Created by yazeed44 on 2/4/15.
 */
public class Ad {

    public static int sCount = 0;
    public final String imageUrl;
    public final String pdfUrl;
    public final int id;
    private String mImagePath;
    private String mPdfPath;

    public Ad(String imageUrl, String pdfUrl) {
        this.imageUrl = imageUrl;
        this.pdfUrl = pdfUrl;
        id = sCount++;
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

    public boolean isValid() {

        return imageUrl != null && !imageUrl.isEmpty() && pdfUrl != null && !pdfUrl.isEmpty();
    }

    public boolean hasBeenDownloaded() {
        return hasDownloadedImage() && hasDownloadedPdf();
    }

    private boolean hasDownloadedPdf() {
        final File exceptedPdfFile = new File(getExceptedPdfPath());
        return exceptedPdfFile.exists() && exceptedPdfFile.isFile();
    }

    private boolean hasDownloadedImage() {
        final File exceptedImageFile = new File(getExceptedImagePath());
        return exceptedImageFile.exists() && exceptedImageFile.isFile();
    }

    public String getExceptedImagePath() {
        return DBUtil.AD_IMAGE_DIR + id + DBUtil.AD_IMAGE_FILE_TYPE_SUFFIX;


    }

    public String getExceptedPdfPath() {

        return DBUtil.AD_PDF_DIR + id + DBUtil.AD_PDT_FILE_TYPE_SUFFIX;
    }

    public void deleteOldFilesIfExists() {

        LoadUtil.deleteFile(new File(getExceptedPdfPath()));
        LoadUtil.deleteFile(new File(getExceptedImagePath()));
    }
}
