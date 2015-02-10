package net.yazeed44.groceryshopping.ui;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by yazeed44 on 1/16/15.
 */
public abstract class BaseActivity extends ActionBarActivity {


    protected AdView mAdView;

    @Override
    protected void onResume() {
        super.onResume();
        mAdView = onCreateAd();
        if (mAdView != null) {
            mAdView.loadAd();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected abstract AdView onCreateAd();


}
