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
import net.yazeed44.groceryshopping.utils.ItemsMsgFormatter;
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

    @InjectView(R.id.items_msg_txt)
    TextView mMsgTxt;

    @InjectView(R.id.share_apps_recycler)
    RecyclerView mShareAppsRecycler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.actionbar_title_share_items);

        ButterKnife.inject(this);
        setupMsgText();
        setupShareApps();
    }

    private void setupShareApps() {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.share_apps_column_num));
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        mShareAppsRecycler.setLayoutManager(gridLayoutManager);
        mShareAppsRecycler.setAdapter(new ShareAppAdapter(getAppsToShare()));
    }

    private void setupMsgText() {
        mMsgTxt.setText(ItemsMsgFormatter.from(MainActivity.CHOSEN_ITEMS).format());
    }


    private List<ShareApp> getAppsToShare() {
        final List<ShareApp> apps = new ArrayList<>();

        for (final ResolveInfo resolveInfo : getNativeAppsToShare()) {

            final ShareApp app = ShareApp.Builder.from(resolveInfo, this)
                    .setText(getShareTxt())
                    .build();

            apps.add(app);
        }

        //Adding copy content , techincally it isn't an app

        apps.add(createCopyShare());

        return apps;

    }

    private ShareApp createCopyShare() {
        final String copyLabel = getResources().getString(R.string.copy_content);
        final Drawable copyIcon = getResources().getDrawable(R.drawable.ic_content_copy);


        return new ShareApp.Builder(copyLabel)
                .setIcon(copyIcon)
                .setText(getShareTxt())
                .setClickListener(new ShareApp.AppClickListener() {
                    @Override
                    public void onClickApp(Context context, ActivityInfo activityInfo, final CharSequence text) {
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

        return mMsgTxt.getText();
    }

    private java.util.List<ResolveInfo> getNativeAppsToShare() {


        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setType("text/plain");
        PackageManager packageManager = getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
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
        typeNoteTxt.setHint(R.string.hint_type_note);


        return ViewUtil.createDialog(this)
                .title(R.string.title_type_note)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .customView(typeNoteTxt, false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        mMsgTxt.append(getResources().getString(R.string.hint_type_note) + " : " + typeNoteTxt.getText() + Html.fromHtml("<br/>"));
                    }
                }).build();
    }

    private interface ShareAppListener {
        void onClickAppLayout(View appLayout);
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

            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.share_app_column_height)));
        }

        @OnClick(R.id.share_app_layout)
        public void onClickLayout(View layoutView) {
            mListener.onClickAppLayout(layoutView);
        }


    }
}
