package net.yazeed44.groceryshopping.ui;

import android.support.v7.app.ActionBarActivity;

/**
 * Created by yazeed44 on 1/16/15.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
