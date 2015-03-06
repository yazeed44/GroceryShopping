package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.octo.android.robospice.SpiceManager;

import net.yazeed44.groceryshopping.utils.OfflineSpiceService;

/**
 * Created by yazeed44 on 1/16/15.
 */
public abstract class BaseActivity extends ActionBarActivity {


    public SpiceManager spiceManager;
    protected AdRecyclerView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spiceManager = onCreateSpiceManager();
    }

    @Override
    protected void onStart() {
        spiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

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

    protected abstract AdRecyclerView onCreateAd();

    protected SpiceManager onCreateSpiceManager() {
        return new SpiceManager(OfflineSpiceService.class);
    }


}
