package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Category;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.Item;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import java.util.ArrayList;

/**
 * Created by yazeed44 on 1/1/15.
 */
public class ItemsFragment extends BaseFragment {

    protected ArrayList<Item> mItemsArray;
    protected ItemsAdapter mAdapter;
    private GridView mItemsGridView;
    private Category mCategory;
    private OnCheckItemListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initGridView(inflater, container);

        return mItemsGridView;
    }

    private void initGridView(final LayoutInflater inflater, final ViewGroup container) {

        mItemsGridView = (GridView) inflater.inflate(R.layout.fragment_items, container, false);
        mItemsArray = getItems();
        mAdapter = createAdapter();
        mItemsGridView.setAdapter(mAdapter);
    }

    protected ArrayList<Item> getItems() {
        final int categoryIndex = getArguments().getInt(MainActivity.CATEGORY_INDEX_KEY);
        mCategory = DBUtil.getCategories().get(categoryIndex);
        return mCategory.getItems();
    }

    protected ItemsAdapter createAdapter() {
        return new ItemsAdapter();

    }

    public void setListener(final OnCheckItemListener listener) {
        mListener = listener;
    }

    public static interface OnCheckItemListener {
        void onCheck(final Item item);

        void onUnCheck(final Item item);
    }

    protected class ItemsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        protected ItemsAdapter() {
            mItemsGridView.setOnItemClickListener(this);
        }

        @Override
        public int getCount() {
            return mItemsArray.size();

        }

        @Override
        public Object getItem(int position) {
            return mItemsArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mItemsArray.get(position).key;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Item item = mItemsArray.get(position);

            final ViewHolder holder;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.element_item, parent, false);
                holder = createHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            loadItem(holder, item);
            setHeight(convertView);

            return convertView;
        }

        private void setHeight(View convertView) {
            final int height = getResources().getDimensionPixelSize(R.dimen.item_height);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        }

        private void loadItem(final ViewHolder holder, final Item item) {


            if (isChecked(item)) {
                holder.mItemCheckBox.setChecked(true);

            } else {
                holder.mItemCheckBox.setChecked(false);
            }

            holder.mItemCheckBox.setTypeface(ViewUtil.getRegularDefaultTypeface());
            holder.mItemCheckBox.setText(item.name);
        }

        private boolean isChecked(final Item item) {

            boolean isChecked = false;
            for (final Item arrayItem : MainActivity.sCheckedItems) {

                if (item.key == arrayItem.key) {
                    isChecked = true;
                }
            }


            return isChecked;
        }

        private ViewHolder createHolder(final View convertView) {
            final ViewHolder holder = new ViewHolder();
            holder.mItemCheckBox = (CheckBox) convertView.findViewById(R.id.item_checkbox);
            return holder;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ViewHolder holder = createHolder(view);
            final Item item = mItemsArray.get(position);

            if (holder.mItemCheckBox.isChecked()) {
                //Un check
                mListener.onUnCheck(item);
                holder.mItemCheckBox.setChecked(false);
            } else {
                //check
                mListener.onCheck(item);
                holder.mItemCheckBox.setChecked(true);
            }


        }


        private class ViewHolder {
            private CheckBox mItemCheckBox;
        }
    }
}
