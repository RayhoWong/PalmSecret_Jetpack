package com.example.oldlib.utils;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.DisplayMetrics;

import java.io.InputStream;

public class ImageHelper {

    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    private static int HEIGHT = 0;

    private static int mMaxMemory;

    public static void init(Application application) {
        DisplayMetrics dm = application.getResources().getDisplayMetrics();
        boolean isLandscape = false;
        if (application.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isLandscape = true;
        }
        if (isLandscape) {//这样是为了保证 SCREEN_WIDTH一定是width  SCREEN_HEIGHT一定是height
            SCREEN_WIDTH = dm.heightPixels;
            SCREEN_HEIGHT = dm.widthPixels;
        } else {
            SCREEN_WIDTH = dm.widthPixels;
            SCREEN_HEIGHT = dm.heightPixels;
        }
        HEIGHT = SCREEN_HEIGHT;
        mMaxMemory = (int) Runtime.getRuntime().maxMemory();
//		if(mMaxMemory >= 536870912){
//			QUALITY = 0.7f;
//		} else{
//			QUALITY = 0.4f;
//		}
    }


    /**
     * 将Image Uri转华为Bitmap  用于编辑
     *
     * @return
     */
    public static Bitmap UriToBitmap(Context context,final Uri mUri) {
        if (mUri == null) {
            return null;
        }

        int insamplesize = 1;
        Bitmap bitmap = null;
        ContentResolver mCr = context.getContentResolver();
        Resources res = context.getResources();
        BitmapFactory.Options option = new BitmapFactory.Options();
        InputStream in1 = null;
        InputStream in2 = null;
        InputStream in3 = null;
        try {
            option.inJustDecodeBounds = true;
            in1 = mCr.openInputStream(mUri);
            bitmap = BitmapFactory.decodeStream(in1, null, option);
            /*
             * 原来的长宽
             */
            int width;
            int height;

            width = option.outWidth;
            height = option.outHeight;
            option.inJustDecodeBounds = false;
            option.inPreferredConfig = Bitmap.Config.ARGB_8888;
            option.inPurgeable = true;
            option.inInputShareable = true;
            option.inDither = false;

            float scale;
            scale = getFitSampleSizeLarger(width, height);
            scale = checkCanvasAndTextureSize(width, height, scale);

            int i = 1;
            while (scale / Math.pow(i, 2) > 1.0f) {
                i *= 2;
            }
            if (i != 1) {
                i = i / 2;
            }
            insamplesize = i;

            if (scale != 1.0f) {
                int targetDensity = res.getDisplayMetrics().densityDpi;
                option.inScaled = true;
                option.inDensity = (int) (targetDensity * Math.sqrt(scale / Math.pow(i, 2)) + 1);
                option.inTargetDensity = targetDensity;
            }

            option.inSampleSize = insamplesize;

            in2 = mCr.openInputStream(mUri);
            bitmap = BitmapFactory.decodeStream(in2, null, option);

//			if(bean.mDegree % 360 != 0){
//				bitmap = rotating(bitmap, bean.mDegree);
//			}
            bitmap = rotatingAndScale(bitmap, 0);
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
            /**
             * 如果内存溢出 缩小1/4再解析
             */
            option.inSampleSize = insamplesize * 2;
            try {
                in3 = mCr.openInputStream(mUri);
                bitmap = BitmapFactory.decodeStream(in3, null, option);
                bitmap = rotatingAndScale(bitmap, 0);
            } catch (Throwable e1) {
                System.gc();
                e1.printStackTrace();
            }
            return bitmap;
        } finally {
            try {
                if (in1 != null) {
                    in1.close();
                }
                if (in2 != null) {
                    in2.close();
                }
                if (in3 != null) {
                    in3.close();
                }
            } catch (Throwable e) {
            }
        }
//		return getScaleBitmap(bitmap);
        return bitmap;
    }


    /**
     * 编辑界面使用 用于旋转和缩放成合适大小的图片
     *
     * @param bitmap
     * @param degree
     * @return
     */
    public static Bitmap rotatingAndScale(Bitmap bitmap, float degree) {
        try {
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            Matrix m = new Matrix();

            if (degree % 360 != 0) {
                m.postRotate(degree, 0.5f, 0.5f);
            }

            int bw = width;
            int bh = height;
            if (degree == 90 || degree == 270) {
                bw = height;
                bh = width;
            }
            if (bw < SCREEN_WIDTH && bh < HEIGHT) {
                float w;
                float h;
                w = SCREEN_WIDTH;
                h = w * ((float) bh / (float) bw);
                if (h > HEIGHT) {
                    h = HEIGHT;
                    w = h * ((float) bw / (float) bh);
                }
                m.postScale(w / bw, h / bh);
            }
            Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);
            return result;
        } catch (Throwable e) {
            System.gc();
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 用于解析图片
     *
     * @param width
     * @param height
     * @param scale
     * @return
     */
    public static float checkCanvasAndTextureSize(int width, int height, float scale) {
        float result = scale;
        Canvas canvas = new Canvas();
        int maxW = canvas.getMaximumBitmapWidth() / 8;
        int maxH = canvas.getMaximumBitmapHeight() / 8;
        int max2 = 0;
        int max = 0;
        if (max2 != 0) {
            max = Math.min(Math.min(maxW, maxH), max2);
        } else {
            max = Math.min(maxW, maxH);
        }
        if (width * width / scale >= max * max || height * height / scale > max * max) {
            result = Math.max(width * 1.0f / max, height * 1.0f / max);
            result = result * result;
        }
        return result;
    }

    /**
     * 新策略用于图片预览 和 编辑的图片压缩
     * 向下取
     *
     * @param width
     * @param height
     * @return
     */
    public static float getFitSampleSizeLarger(int width, int height) {
//		int nw, nh;
//
//		//保证屏幕分辨率的质量
//		if(width * 1.0f / height >= SCREEN_WIDTH * 1.0f / SCREEN_HEIGHT){//宽顶着
//			nw = SCREEN_WIDTH;
//			nh = (int)(height * SCREEN_WIDTH * 1.0f / width + 0.5f);
//		} else{//高顶着
//			nh = SCREEN_HEIGHT;
//			nw = (int)(width * SCREEN_HEIGHT * 1.0f / height + 0.5f);
//		}
//
//		//nw nh是显示在屏幕上的像素宽高
        int size = width * height * 4;//原来的大小

//		int minSize = nw * nh * 4;
//		int midSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4;

        float maxSize;
        if (is1080PResolution()) {//高分辨率
            if (isHighMemory()) {
                maxSize = 1920 * 1080 * 4 * 2;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            } else {
                maxSize = 1920 * 1080 * 4 * 1.5f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            }
        } else if (is720PResolution()) {//中分辨率
            if (isHighMemory()) {
                maxSize = 1280 * 720 * 4 * 3.5f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            } else {
                maxSize = 1280 * 720 * 4 * 2.5f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            }
        } else {//低分辨率
            if (isHighMemory()) {
                maxSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4 * 4;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            } else {
                maxSize = SCREEN_WIDTH * SCREEN_HEIGHT * 4 * 2f;
                if (size > maxSize) {//这时候才需要压缩
                    return (size / maxSize);
                }
            }
        }

        return 1;

//		float count = size / memory;
//		if(count <= 1.0f){
//			return 1;
//		} else{
//
//		}
//
//		int result = 1;
//		while((size / (result * result)) > minSize){
//			result = result * 2;
//		}
//		if(result != 1){
//			result /= 2;
////			while(result != 1 && (size * 1.0f / (result * result / 4)) < midSize ){
////				result /= 2;
////			}
//		}
//		return result;
    }


    private static boolean is720PResolution() {
        int modeSize = 1280 * 720;
        int size = SCREEN_WIDTH * SCREEN_HEIGHT;
        if (size >= modeSize * 0.7) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean is1080PResolution() {
        int modeSize = 1920 * 1080;
        int size = SCREEN_WIDTH * SCREEN_HEIGHT;
        if (size >= modeSize * 0.7) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是不是
     *
     * @return
     */
    private static boolean isHighMemory() {
        if (mMaxMemory >= 536870912) {//512
            return true;
        } else {
            return false;
        }
    }


    /**
     * 新策略用于社区上传的压缩
     * 向下取
     *
     * @param width
     * @param height
     * @return
     */
    public static float getShareFitSampleSize(int width, int height) {
//		int nw, nh;
//
//		//保证屏幕分辨率的质量
//		if(width * 1.0f / height >= SCREEN_WIDTH * 1.0f / SCREEN_HEIGHT){//宽顶着
//			nw = SCREEN_WIDTH;
//			nh = (int)(height * SCREEN_WIDTH * 1.0f / width + 0.5f);
//		} else{//高顶着
//			nh = SCREEN_HEIGHT;
//			nw = (int)(width * SCREEN_HEIGHT * 1.0f / height + 0.5f);
//		}

        //nw nh是显示在屏幕上的像素宽高
        int size = width * height * 4;//原来的大小

        float maxSize = 1920 * 1080 * 4;
        if (size > maxSize) {//这时候才需要压缩
            return (size / maxSize);
        }
        return 1;
    }

}
