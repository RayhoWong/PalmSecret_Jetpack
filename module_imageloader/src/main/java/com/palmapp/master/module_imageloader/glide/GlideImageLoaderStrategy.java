package com.palmapp.master.module_imageloader.glide;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Looper;
import android.util.SparseArray;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.palmapp.master.baselib.manager.ThreadExecutorProxy;
import com.palmapp.master.baselib.utils.LogUtil;
import com.palmapp.master.module_imageloader.BaseImageLoaderStrategy;
import com.palmapp.master.module_imageloader.ImageLoader;

import java.lang.ref.WeakReference;

/**
 * Glide 加载的策略,
 * 注意：
 * 1.不能给 ImageView 设置 Tag，
 * 2.尽量在主线程调用，避免图片出现错位
 * 3.如果不设置Placeholder（占位图），在加载时会把ImageView之前设置的Drawable清除，导致预览时为空
 */
@SuppressLint("CheckResult")
public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy<ImageConfigImpl> {
    private SparseArray<WeakReference<Drawable>> mPlaceHolder = new SparseArray<>();

    @Override
    public void loadImage(Context ctx, ImageConfigImpl config) {
        if (ctx == null) throw new NullPointerException("Context is required");
        if (config == null) throw new NullPointerException("ImageConfigImpl is required");

        ImageView imageView = config.getImageView();
        if (imageView == null && config.imageLoadingListener() == null)
            throw new NullPointerException("Imageview is required");


        if (!(Looper.myLooper() == Looper.getMainLooper())) {
            ThreadExecutorProxy.INSTANCE.runOnMainThread(new MyRunnable(ctx, config), 0);
            LogUtil.d("currentThread is not MainThread");
            return;
        }

        GlideRequests requests;
        try {
            requests = GoGlide.with(ctx);//如果context是activity则自动使用Activity的生命周期
        } catch (IllegalArgumentException e) {
            LogUtil.e(LogUtil.TAG_YXQ, e.getMessage());
            return;
        }
        GlideRequest<Drawable> glideRequest = null;
        if(config.getHolder().string!=null){
            glideRequest = requests.load(config.getHolder().string);
        }else if(config.getHolder().id!=0){
            glideRequest = requests.load(config.getHolder().id);
        }else if(config.getHolder().file!=null){
            glideRequest = requests.load(config.getHolder().file);
        }else if(config.getHolder().uri!=null){
            glideRequest = requests.load(config.getHolder().uri);
        }

        switch (config.getCacheStrategy()) {//缓存策略
            case 0:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;
            case 1:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE);
                break;
            case 2:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                break;
            case 3:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.DATA);
                break;
            case 4:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                break;
            default:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;
        }

        glideRequest.skipMemoryCache(config.isSkipMemoryCache());
        glideRequest.dontAnimate();
        if (config.isCrossFade()) {
            glideRequest.transition(DrawableTransitionOptions.withCrossFade());
        }

        if (config.isCenterCrop()) {
            glideRequest.centerCrop();
        }

        if (config.isCircle()) {
            glideRequest.circleCrop();
        }

        if (config.isImageRadius()) {
            glideRequest.transform(new RoundedCorners(config.getImageRadius()));
        }

        if (config.isOverrideSize()) {
            glideRequest.override(config.getWidth(), config.getHeight());
        }

//        if (config.bitmapConfig() != null) {
//            Bitmap.Config bitmapConfig = config.bitmapConfig();
//            if (bitmapConfig == Bitmap.Config.ARGB_8888) {
//                requests.setRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888));
//            } else if (bitmapConfig == Bitmap.Config.RGB_565) {
//            }
//        }
        glideRequest.format(DecodeFormat.PREFER_RGB_565);

        //设置占位符
        if (config.getPlaceholder() > 0) {
            WeakReference<Drawable> weakReference = mPlaceHolder.get(config.getPlaceholder());
            if (weakReference != null && weakReference.get() != null) {
                glideRequest.placeholder(weakReference.get());
            } else {
                Drawable temp = ctx.getResources().getDrawable(config.getPlaceholder());
                mPlaceHolder.put(config.getPlaceholder(), new WeakReference<Drawable>(temp));
                glideRequest.placeholder(temp);
            }
            glideRequest.placeholder(config.getPlaceholder());
        } else if (config.getPlaceholderDrawable() != null) {
            glideRequest.placeholder(config.getPlaceholderDrawable());
        }

        if (config.getErrorPic() > 0)//设置错误的图片
            glideRequest.error(config.getErrorPic());

        if (config.getFallback() > 0)//设置请求 url 为空图片
            glideRequest.fallback(config.getFallback());

        glideRequest.disallowHardwareConfig();

        final ImageLoadingListener imageLoadingListener = config.imageLoadingListener();
        if (imageLoadingListener != null) {
            if (imageView == null) {
                glideRequest.into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        imageLoadingListener.onLoadingStarted(drawable2Bitmap(placeholder));
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        imageLoadingListener.onLoadingComplete(drawable2Bitmap(resource));
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        imageLoadingListener.onLoadingFailed(drawable2Bitmap(errorDrawable));
                    }
                });

            } else {

                glideRequest.into(new DrawableImageViewTarget(config.getImageView()) {

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        imageLoadingListener.onLoadingStarted(drawable2Bitmap(placeholder));
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        imageLoadingListener.onLoadingFailed(drawable2Bitmap(errorDrawable));
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        super.onResourceReady(resource, transition);
                        imageLoadingListener.onLoadingComplete(drawable2Bitmap(resource));
                    }
                });

            }

        } else {
            glideRequest
                    .into(config.getImageView());
        }

    }

    @Override
    public void clear(final Context ctx, ImageConfigImpl config) {
        if (ctx == null) throw new NullPointerException("Context is required");
        if (config == null) throw new NullPointerException("ImageConfigImpl is required");

        if (config.getImageViews() != null && config.getImageViews().length > 0) {//取消在执行的任务并且释放资源
            for (ImageView imageView : config.getImageViews()) {
                GoGlide.get(ctx).getRequestManagerRetriever().get(ctx).clear(imageView);
            }
        }

        if (config.isClearDiskCache()) {//清除本地缓存、
            ThreadExecutorProxy.INSTANCE.execute(new Runnable() {
                @Override
                public void run() {
                    Glide.get(ctx).clearDiskCache();
                }
            }, "GlideImageLoaderStrategy_1", 5);
        }

        if (config.isClearMemory()) {//清除内存缓存
            ThreadExecutorProxy.INSTANCE.execute(new Runnable() {
                @Override
                public void run() {
                    Glide.get(ctx).clearMemory();
                }
            }, "GlideImageLoaderStrategy_2", 5);
        }

    }

    private Bitmap drawable2Bitmap(Drawable drawable) {

        try {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof NinePatchDrawable) {
                Bitmap bitmap = Bitmap
                        .createBitmap(
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(),
                                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                        : Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                drawable.draw(canvas);
                return bitmap;
            }

        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }

        return null;

    }

    private static class MyRunnable implements Runnable {
        private ImageConfigImpl mConfig;
        private WeakReference<Context> mRefContext;

        private MyRunnable(Context context, ImageConfigImpl config) {
            mRefContext = new WeakReference<>(context);
            mConfig = config;
        }

        @Override
        public void run() {
            if (mRefContext.get() == null) {
                LogUtil.e("Context is null");
                return;
            }
            ImageLoader.getInstance().loadImage(mRefContext.get(), mConfig);

        }
    }
}
