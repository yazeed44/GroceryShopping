package net.yazeed44.groceryshopping.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Category;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.PaletteLoader;
import net.yazeed44.groceryshopping.utils.PaletteRequest;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by yazeed44 on 12/31/14.
 */
public class CategoriesFragment extends BaseFragment {


    @InjectView(R.id.categories_recycler)
    RecyclerView mCategoriesRecycler;
    private OnClickCategoryListener mCategoryListener;
    private ArrayList<Category> mCategories = DBUtil.getCategories();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_categories, container, false);
        ButterKnife.inject(this, layout);
        setupRecycler();

        return layout;
    }

    @Override
    protected boolean shouldDisplayUp() {
        return false;
    }

    private void setupRecycler() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.categories_column_num));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mCategoriesRecycler.setHasFixedSize(true);
        mCategoriesRecycler.setLayoutManager(gridLayoutManager);

        final CategoriesAdapter adapter = new CategoriesAdapter();
        mCategoriesRecycler.setAdapter(adapter);

    }


    public void setCategoryListener(OnClickCategoryListener listener) {
        mCategoryListener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCategoryListener = (OnClickCategoryListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCategoryListener = null;
    }

    @Override
    protected boolean shouldHideSearchView() {
        return false;
    }

    @Override
    protected AdView onCreateAdView() {
        return (AdView) getView().findViewById(R.id.ad_view);
    }

    @Override
    protected void attachAdView(AdView adView) {
        adView.attachToRecyclerView(mCategoriesRecycler);
    }

    public static interface OnClickCategoryListener {
        void onClickCategory(final Category category);
    }


    private interface CategoryListener {
        void onClickCategory(View layout);
    }

    class CategoriesAdapter extends RecyclerView.Adapter<CategoryViewHolder> implements CategoryListener {


        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_category, parent, false);
            return new CategoryViewHolder(itemView, this);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            final Category category = mCategories.get(position);

            loadCategory(holder, category);

        }

        @Override
        public int getItemCount() {
            return mCategories.size();
        }


        private void loadCategory(final CategoryViewHolder holder, final Category category) {


            ImageLoader.getInstance().displayImage("drawable://" + category.thumbnailRes, holder.thumbnail);

            holder.title.setTypeface(ViewUtil.getRegularDefaultTypeface());
            holder.title.setText(category.name);

            final Bitmap photo = category.getThumbnail();

            if (photo != null) {
                final String id = holder.title.getText().toString();
                final Context context = holder.thumbnail.getContext();


                PaletteLoader.with(context, id)
                        .load(photo)
                        .setPaletteRequest(new PaletteRequest(PaletteRequest.SwatchType.REGULAR_VIBRANT, PaletteRequest.SwatchColor.BACKGROUND))
                        .into(holder.bottomBar);


                PaletteLoader.with(context, id)
                        .load(photo)
                        .setPaletteRequest(new PaletteRequest(PaletteRequest.SwatchType.REGULAR_VIBRANT, PaletteRequest.SwatchColor.TEXT_TITLE))
                        .into(holder.title);
            }
        }

        @Override
        public void onClickCategory(View layout) {

            final int position = ViewUtil.getPositionOfChild(layout, R.id.category_layout, mCategoriesRecycler);

            if (mCategoryListener != null) {
                mCategoryListener.onClickCategory(mCategories.get(position));
            } else {
                Log.w("onCategoryClick", "Category listener is null");
            }
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.category_thumbnail)
        ImageView thumbnail;
        @InjectView(R.id.category_bottom_title)
        TextView title;
        @InjectView(R.id.category_bottom_bar)
        FrameLayout bottomBar;

        private CategoryListener listener;

        public CategoryViewHolder(View itemView, CategoryListener listener) {
            super(itemView);
            this.listener = listener;

            ButterKnife.inject(this, itemView);
        }

        @OnClick(R.id.category_layout)
        public void onClickLayout(View itemView) {
            listener.onClickCategory(itemView);
        }

    }

}
