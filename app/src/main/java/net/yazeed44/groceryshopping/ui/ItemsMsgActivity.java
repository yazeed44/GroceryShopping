package net.yazeed44.groceryshopping.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.ItemsMsgOrganizer;
import net.yazeed44.groceryshopping.utils.ShareApp;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by yazeed44 on 1/21/15.
 */
public class ItemsMsgActivity extends BaseActivity {


    public static final String APP_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.yousafco.groceryshopping";
    @InjectView(R.id.categories_items_recycler)
    RecyclerView mCategoriesRecycler;
    @InjectView(R.id.share_apps_recycler)
    RecyclerView mShareAppsRecycler;

    @InjectView(R.id.items_msg_note_txt_view)
    TextView mNoteTxt;

    private int mNoteCount = 1;
    private ItemsMsgOrganizer mItemsOrganizer;

    private Intent mShareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.actionbar_title_share_items);

        ButterKnife.inject(this);

        setupCategoriesItemsRecycler();
        initShareIntent();
        setupShareAppsRecycler();
    }

    private void initShareIntent() {
        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT, getShareTxt());
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.subject_items_msg));


    }

    private void updateShareIntent() {

        mNoteTxt.setVisibility(View.VISIBLE);
        mShareIntent.putExtra(Intent.EXTRA_TEXT, getShareTxt());
        setupShareAppsAdapter();
    }


    private void setupCategoriesItemsRecycler() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.categories_items_msg_column_num));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mCategoriesRecycler.setLayoutManager(gridLayoutManager);


        mItemsOrganizer = ItemsMsgOrganizer.from(MainActivity.CHOSEN_ITEMS);

        final List<ItemsMsgOrganizer.ItemsOrganized> itemsOrganized = mItemsOrganizer.organize();
        mCategoriesRecycler.setAdapter(new CategoriesItemsAdapter(itemsOrganized));

    }

    private void setupShareAppsRecycler() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.share_apps_column_num));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        mShareAppsRecycler.setLayoutManager(gridLayoutManager);
        setupShareAppsAdapter();

    }

    private void setupShareAppsAdapter() {
        mShareAppsRecycler.setAdapter(new ShareAppAdapter(getAppsToShare()));
    }

    private List<ShareApp> getAppsToShare() {
        final List<ShareApp> apps = new ArrayList<>();

        for (final ResolveInfo resolveInfo : getNativeAppsToShare()) {

            final ShareApp app = ShareApp.Builder.from(resolveInfo, this)
                    .setIntent(mShareIntent)
                    .build();

            apps.add(app);
        }

        //Adding copy content , technically it isn't an app

        apps.add(createCopyShare());


        return apps;

    }

    private ShareApp createCopyShare() {
        final String copyLabel = getResources().getString(R.string.copy_content);
        final Drawable copyIcon = getResources().getDrawable(R.drawable.ic_content_copy);


        return new ShareApp.Builder(copyLabel)
                .setIcon(copyIcon)
                .setIntent(mShareIntent)
                .setOnClickListener(new ShareApp.AppClickListener() {
                    @Override
                    public void onClickApp(Context context, ActivityInfo activityInfo) {
                        copyMsgToClipboard(getShareTxt().toString());
                    }
                }).build();
    }


    private void copyMsgToClipboard(final String txtToCopy) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(txtToCopy);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("msg", txtToCopy);
            clipboard.setPrimaryClip(clip);

        }

        ViewUtil.toastShort(this, R.string.toast_copied_msg_sucessfully);
    }

    private CharSequence getShareTxt() {

        final String shareAppTxt = getResources().getString(R.string.msg_shared_by) + " " + getResources().getString(R.string.app_name) + "\n" + APP_PLAY_STORE_URL;

        return "\n" + mItemsOrganizer.getText() + "\n" + mNoteTxt.getText() + "\n" + shareAppTxt;
    }

    private java.util.List<ResolveInfo> getNativeAppsToShare() {

        PackageManager packageManager = getPackageManager();
        return packageManager.queryIntentActivities(mShareIntent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_add_note:
                createTypeNoteDialog().show();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private MaterialDialog createTypeNoteDialog() {


        final EditText typeNoteTxt = new EditText(this);
        final String noteString = getResources().getString(R.string.hint_type_note);
        typeNoteTxt.setHint(noteString);


        return ViewUtil.createDialog(this)
                .title(R.string.title_type_note)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .customView(typeNoteTxt, false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        final CharSequence noteTxt = noteString + " " + mNoteCount++ + " : " + typeNoteTxt.getText() + Html.fromHtml("<br/>");
                        mNoteTxt.append(noteTxt);
                        updateShareIntent();
                    }
                }).build();
    }

    @Override
    protected AdRecyclerView onCreateAd() {
        //TODO Implement Ad
        return null;
    }

    private interface ShareAppListener {
        void onClickAppLayout(View appLayout);
    }

    private class CategoriesItemsAdapter extends RecyclerView.Adapter<CategoriesItemsViewHolder> {


        private List<ItemsMsgOrganizer.ItemsOrganized> mItemsOrganized;

        CategoriesItemsAdapter(final List<ItemsMsgOrganizer.ItemsOrganized> itemsOrganized) {

            mItemsOrganized = itemsOrganized;
        }


        @Override
        public CategoriesItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = getLayoutInflater().inflate(R.layout.element_items_msg, parent, false);
            return new CategoriesItemsViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(CategoriesItemsViewHolder holder, int position) {

            final ItemsMsgOrganizer.ItemsOrganized itemsOrganized = mItemsOrganized.get(position);

            holder.name.setText(itemsOrganized.categoryName);
            holder.items.setText(itemsOrganized.generateTxt());
        }

        @Override
        public int getItemCount() {
            return mItemsOrganized.size();
        }
    }

    class CategoriesItemsViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.items_msg_category_name)
        TextView name;

        @InjectView(R.id.items_msg_category_items)
        TextView items;

        public CategoriesItemsViewHolder(View itemView) {
            super(itemView);

            ButterKnife.inject(this, itemView);
        }
    }


    private class ShareAppAdapter extends RecyclerView.Adapter<ShareAppHolder> implements ShareAppListener {


        private List<ShareApp> mApps;

        private ShareAppAdapter(final List<ShareApp> apps) {
            mApps = apps;
        }

        @Override
        public ShareAppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View shareAppView = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_share_app, parent, false);

            return new ShareAppHolder(shareAppView, this);
        }

        @Override
        public void onBindViewHolder(ShareAppHolder holder, int position) {

            final ShareApp app = mApps.get(position);


            holder.mImage.setImageDrawable(app.icon);
            holder.mName.setText(app.appLabel);

        }

        @Override
        public int getItemCount() {
            return mApps.size();
        }

        @Override
        public void onClickAppLayout(View appLayout) {

            getShareAppObject(appLayout).onClickApp(appLayout.getContext());
        }

        private ShareApp getShareAppObject(View appLayout) {
            return mApps.get(mShareAppsRecycler.getChildPosition(appLayout));
        }
    }

    class ShareAppHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.share_app_image)
        ImageView mImage;

        @InjectView(R.id.share_app_name)
        TextView mName;


        private ShareAppListener mListener;

        public ShareAppHolder(View itemView, ShareAppListener listener) {
            super(itemView);
            mListener = listener;

            ButterKnife.inject(this, itemView);

            final int height = getResources().getDimensionPixelSize(R.dimen.share_app_column_height);
            final int width = getResources().getDimensionPixelSize(R.dimen.share_app_column_width);

            itemView.setLayoutParams(new AbsListView.LayoutParams(width, height));
        }

        @OnClick(R.id.share_app_layout)
        public void onClickLayout(View layoutView) {
            mListener.onClickAppLayout(layoutView);
        }


    }
}
