package net.yazeed44.groceryshopping.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.yazeed44.groceryshopping.utils.DBUtil;

/**
 * Created by yazeed44 on 2/4/15.
 */
public class AdView extends ImageView implements View.OnClickListener {
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
        ImageLoader.getInstance().displayImage(DBUtil.getAd().imageUrl, this);
    }

    @Override
    public void onClick(View v) {


    }
}
