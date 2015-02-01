package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.Item;

import java.util.ArrayList;

/**
 * Created by yazeed44 on 1/14/15.
 */
public class SearchItemsFragment extends ItemsFragment {

    private ArrayList<Item> mQueryResult = new ArrayList<>();
    private ArrayList<Item> mItems = DBUtil.getAllItems();


    public void query(final String query) {
        ((SearchAdapter) mAdapter).getFilter().filter(query);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        query("");
    }

    @Override
    protected boolean shouldHideSearchView() {
        return false;
    }

    @Override
    protected ArrayList<Item> getItems() {
        return mQueryResult;
    }

    @Override
    protected ItemsAdapter createAdapter() {
        return new SearchAdapter();
    }

    private class SearchAdapter extends ItemsAdapter implements Filterable {


        private ItemsFilter filter;

        protected SearchAdapter() {
            super();
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new ItemsFilter();
            }
            return filter;
        }


        private class ItemsFilter extends Filter {
            public static final String TAG = "ItemsFilter";

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();

                if (TextUtils.isEmpty(constraint)) {
                    Log.d(TAG, "Query is empty !!");
                    results.values = mItems;
                    results.count = mItems.size();
                } else {
                    final ArrayList<Item> filteredItems = new ArrayList<>();

                    for (final Item item : mItems) {
                        if (item.name.startsWith(constraint.toString())) {
                            Log.d(TAG, "Got query result  " + item.name);
                            filteredItems.add(item);
                        }
                    }

                    results.values = filteredItems;
                    results.count = filteredItems.size();
                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {


                if (results.count == 0) {

                    mItemsArray = (ArrayList<Item>) results.values;
                    notifyDataSetChanged();
                    Log.d(TAG, "Result updated to  " + mItemsArray.toString());
                } else {

                    mItemsArray = (ArrayList<Item>) results.values;
                    notifyDataSetChanged();
                    Log.d(TAG, "Result updated to  " + mItemsArray.toString());

                }

            }
        }
    }


}
