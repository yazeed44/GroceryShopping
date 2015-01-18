package net.yazeed44.groceryshopping.ui;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Category;
import net.yazeed44.groceryshopping.utils.DBUtil;
import net.yazeed44.groceryshopping.utils.Item;
import net.yazeed44.groceryshopping.utils.ViewUtil;


public class MainActivity extends BaseActivity implements CategoriesFragment.onClickCategoryListener, ItemsFragment.OnCheckItemListener {


    public static final String CATEGORY_INDEX_KEY = "categoryIndexKey";
    public static SparseArray<Item> sCheckedItems = new SparseArray<>();
    private CategoriesFragment mCategoriesFragment;
    private SearchView mSearchView;
    private ItemsTabsFragment mItemsFragment;
    private SearchItemsFragment mSearchItemsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(null);

        showCategories(savedInstanceState);

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
                mCategoriesFragment.setListener(this);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, mCategoriesFragment)
                    .commit();

        }
    }


    private void setupSearchView() {
        mSearchView.setQueryHint(getResources().getString(R.string.hint_search_items));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            private void query(final String query) {
                //TODO

                if (mSearchItemsFragment == null || !mSearchItemsFragment.isVisible()) {
                    showSearchFragment();
                }


                if (mSearchItemsFragment.isVisible()) {
                    Log.d("setupSearchView", "Query   " + query);
                    mSearchItemsFragment.query(query);
                }


            }

            @Override
            public boolean onQueryTextSubmit(String s) {
                query(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                query(s);
                return true;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                getSupportFragmentManager().popBackStack();

                mSearchView.clearFocus();
                return false;
            }
        });
    }

    private void showSearchFragment() {
        //TODO Add animations
        if (mSearchItemsFragment == null) {
            mSearchItemsFragment = new SearchItemsFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, mSearchItemsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);


        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        setupSearchView();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClickCategory(Category category) {
        //TODO add animation

        showItems(category);
    }


    private void showItems(final Category category) {

        if (mItemsFragment == null) {
            mItemsFragment = new ItemsTabsFragment();
            mItemsFragment.setCheckListener(this);
            Log.d("showItems", "Items Fragment has been initalized");
        }


        final Bundle chosenCategoryBundle = new Bundle();
        chosenCategoryBundle.putInt(CATEGORY_INDEX_KEY, DBUtil.getCategories().indexOf(category));

        mItemsFragment.setArguments(chosenCategoryBundle);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, mItemsFragment)
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  getSupportActionBar().setTitle(category.name);
    }

    @Override
    public void onBackPressed() {


        if (mItemsFragment != null && mItemsFragment.isVisible()) {
            getSupportFragmentManager().popBackStack();
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        } else if (mSearchItemsFragment != null && mSearchItemsFragment.isVisible()) {

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onCheck(Item item) {
        sCheckedItems.put(item.key, item);
        Log.d("onCheck", item.name + "  has been checked");

    }

    @Override
    public void onUnCheck(Item item) {
        sCheckedItems.remove(item.key);
        Log.d("onUnCheck", item.name + "  has been Un checked");


    }
}
