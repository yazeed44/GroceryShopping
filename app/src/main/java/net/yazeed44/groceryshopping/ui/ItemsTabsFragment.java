package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
public class ItemsTabsFragment extends Fragment {


    private ItemsFragment.OnCheckItemListener mCheckListener;
    private ViewPager mItemsPager;
    private boolean hasSelectedTab = false;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View tabsLayout = inflater.inflate(R.layout.fragment_tabs_items, container, false);
        final PagerSlidingTabStrip itemsTabs = (PagerSlidingTabStrip) tabsLayout.findViewById(R.id.items_tabs);
        mItemsPager = (ViewPager) tabsLayout.findViewById(R.id.items_pager);
        final int categoryIndex = getArguments().getInt(MainActivity.CATEGORY_INDEX_KEY);
        final Category chosenCategory = DBUtil.getCategories().get(categoryIndex);


        mItemsPager.setAdapter(new ItemsPagerAdapter(getFragmentManager()));
        //itemsPager.setCurrentItem(DBUtil.getCategories().indexOf(chosenCategory), false);


        itemsTabs.setViewPager(mItemsPager);
        itemsTabs.notifyDataSetChanged();


        return tabsLayout;
    }

    public void setCheckListener(final ItemsFragment.OnCheckItemListener listener) {
        this.mCheckListener = listener;
    }

    private class ItemsPagerAdapter extends FragmentPagerAdapter {
        public ItemsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private ItemsFragment getInstance(final int position) {

            if (!hasSelectedTab) {
                mItemsPager.setCurrentItem();
            }

            final ItemsFragment fragment = new ItemsFragment();
            final Bundle bundle = new Bundle();
            bundle.putInt(MainActivity.CATEGORY_INDEX_KEY, position);
            fragment.setArguments(bundle);
            fragment.setListener(mCheckListener);

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
