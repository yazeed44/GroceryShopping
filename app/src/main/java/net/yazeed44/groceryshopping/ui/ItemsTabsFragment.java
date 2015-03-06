package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Category;
import net.yazeed44.groceryshopping.utils.DBUtil;

/**
 * Created by yazeed44 on 1/16/15.
 */
public class ItemsTabsFragment extends BaseFragment {


    private ItemsFragment.OnCheckItemListener mCheckListener;
    private ViewPager mItemsPager;
    private Category mChosenCategory;
    private PagerSlidingTabStrip mItemsTabs;
    private boolean mHasOpenChosenCategory = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View tabsLayout = inflater.inflate(R.layout.fragment_tabs_items, container, false);
        mItemsTabs = (PagerSlidingTabStrip) tabsLayout.findViewById(R.id.items_tabs);
        mItemsPager = (ViewPager) tabsLayout.findViewById(R.id.items_pager);
        final int categoryIndex = getArguments().getInt(MainActivity.KEY_CATEGORY_INDEX);
        mChosenCategory = DBUtil.getCategories().get(categoryIndex);


        mItemsPager.setAdapter(new ItemsPagerAdapter(getChildFragmentManager()));

        mItemsTabs.setViewPager(mItemsPager);


        return tabsLayout;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mHasOpenChosenCategory) {
            return;
        }

        mItemsPager.setCurrentItem(0, true);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                mItemsPager.setCurrentItem(DBUtil.getCategories().indexOf(mChosenCategory), true);
                mItemsTabs.notifyDataSetChanged();
                mHasOpenChosenCategory = true;
            }
        });

    }

    public void setCurrentPage(final Category currentCategory) {
        final int index = DBUtil.getCategories().indexOf(currentCategory);
        mItemsPager.setCurrentItem(index);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHasOpenChosenCategory = false;
    }

    public void setCheckListener(final ItemsFragment.OnCheckItemListener listener) {
        this.mCheckListener = listener;
    }

    @Override
    protected AdRecyclerView onCreateAdView() {
        return null;
    }

    @Override
    protected void attachAdView(AdRecyclerView adView) {

    }

    private class ItemsPagerAdapter extends FragmentPagerAdapter {
        public ItemsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private ItemsFragment getInstance(final int position) {

            final ItemsFragment fragment = new ItemsFragment();
            final Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.KEY_CATEGORY_INDEX, position);
            fragment.setArguments(bundle);
            fragment.setListener(mCheckListener);
            Log.d("ItemsPagerAdapter", "Fragment  " + position + " is showing right now");

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return DBUtil.getCategories().get(position).name;
        }

        @Override
        public Fragment getItem(int position) {
            return getInstance(position);
        }

        @Override
        public int getCount() {
            return DBUtil.getCategories().size();
        }
    }
}
