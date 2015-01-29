package net.yazeed44.groceryshopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Category;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.Item;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity implements CategoriesFragment.OnClickCategoryListener, ItemsFragment.OnCheckItemListener {


    public static final String CATEGORY_INDEX_KEY = "categoryIndexKey";
    public static final ArrayList<Item> CHOSEN_ITEMS = new ArrayList<>();
    @InjectView(R.id.items_search_view)
    SearchView mItemsSearchView;
    private CategoriesFragment mCategoriesFragment;
    private ItemsTabsFragment mItemsFragment;
    private SearchItemsFragment mSearchItemsFragment;
    private ShoppingCartActionView mShoppingCartView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(null);

        ButterKnife.inject(this);

        showCategories(savedInstanceState);

        setupItemsSearch();

    }


    private void initUtils() {
        ViewUtil.init(this);
        ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).build());
        DBUtil.initInstance(this);
    }

    /*private void setupToolbar(){
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }*/

    private void showCategories(final Bundle bundle) {

        if (bundle == null) {
            initUtils();

            if (mCategoriesFragment == null) {
                mCategoriesFragment = new CategoriesFragment();
                mCategoriesFragment.setCategoryListener(this);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, mCategoriesFragment)
                    .commit();

        }
    }

    private void showSearchFragment() {
        //TODO Add animations
        if (mSearchItemsFragment == null) {
            mSearchItemsFragment = new SearchItemsFragment();
            mSearchItemsFragment.setListener(this);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, mSearchItemsFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupItemsSearch() {


        mItemsSearchView.setQueryHint(getResources().getString(R.string.hint_search_items));

        mItemsSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSearchFragment();
            }
        });


        mItemsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void query(final String query) {

                if (mSearchItemsFragment != null)
                mSearchItemsFragment.query(query);
            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                query(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query(s);
                return false;
            }
        });


        mItemsSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mItemsSearchView.clearFocus();
                getSupportFragmentManager().popBackStack();
                return false;
            }
        });

        mItemsSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemsSearchView.onActionViewExpanded();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_shopping_cart, menu);


        mShoppingCartView = (ShoppingCartActionView) MenuItemCompat.getActionView(menu.findItem(R.id.action_shopping_cart));
        updateShoppingCart();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {


            case R.id.action_shopping_cart:
                openReviewItemsActivity();

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    private void openReviewItemsActivity() {
        final Intent intent = new Intent(this, ReviewItemsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClickCategory(Category category) {
        //TODO add animation

        showItems(category);
        hideSearchView();
    }


    private void showItems(final Category category) {

        if (mItemsFragment == null) {
            mItemsFragment = new ItemsTabsFragment();
            mItemsFragment.setCheckListener(this);
            Log.d("showItems", "Items Fragment has been initialized");
        }


        final Bundle chosenCategoryBundle = new Bundle();
        chosenCategoryBundle.putInt(CATEGORY_INDEX_KEY, DBUtil.getCategories().indexOf(category));

        mItemsFragment.setArguments(chosenCategoryBundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, mItemsFragment)
                .addToBackStack(null)
                .commit();


    }

    @Override
    public void onBackPressed() {


        if (mItemsFragment != null && mItemsFragment.isVisible()) {
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            showSearchView();

        } else if (mSearchItemsFragment != null && mSearchItemsFragment.isVisible()) {

            mItemsSearchView.onActionViewCollapsed();
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        } else {
            super.onBackPressed();
        }
    }
    private void updateShoppingCart() {
        if (mShoppingCartView != null)
            mShoppingCartView.updateCounter(CHOSEN_ITEMS.size());
    }

    private void showSearchView() {
        ((View) mItemsSearchView.getParent()).setVisibility(View.VISIBLE);
    }

    private void hideSearchView() {
        ((View) mItemsSearchView.getParent()).setVisibility(View.GONE);
    }

    @Override
    public void onAddItem(Item item) {
        CHOSEN_ITEMS.add(item);
        Log.d("onAddItem", item.name + "  has been checked");
        updateShoppingCart();

    }

    @Override
    public void onRemoveItem(Item item) {
        CHOSEN_ITEMS.remove(item);
        Log.d("onRemoveItem", item.name + "  has been Un checked");
        updateShoppingCart();


    }

}
