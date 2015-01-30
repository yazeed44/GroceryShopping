package net.yazeed44.groceryshopping.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.yazeed44.groceryshopping.R;

/**
 * Created by yazeed44 on 1/5/15.
 */
public final class ViewUtil {

    private static Typeface mMediumTypeface;
    private static Typeface mRegularTypeface;
    private static Context mContext;

    private ViewUtil() {
        throw new AssertionError();
    }

    public static void init(final Context context) {
        mContext = context;
        getMediumTypeface(); //Initialize
        getRegularDefaultTypeface(); //Initialize
    }


    public static MaterialDialog.Builder createDialog(final Context context) {
        return new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .typeface(getMediumTypeface(), getRegularDefaultTypeface())
                .positiveColorRes(R.color.dialog_pos_btn_text_color)
                .negativeColorRes(R.color.dialog_neg_btn_text_color)
                ;
    }

    public static Bitmap drawableToBitmap(final int drawableRes) {

        return ImageLoader.getInstance().loadImageSync("drawable://" + drawableRes);
    }

    public static Typeface getMediumTypeface() {

        if (mMediumTypeface != null) {
            return mMediumTypeface;
        }

        mMediumTypeface = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Medium.ttf");
        return mMediumTypeface;
    }

    public static Typeface getRegularDefaultTypeface() {

        if (mRegularTypeface != null) {
            return mRegularTypeface;
        }

        mRegularTypeface = Typeface.createFromAsset(mContext.getAssets(), "Roboto-Regular.ttf");
        return mRegularTypeface;

    }

    public static void toastShort(final Context context, final CharSequence charSequence) {
        Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
    }

    public static void toastShort(final Context context, final int txtId) {
        Toast.makeText(context, txtId, Toast.LENGTH_SHORT).show();
    }

    public static int getPositionOfChild(final View child, final int childParentId, final RecyclerView recyclerView) {

        if (child.getId() == childParentId) {
            return recyclerView.getChildPosition(child);
        }


        View parent = (View) child.getParent();
        while (parent.getId() != childParentId) {
            parent = (View) parent.getParent();
        }
        return recyclerView.getChildPosition(parent);
    }

}
