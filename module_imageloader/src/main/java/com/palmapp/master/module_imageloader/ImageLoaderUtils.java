package com.palmapp.master.module_imageloader;

import android.content.Context;
import android.widget.ImageView;
import com.palmapp.master.module_imageloader.glide.ImageConfigImpl;
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener;

import java.io.File;

public class ImageLoaderUtils {

    public static void displayImage(String url, ImageView imageView) {
        ImageLoader.getInstance().loadImage(imageView.getContext(), ImageConfigImpl.builder().data(DataHolder.create(url)).imageView(imageView).build());
    }

    public static void displayImage(int id, ImageView imageView) {
        ImageLoader.getInstance().loadImage(imageView.getContext(), ImageConfigImpl.builder().data(DataHolder.create(id)).imageView(imageView).build());
    }

    public static void displayImage(int id, ImageView imageView,int defaultPlaceHolder){
        ImageLoader.getInstance().loadImage(imageView.getContext(), ImageConfigImpl.builder().data(DataHolder.create(id)).placeholder(defaultPlaceHolder).imageView(imageView).build());
    }

    public static void displayImage(String url, ImageView imageView,int defaultPlaceHolder){
        ImageLoader.getInstance().loadImage(imageView.getContext(), ImageConfigImpl.builder().data(DataHolder.create(url)).placeholder(defaultPlaceHolder).imageView(imageView).build());
    }

    public static void displayImageNoCache(Context context, String url, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(context, ImageConfigImpl.builder().data(DataHolder.create(url)).skipMemoryCache(true).cacheStrategy(1).imageLoadingListener(listener).build());
    }

    public static void displayImage(Context context, String url, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(context, ImageConfigImpl.builder().data(DataHolder.create(url)).imageLoadingListener(listener).build());
    }

    public static void displayImage(Context context, File file, ImageView imageView) {
        ImageLoader.getInstance().loadImage(context, ImageConfigImpl.builder().data(DataHolder.create(file)).skipMemoryCache(true).cacheStrategy(1).imageView(imageView).build());
    }


    /**
     *
     * @param placeholder 占位图
     * @param errorPic     失败
     * @param fallback      空url
     * @return  config 不能重复使用，一次操作对应一个config
     */
    public static ImageConfigImpl.Builder createConfig(int placeholder,int errorPic,int fallback){
        return ImageConfigImpl.builder().placeholder(placeholder).errorPic(errorPic).fallback(fallback);
    }
}
