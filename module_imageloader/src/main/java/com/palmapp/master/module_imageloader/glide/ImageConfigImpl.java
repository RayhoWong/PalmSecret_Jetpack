package com.palmapp.master.module_imageloader.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.palmapp.master.module_imageloader.DataHolder;
import com.palmapp.master.module_imageloader.ImageConfig;

/**
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 * <p>
 * **注意**
 * 一次操作就要创建一个对象，不能重复使用
 */
public class ImageConfigImpl extends ImageConfig {
    private int cacheStrategy;//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    private int fallback; //请求 url 为空,则使用此图片作为占位符
    private Drawable placeholderDrawable;
    private int imageRadius;//图片每个圆角的大小
    private int blurValue;//高斯模糊值, 值越大模糊效果越大
    private ImageView[] imageViews;
    private boolean isCrossFade;//是否使用淡入淡出过渡动画
    private boolean isCenterCrop;//是否将图片剪切为 CenterCrop
    private boolean isCircle;//是否将图片剪切为圆形
    private boolean isClearMemory;//清理内存缓存
    private boolean isClearDiskCache;//清理本地缓存
    private boolean skipMemoryCache; //跳过内存缓存
    private ImageLoadingListener imageLoadingListener;//清理本地缓存
    private int width;
    private int height;
    private Bitmap.Config bitmapConfig;


    private ImageConfigImpl(Builder builder) {
        this.holder = builder.holder;
        this.imageView = builder.imageView;
        this.placeholder = builder.placeholder;
        this.placeholderDrawable = builder.placeholderDrawable;
        this.errorPic = builder.errorPic;
        this.fallback = builder.fallback;
        this.cacheStrategy = builder.cacheStrategy;
        this.imageRadius = builder.imageRadius;
        this.blurValue = builder.blurValue;
        this.imageViews = builder.imageViews;
        this.isCrossFade = builder.isCrossFade;
        this.isCenterCrop = builder.isCenterCrop;
        this.isCircle = builder.isCircle;
        this.isClearMemory = builder.isClearMemory;
        this.isClearDiskCache = builder.isClearDiskCache;
        this.skipMemoryCache = builder.skipMemoryCache;
        this.imageLoadingListener = builder.imageLoadingListener;

        this.width = builder.width;
        this.height = builder.height;
        this.bitmapConfig = builder.bitmapConfig;

    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public boolean isClearMemory() {
        return isClearMemory;
    }

    public boolean isClearDiskCache() {
        return isClearDiskCache;
    }

    public ImageLoadingListener imageLoadingListener() {
        return imageLoadingListener;
    }

    public int getFallback() {
        return fallback;
    }

    public Drawable getPlaceholderDrawable() {
        return placeholderDrawable;
    }

    public int getBlurValue() {
        return blurValue;
    }

    public boolean isBlurImage() {
        return blurValue > 0;
    }

    public int getImageRadius() {
        return imageRadius;
    }

    public boolean isImageRadius() {
        return imageRadius > 0;
    }

    public boolean isOverrideSize() {
        return width > 0 && height > 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isCrossFade() {
        return isCrossFade;
    }

    public boolean isCenterCrop() {
        return isCenterCrop;
    }

    public boolean isSkipMemoryCache() {
        return skipMemoryCache;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public Bitmap.Config bitmapConfig() {
        return bitmapConfig;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private DataHolder holder;
        private ImageView imageView;
        private Drawable placeholderDrawable;
        private int placeholder;
        private int errorPic;
        private int fallback; //请求 url 为空,则使用此图片作为占位符
        /**
         * 0 对应DiskCacheStrategy.all,
         * 1 对应DiskCacheStrategy.NONE,
         * 2 对应DiskCacheStrategy.SOURCE,
         * 3 对应DiskCacheStrategy.RESULT
         */
        private int cacheStrategy;
        private int imageRadius;//图片每个圆角的大小
        private int blurValue;//高斯模糊值, 值越大模糊效果越大
        private int width;
        private int height;
        private ImageView[] imageViews;
        private boolean isCrossFade;//是否使用淡入淡出过渡动画
        private boolean isCenterCrop;//是否将图片剪切为 CenterCrop
        private boolean isCircle;//是否将图片剪切为圆形
        private boolean isClearMemory;//清理内存缓存
        private boolean isClearDiskCache;//清理本地缓存
        private boolean skipMemoryCache; //跳过内存缓存
        private ImageLoadingListener imageLoadingListener;//加载监听
        private Bitmap.Config bitmapConfig;

        private Builder() {
        }

        public Builder data(DataHolder data) {
            this.holder = data;
            return this;
        }

        public Builder placeholder(int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder placeholderDrawable(Drawable placeholder) {
            this.placeholderDrawable = placeholder;
            return this;
        }

        public Builder errorPic(int errorPic) {
            this.errorPic = errorPic;
            return this;
        }

        public Builder fallback(int fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder cacheStrategy(int cacheStrategy) {
            this.cacheStrategy = cacheStrategy;
            return this;
        }

        public Builder imageRadius(int imageRadius) {
            this.imageRadius = imageRadius;
            return this;
        }

        public Builder blurValue(int blurValue) { //blurValue 建议设置为 15
            this.blurValue = blurValue;
            return this;
        }

        public Builder imageViews(ImageView... imageViews) {
            this.imageViews = imageViews;
            return this;
        }

        public Builder isCrossFade(boolean isCrossFade) {
            this.isCrossFade = isCrossFade;
            return this;
        }

        public Builder isCenterCrop(boolean isCenterCrop) {
            this.isCenterCrop = isCenterCrop;
            return this;
        }

        public Builder isCircle(boolean isCircle) {
            this.isCircle = isCircle;
            return this;
        }

        public Builder isClearMemory(boolean isClearMemory) {
            this.isClearMemory = isClearMemory;
            return this;
        }

        public Builder isClearDiskCache(boolean isClearDiskCache) {
            this.isClearDiskCache = isClearDiskCache;
            return this;
        }

        public Builder skipMemoryCache(boolean skipMemoryCache) {
            this.skipMemoryCache = skipMemoryCache;
            return this;
        }

        public Builder bitmapConfig(Bitmap.Config bitmapConfig) {
            this.bitmapConfig = bitmapConfig;
            return this;
        }


        public Builder overrideSize(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }


        public Builder imageLoadingListener(ImageLoadingListener imageLoadingListener) {
            this.imageLoadingListener = imageLoadingListener;
            return this;
        }


        public ImageConfigImpl build() {
            return new ImageConfigImpl(this);
        }
    }
}
