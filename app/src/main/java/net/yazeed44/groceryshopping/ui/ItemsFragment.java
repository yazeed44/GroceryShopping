package net.yazeed44.groceryshopping.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyandroidanimations.library.Animation;
import com.easyandroidanimations.library.AnimationListener;
import com.easyandroidanimations.library.FlipVerticalAnimation;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Category;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.Item;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by yazeed44 on 1/1/15.
 */
public class ItemsFragment extends BaseFragment {

    protected ArrayList<Item> mItemsArray;
    protected ItemsAdapter mAdapter;
    private GridView mItemsGridView;
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
        final Category category = DBUtil.getCategories().get(categoryIndex);
        return category.getItems();
    }

    protected ItemsAdapter createAdapter() {
        return new ItemsAdapter();

    }

    public void setListener(final OnCheckItemListener listener) {
        mListener = listener;
    }

    public static interface OnCheckItemListener {
        void onAddItem(final Item item);

        void onRemoveItem(final Item item);
    }

    protected class ItemsAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private final Drawable mAddDrawable = getResources().getDrawable(R.drawable.ic_action_add_shopping_cart);

        private final Drawable mAddedDrawable = getResources().getDrawable(R.drawable.ic_done);

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
                holder = new ViewHolder(convertView);
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


            if (isAdded(item)) {
                loadAddedItem(holder);

            } else {
                loadAvailableItem(holder);
            }

            holder.nameTxt.setTypeface(ViewUtil.getRegularDefaultTypeface());
            holder.nameTxt.setText(item.name);
        }


        private boolean isAdded(final Item item) {

            boolean isAdded = false;
            for (final Item arrayItem : MainActivity.CHOSEN_ITEMS) {

                if (item.key == arrayItem.key) {
                    isAdded = true;
                }
            }


            return isAdded;
        }

        private void loadAddedItem(final ViewHolder holder) {
            holder.addImageView.setImageDrawable(mAddedDrawable);
            holder.addImageView.setColorFilter(getResources().getColor(R.color.accent));
        }

        private void loadAvailableItem(final ViewHolder holder) {
            holder.addImageView.setImageDrawable(mAddDrawable);
            holder.addImageView.setColorFilter(Color.TRANSPARENT);
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final ViewHolder holder = new ViewHolder(view);
            final Item item = mItemsArray.get(position);
            final long duration = 150;

            //TODO Add animation
            if (isAdded(item)) {
                //removing
                mListener.onRemoveItem(item);
                animateRemoving(holder, duration);
            } else {
                //adding
                mListener.onAddItem(item);
                animateAdding(holder, duration);
            }


        }


        private void animateAdding(final ViewHolder holder, final long duration) {
            new FlipVerticalAnimation(holder.addImageView)
                    .setDuration(duration)
                    .setListener(new AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            loadAddedItem(holder);


                        }
                    })
                    .animate();
        }


        private void animateRemoving(final ViewHolder holder, final long duration) {
            new FlipVerticalAnimation(holder.addImageView)
                    .setDuration(duration)
                    .setListener(new AnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            loadAvailableItem(holder);
                        }
                    })
                    .animate();
        }


        class ViewHolder {
            @InjectView(R.id.item_add_image)
            ImageView addImageView;
            @InjectView(R.id.item_name)
            TextView nameTxt;

            public ViewHolder(final View layout) {

                ButterKnife.inject(this, layout);
            }
        }
    }
}
