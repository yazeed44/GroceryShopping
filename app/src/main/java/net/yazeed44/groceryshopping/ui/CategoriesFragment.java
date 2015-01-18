package net.yazeed44.groceryshopping.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
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


/**
 * Created by yazeed44 on 12/31/14.
 */
public class CategoriesFragment extends BaseFragment {


    private GridView mCategoriesList;
    private onClickCategoryListener mListener;
    private ArrayList<Category> mCategories = DBUtil.getCategories();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCategoriesList = (GridView) inflater.inflate(R.layout.fragment_categories, container, false);
        setupAdapter();


        return mCategoriesList;
    }

    @Override
    protected boolean shouldDisplayUp() {
        return false;
    }

    private void setupAdapter() {
        final CategoriesAdapter adapter = new CategoriesAdapter();
        mCategoriesList.setAdapter(adapter);

    }

    public void setListener(onClickCategoryListener listener) {
        mListener = listener;
    }


    public static interface onClickCategoryListener {
        public void onClickCategory(final Category category);
    }


    private class CategoriesAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {

        private CategoriesAdapter() {
            mCategoriesList.setOnItemClickListener(this);
        }


        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Category category = mCategories.get(position);

            final ViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.element_category, parent, false);
                holder = createHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            loadCategory(holder, category);
            setupHeight(convertView);


            return convertView;
        }


        private void loadCategory(final ViewHolder holder, final Category category) {


            ImageLoader.getInstance().displayImage("drawable://" + category.thumbnailRes, holder.thumbnail);

            holder.title.setTypeface(ViewUtil.getRegularDefaultTypeface());
            holder.title.setText(category.name);

            final Bitmap photo = category.getBitmap();

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

        private void setupHeight(final View convertView) {
            final int height = getResources().getDimensionPixelSize(R.dimen.category_height);

            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        }


        private ViewHolder createHolder(final View convertView) {
            final ViewHolder holder = new ViewHolder();
            holder.thumbnail = (ImageView) convertView.findViewById(R.id.category_thumbnail);
            holder.title = (TextView) convertView.findViewById(R.id.category_bottom_title);
            holder.bottomBar = (FrameLayout) convertView.findViewById(R.id.category_bottom_bar);
            return holder;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mListener.onClickCategory(mCategories.get(position));
        }


        private class ViewHolder {
            private ImageView thumbnail;
            private TextView title;
            private FrameLayout bottomBar;
        }
    }


}
