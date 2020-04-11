package com.palmapp.master.module_ad.manager;

import android.app.Activity;

public final class AdParams {
    private Activity mActivity;

    public AdParams(Builder builder) {
        mActivity = builder.mActivity;
    }

    public Activity getActivity() {
        return mActivity;
    }


    public static class Builder {
        private Activity mActivity;
        private int mPosition;
        private boolean mIsNoad;
        private String mAppMonetId;

        public Builder() {
            mActivity = null;
            mPosition = -1;
            mIsNoad = false;
            mAppMonetId = null;
        }

        public Builder withAppMonetId(String id) {
            mAppMonetId = id;
            return this;
        }

        public Builder withActivity(Activity activity) {
            mActivity = activity;
            return this;
        }

        public Builder withPosition(int position) {
            mPosition = position;
            return this;
        }

        public Builder withIsNoAd(boolean isNoAd) {
            mIsNoad = isNoAd;
            return this;
        }

        public AdParams build() {
            return new AdParams(this);
        }
    }
}
