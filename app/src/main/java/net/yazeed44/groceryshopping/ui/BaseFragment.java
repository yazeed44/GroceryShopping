package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

/**
 * Created by yazeed44 on 1/18/15.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(shouldDisplayUp());

        if (shouldHideSearchView()) {
            ((MainActivity) getActivity()).hideSearchView();
        } else {
            ((MainActivity) getActivity()).showSearchView();
        }


    }


    protected boolean shouldDisplayUp() {
        return true;
    }

    protected boolean shouldHideSearchView() {
        return true;
    }
}
