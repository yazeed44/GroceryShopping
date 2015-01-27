package net.yazeed44.groceryshopping.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by yazeed44 on 1/25/15.
 */
public class ShareApp {


    public final Drawable icon;
    public CharSequence appLabel;
    private ActivityInfo mActivityInfo;
    private CharSequence mText;
    private AppClickListener mListener;

    private ShareApp(final Builder builder) {
        mActivityInfo = builder.activityInfo;
        mText = builder.text;
        appLabel = builder.appLabel;
        icon = builder.icon;
        mListener = builder.listener;
    }

    public void onClickApp(final Context context) {
        mListener.onClickApp(context, mActivityInfo, mText);
    }


    public static interface AppClickListener {
        void onClickApp(final Context context, final ActivityInfo activityInfo, final CharSequence text);
    }

    public static class Builder {
        private ActivityInfo activityInfo;
        private CharSequence appLabel;
        private Drawable icon;
        private String type = "text/plain";
        private String action = Intent.ACTION_SEND;
        private AppClickListener listener = new AppClickListener() {
            @Override
            public void onClickApp(Context context, ActivityInfo activityInfo, CharSequence text) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(action);
                sendIntent.putExtra(Intent.EXTRA_TEXT, text);

                sendIntent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));

                sendIntent.setType(type);
//        startActivity(Intent.createChooser(sendIntent, "Share"));
                context.startActivity(sendIntent);
            }
        };
        private String extraType = Intent.EXTRA_TEXT;
        private CharSequence text;


        public Builder(final CharSequence label) {
            this.appLabel = label;
        }

        public static Builder from(final ResolveInfo resolveInfo, final Context context) {
            final CharSequence label = resolveInfo.loadLabel(context.getPackageManager());
            final Drawable icon = resolveInfo.loadIcon(context.getPackageManager());
            final ActivityInfo activityInfo = resolveInfo.activityInfo;

            return new Builder(label)
                    .setActivityInfo(activityInfo)
                    .setIcon(icon);
        }


        public Builder setIcon(final Drawable drawable) {
            this.icon = drawable;
            return this;
        }

        public Builder setType(final String type) {
            this.type = type;
            return this;
        }

        public Builder setActivityInfo(final ActivityInfo info) {
            this.activityInfo = info;
            return this;
        }

        public Builder setAction(final String action) {
            this.action = action;
            return this;
        }

        public Builder setExtraType(final String extraType) {
            this.extraType = extraType;
            return this;
        }


        public Builder setClickListener(final AppClickListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setText(final CharSequence text) {
            this.text = text;
            return this;
        }

        public ShareApp build() {
            return new ShareApp(this);
        }
    }
}
