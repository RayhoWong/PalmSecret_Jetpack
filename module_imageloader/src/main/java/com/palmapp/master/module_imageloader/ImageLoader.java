package com.palmapp.master.module_imageloader;

import android.content.Context;
import androidx.annotation.NonNull;
import com.palmapp.master.baselib.utils.LogUtil;


/**
 * ================================================
 * {@link ImageLoader} 可以动态切换图片请求框架(比如说切换成 Picasso )
 * 当需要切换图片请求框架或图片请求框架升级后变更了 Api 时
 * 这里可以将影响范围降到最低,所以封装 {@link ImageLoader} 是为了屏蔽这个风险
 */
public final class ImageLoader {

    private BaseImageLoaderStrategy mStrategy;

    private ImageLoader() {
    }

    public static void init(@NonNull BaseImageLoaderStrategy strategy) {
        SingletonHolder.INSTANCE.setLoadImgStrategy(strategy);
    }

    public static ImageLoader getInstance() {
        if (SingletonHolder.INSTANCE.mStrategy == null) {
            throw new IllegalStateException("please invoke init()");
        }
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final ImageLoader INSTANCE = new ImageLoader();
    }


    /**
     * 加载图片
     *
     * @param context
     * @param config
     * @param <T>
     */
    public <T extends ImageConfig> void loadImage(Context context, T config) {
        try {
            this.mStrategy.loadImage(context, config);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }



    /**
     * 停止加载或清理缓存
     *
     * @param context
     * @param config
     * @param <T>
     */
    public <T extends ImageConfig> void clear(Context context, T config) {
        this.mStrategy.clear(context, config);
    }

    /**
     * 可在运行时随意切换 {@link BaseImageLoaderStrategy}
     *
     * @param strategy
     */
    public void setLoadImgStrategy(@NonNull BaseImageLoaderStrategy strategy) {
        this.mStrategy = strategy;
    }

    public BaseImageLoaderStrategy getLoadImgStrategy() {
        return mStrategy;
    }
}
