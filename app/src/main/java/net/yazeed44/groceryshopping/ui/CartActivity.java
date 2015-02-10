package net.yazeed44.groceryshopping.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFlat;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.Item;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by yazeed44 on 1/20/15.
 */
public class CartActivity extends BaseActivity {


    private String mAmountPrefix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_items);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.actionbar_title_cart);

        initPrefixes();
        initRecycler();


    }

    private void initPrefixes() {
        mAmountPrefix = getResources().getString(R.string.amount_review_item) + " : ";
    }

    private void initRecycler() {
        final RecyclerView itemsRecycler = (RecyclerView) findViewById(R.id.review_items_recycler);
        itemsRecycler.setHasFixedSize(true);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.review_items_column_num));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        itemsRecycler.setLayoutManager(gridLayoutManager);
        itemsRecycler.setAdapter(new ReviewItemsAdapter(itemsRecycler));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_done:
                openItemsMsgActivity();
                break;

            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void openItemsMsgActivity() {
        final Intent msgIntent = new Intent(this, ItemsMsgActivity.class);
        startActivity(msgIntent);
    }

    @Override
    protected AdView onCreateAd() {
        //TODO
        return null;
    }

    private static interface ReviewItemListener {

        void onClickPutAmount(final View putAmountBtn);

        void onClickClear(final View clearView);
    }

    private class ReviewItemsAdapter extends RecyclerView.Adapter<ReviewItemsHolder> implements ReviewItemListener {
        private RecyclerView mRecycler;

        private ReviewItemsAdapter(final RecyclerView recyclerView) {
            mRecycler = recyclerView;
        }

        @Override
        public ReviewItemsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View reviewItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_review_item, parent, false);
            return new ReviewItemsHolder(reviewItemView, this);
        }

        @Override
        public void onBindViewHolder(ReviewItemsHolder holder, int position) {
            final Item item = MainActivity.CHOSEN_ITEMS.get(position);


            holder.mTitle.setText(item.name);
            holder.mAmount.setText(mAmountPrefix + item.getAmount() + " " + item.getChosenUnit());


        }

        @Override
        public int getItemCount() {
            return MainActivity.CHOSEN_ITEMS.size();
        }


        private void showTypeAmountDialog(final Item item) {
            final View typeAmountView = getLayoutInflater().inflate(R.layout.dialog_type_amount, null);
            final EditText amountTxt = ButterKnife.findById(typeAmountView, R.id.type_amount_txt);
            final Spinner unitSpinner = ButterKnife.findById(typeAmountView, R.id.type_amount_unit_spinner);
            unitSpinner.setAdapter(createUnitAdapter(item));


            createAskDialog(typeAmountView)
                    .title(item.name)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            item.setAmount(amountTxt.getText().toString());
                            item.setChosenUnit(unitSpinner.getSelectedItem().toString());
                            Log.i("edit Amount", item.name + "  has assigned new amount  " + item.getAmount());
                            updateChild(item);
                        }
                    }).show();

        }

        private SpinnerAdapter createUnitAdapter(final Item item) {

            final ArrayAdapter<String> units = new ArrayAdapter<String>(CartActivity.this, android.R.layout.simple_spinner_item, item.units);
            units.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            return units;
        }

        private MaterialDialog.Builder createAskDialog(final View view) {
            return ViewUtil.createDialog(CartActivity.this)
                    .positiveText(R.string.ok)
                    .negativeText(R.string.cancel)
                    .customView(view, false);


        }

        private void updateChild(final Item item) {
            final int position = MainActivity.CHOSEN_ITEMS.indexOf(item);

            notifyItemChanged(position);
            Log.d("updateChild", item.name + " should be updated by now");


        }


        @Override
        public void onClickPutAmount(View view) {
            final Item item = getItemObject(view);
            showTypeAmountDialog(item);
        }

        @Override
        public void onClickClear(View clearView) {
            final int itemIndex = MainActivity.CHOSEN_ITEMS.indexOf(getItemObject(clearView));
            final Item item = MainActivity.CHOSEN_ITEMS.get(itemIndex);


            ViewUtil.createDialog(CartActivity.this)
                    .content(getClearContent(item))
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            notifyItemRemoved(itemIndex);
                            MainActivity.CHOSEN_ITEMS.remove(item);
                        }
                    }).show();


            Log.d("CheckedItems", MainActivity.CHOSEN_ITEMS.toString());

        }

        private String getClearContent(final Item item) {
            final String baseContent = getResources().getString(R.string.content_clear_item_dialog);

            return baseContent.replace("0", item.name);
        }


        private Item getItemObject(final View view) {

            return MainActivity.CHOSEN_ITEMS.get(ViewUtil.getPositionOfChild(view, R.id.review_item_card, mRecycler));
        }
    }

    class ReviewItemsHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.review_item_title)
        TextView mTitle;
        @InjectView(R.id.review_item_amount)
        TextView mAmount;
        @InjectView(R.id.review_item_put_amount)
        ButtonFlat mPutAmount;
        @InjectView(R.id.review_item_clear)
        ImageView mClearImage;

        private ReviewItemListener mListener;

        public ReviewItemsHolder(View itemView, final ReviewItemListener listener) {
            super(itemView);
            mListener = listener;

            ButterKnife.inject(this, itemView);

            setupButtons();

        }

        private void setupButtons() {
            final Typeface flatButtonTypeface = Typeface.create(ViewUtil.getMediumTypeface(), Typeface.BOLD);

            mPutAmount.getTextView().setTypeface(flatButtonTypeface);

            final float textSize = getResources().getDimensionPixelSize(R.dimen.review_item_btns_text_size);
            mPutAmount.getTextView().setTextSize(textSize);
        }

        @OnClick(R.id.review_item_clear)
        public void clear(View view) {
            mListener.onClickClear(view);
        }

        @OnClick(R.id.review_item_put_amount)
        public void putAmount(View view) {
            mListener.onClickPutAmount(view);
        }


    }


}
