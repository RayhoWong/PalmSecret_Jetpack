package com.palmapp.master.module_imageloader.glide;

import android.graphics.Bitmap;

public abstract class ImageLoadingListener {
    public void onLoadingStarted(Bitmap bitmap){}

    public void onLoadingFailed(Bitmap bitmap){}

    public void onLoadingComplete(Bitmap bitmap){}
}
