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
    private AppClickListener mListener;

    private ShareApp(final Builder builder) {
        mActivityInfo = builder.mActivityInfo;
        appLabel = builder.mAppLabel;
        icon = builder.mIcon;
        mListener = builder.listener;
    }

    public void onClickApp(final Context context) {
        mListener.onClickApp(context, mActivityInfo);
    }


    public static interface AppClickListener {
        void onClickApp(final Context context, final ActivityInfo activityInfo);
    }

    public static class Builder {
        private ActivityInfo mActivityInfo;
        private CharSequence mAppLabel;
        private Drawable mIcon;

        private Intent mIntent;

        private AppClickListener listener = new AppClickListener() {
            @Override
            public void onClickApp(Context context, ActivityInfo activityInfo) {

                mIntent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));


                context.startActivity(mIntent);
            }
        };


        public Builder(final CharSequence label) {
            this.mAppLabel = label;
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
            this.mIcon = drawable;
            return this;
        }

        public Builder setActivityInfo(final ActivityInfo info) {
            this.mActivityInfo = info;
            return this;
        }


        public Builder setOnClickListener(final AppClickListener listener) {
            this.listener = listener;
            return this;
        }


        public Builder setIntent(final Intent intent) {
            mIntent = (Intent) intent.clone();
            return this;
        }

        public ShareApp build() {
            return new ShareApp(this);
        }
    }
}
