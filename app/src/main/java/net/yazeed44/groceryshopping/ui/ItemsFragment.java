package net.yazeed44.groceryshopping.ui;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.OnClick;

/**
 * Created by yazeed44 on 1/1/15.
 */
public class ItemsFragment extends BaseFragment {

    protected ArrayList<Item> mItemsArray;
    protected ItemsAdapter mAdapter;

    @InjectView(R.id.items_recycler)
    RecyclerView mItemsRecyclerView;
    private OnCheckItemListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_items, container, false);

        ButterKnife.inject(this, layout);
        init();
        setupRecycler();

        return layout;
    }

    private void init() {

        mItemsArray = getItems();
        mAdapter = createAdapter();

    }

    private void setupRecycler() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.items_column_num));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mItemsRecyclerView.setHasFixedSize(true);
        mItemsRecyclerView.setLayoutManager(gridLayoutManager);
        mItemsRecyclerView.setAdapter(mAdapter);
    }

    protected ArrayList<Item> getItems() {
        final int categoryIndex = getArguments().getInt(MainActivity.KEY_CATEGORY_INDEX);
        final Category category = DBUtil.getCategories().get(categoryIndex);
        return category.getItems();
    }

    protected ItemsAdapter createAdapter() {
        return new ItemsAdapter();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setListener((OnCheckItemListener) activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        setListener(null);
    }

    public void setListener(final OnCheckItemListener listener) {
        mListener = listener;
    }

    @Override
    protected AdRecyclerView onCreateAdView() {
        return (AdRecyclerView) getView().findViewById(R.id.ad_view);
    }

    @Override
    protected void attachAdView(AdRecyclerView adView) {
        adView.attachToRecyclerView(mItemsRecyclerView);

    }

    public static interface OnCheckItemListener {
        void onAddItem(final Item item);

        void onRemoveItem(final Item item);
    }

    private interface ItemListener {
        void onClickItem(View itemLayout);
    }

    protected class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> implements ItemListener {

        private final Drawable mAddDrawable = getResources().getDrawable(R.drawable.ic_action_add_shopping_cart);

        private final Drawable mAddedDrawable = getResources().getDrawable(R.drawable.ic_added_shopping_cart);

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_item, parent, false);
            return new ItemViewHolder(layout, this);
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {

            final Item item = mItemsArray.get(position);

            loadItem(holder, item);
        }

        @Override
        public int getItemCount() {
            return mItemsArray.size();
        }

        private void loadItem(final ItemViewHolder holder, final Item item) {


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

        private void loadAddedItem(final ItemViewHolder holder) {
            holder.addImageView.setImageDrawable(mAddedDrawable);
            holder.addImageView.setColorFilter(getResources().getColor(R.color.accent));
        }

        private void loadAvailableItem(final ItemViewHolder holder) {
            holder.addImageView.setImageDrawable(mAddDrawable);
            holder.addImageView.setColorFilter(Color.TRANSPARENT);
        }

        private void animateAdding(final ItemViewHolder holder, final long duration) {
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


        private void animateRemoving(final ItemViewHolder holder, final long duration) {
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

        @Override
        public void onClickItem(View itemLayout) {
            final int position = ViewUtil.getPositionOfChild(itemLayout, R.id.item_linear_layout, mItemsRecyclerView);
            final Item item = mItemsArray.get(position);
            final ItemViewHolder holder = new ItemViewHolder(itemLayout, this);

            final long flipDuration = 400;
            if (isAdded(item)) {
                //removing
                mListener.onRemoveItem(item);
                animateRemoving(holder, flipDuration);
            } else {
                //adding
                mListener.onAddItem(item);
                animateAdding(holder, flipDuration);
            }

        }


        class ItemViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.item_add_image)
            ImageView addImageView;
            @InjectView(R.id.item_name)
            TextView nameTxt;


            ItemListener mListener;

            public ItemViewHolder(final View layout, final ItemListener listener) {
                super(layout);
                mListener = listener;

                ButterKnife.inject(this, layout);
            }

            @OnClick(R.id.item_linear_layout)
            public void onClickItem(View view) {
                mListener.onClickItem(view);
            }
        }


    }
}
