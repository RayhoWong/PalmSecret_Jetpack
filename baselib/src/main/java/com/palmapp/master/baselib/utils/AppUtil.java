package com.palmapp.master.baselib.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Process;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.palmapp.master.baselib.GoCommonEnv;
import com.palmapp.master.baselib.constants.PreConstants;
import com.palmapp.master.baselib.manager.GoPrefManager;
import com.palmapp.master.baselib.proxy.GoogleServiceProxy;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author :     xiemingrui
 * @since :      2019/7/24
 */
public class AppUtil {
    private static String sGoogleId;

    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    @Nullable
    public static String getCurProcessName(Context context) {
        int myPid = Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = activityManager.getRunningAppProcesses();
        if (appProcessList != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
                if (appProcess.pid == myPid) {
                    return appProcess.processName;
                }
            }
        }

        java.lang.Process process = null;
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("ps " + myPid);
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            String[] texts = null;
            if (line != null) { //第一行为标题
                if ((line = reader.readLine()) != null) { //第二行才是数据
                    texts = line.split("\\s+", Integer.MAX_VALUE);
                    String name = texts[texts.length - 1];
                    return name;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 获取app 安装时间
     */
    public static long getInstalledTime(Context context, String packageName) {
        long installTime = 0;
        try {
            PackageManager manager = context.getPackageManager();
            if (manager != null) {
                PackageInfo info = manager.getPackageInfo(packageName, 0);
                if (info != null) {
                    installTime = info.firstInstallTime;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (installTime <= 0) {
            installTime = GoPrefManager.INSTANCE.getDefault().getLong(PreConstants.App.KEY_INSTALL_TIME, 0L);
        }
        if (installTime <= 0) {
            installTime = System.currentTimeMillis();
            GoPrefManager.INSTANCE.getDefault().putLong(PreConstants.App.KEY_INSTALL_TIME, installTime).commit();
        }
        return installTime;
    }

    /**
     * 获取Google Advertising Id
     * 注:该方法需要在异步线程中调用,因为AdvertisingIdClient.getAdvertisingIdInfo(mContext)不能在UI线程中执行.
     *
     * @return the device specific Advertising ID provided by the Google Play Services, <em>null</em> if an error
     * occurred.
     */
    public static String getGoogleAdvertisingId() {
        String id = GoogleServiceProxy.INSTANCE.getGoogleAdvertisingId();
        LogUtil.d(LogUtil.TAG_XMR, "getGoogleAdvertisingId:" + id);
        return id;
    }

    /**
     * 获取指定包的版本号
     *
     * @param context
     * @param pkgName
     * @author huyong
     */
    public static int getVersionCodeByPkgName(Context context, String pkgName) {
        int versionCode = 0;
        if (pkgName != null) {
            PackageManager pkgManager = context.getPackageManager();
            try {
                PackageInfo pkgInfo = pkgManager.getPackageInfo(pkgName, 0);
                versionCode = pkgInfo.versionCode;
            } catch (Exception e) {
                Log.i("AppUtils", "getVersionCodeByPkgName=" + pkgName + " has " + e.getMessage());
            }
        }
        return versionCode;
    }

    public static boolean isMainProcess(Context context) {
        return TextUtils.equals(getCurProcessName(context), GoCommonEnv.INSTANCE.getApplicationId());
    }

    public static void saveBitmapFile(Bitmap bitmap, String path) {
        File file = new File(path);//将要保存图片的路径
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getScreenW(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display.widthPixels;
    }

    public static int getScreenH(Context context) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        return display.heightPixels;
    }

    /**
     * 以等比缩放方式resize图片
     *
     * @param src        原始图片
     * @param destWidth  目标图片宽度
     * @param destHeight 目标图片高度
     * @return
     */
    public static Bitmap resizeBitmap(Bitmap src, int destWidth, int destHeight) {
        if (src == null || destWidth == 0 || destHeight == 0) {
            return null;
        }
        int w = src.getWidth();
        int h = src.getHeight();
        if (w == destWidth && h == destHeight) {
            return src;
        }
        Matrix m = new Matrix();
        final float sx = destWidth / (float) w;
        final float sy = destHeight / (float) h;
        final float scale = Math.min(sy, sx);
        m.setScale(scale, scale);
        return Bitmap.createBitmap(src, 0, 0, w, h, m, false);
    }

    public static Bitmap resizeBitmapByBlack(Bitmap src, int destWidth, int destHeight) {
        if (src == null || destWidth == 0 || destHeight == 0) {
            return null;
        }
        // 图片宽度
        int w = src.getWidth();
        // 图片高度
        int h = src.getHeight();
        // Imageview宽度
        int x = destWidth;
        // Imageview高度
        int y = destHeight;

        // 高宽比之差
        float temp = (y / (float) x) - (h / (float) w);
        /**
         * 判断高宽比例，如果目标高宽比例大于原图，则原图高度不变，宽度为(w1 = (h * x) / y)拉伸
         * 画布宽高(w1,h),在原图的((w - w1) / 2, 0)位置进行切割
         */
        Bitmap newb = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);
        Matrix m = new Matrix();
        final float sx = destWidth / (float) w;
        final float sy = destHeight / (float) h;
        final float scale = Math.min(sy, sx);
        m.setScale(scale, scale);
        Bitmap result = Bitmap.createBitmap(src, 0, 0, w, h, m, false);
        canvas.drawColor(Color.BLACK);
        RectF rectF = new RectF();
        if (temp > 0) {
            rectF.set(0, (destHeight - result.getHeight()) / 2f, result.getWidth(), (destHeight - result.getHeight()) / 2f + result.getHeight());
        } else {
            rectF.set((destWidth - result.getWidth()) / 2f, 0, (destWidth - result.getWidth()) / 2f + result.getWidth(), result.getHeight());
        }
        canvas.drawBitmap(result, null, rectF, null);
        return newb;
    }

    /**
     * 以CenterCrop方式resize图片
     *
     * @param src        原始图片
     * @param destWidth  目标图片宽度
     * @param destHeight 目标图片高度
     * @return
     */
    public static Bitmap resizeBitmapByCenterCrop(Bitmap src, int destWidth, int destHeight) {
        if (src == null || destWidth == 0 || destHeight == 0) {
            return null;
        }
        // 图片宽度
        int w = src.getWidth();
        // 图片高度
        int h = src.getHeight();
        // Imageview宽度
        int x = destWidth;
        // Imageview高度
        int y = destHeight;
        // 高宽比之差
        float temp = (y / (float) x) - (h / (float) w);
        /**
         * 判断高宽比例，如果目标高宽比例大于原图，则原图高度不变，宽度为(w1 = (h * x) / y)拉伸
         * 画布宽高(w1,h),在原图的((w - w1) / 2, 0)位置进行切割
         */
        Bitmap newb;
        if (temp > 0) {
            // 计算画布宽度
            int w1 = (h * x) / y;
            // 创建一个指定高宽的图片
            newb = Bitmap.createBitmap(src, (w - w1) / 2, 0, w1, h);
            //原图回收
        } else {
            /**
             * 如果目标高宽比小于原图，则原图宽度不变，高度为(h1 = (y * w) / x),
             * 画布宽高(w, h1), 原图切割点(0, (h - h1) / 2)
             */

            // 计算画布高度
            int h1 = (y * w) / x;
            // 创建一个指定高宽的图片
            newb = Bitmap.createBitmap(src, 0, (h - h1) / 2, w, h1);
            //原图回收
        }
        return Bitmap.createScaledBitmap(newb, destWidth, destHeight, false);
    }

    /**
     * 获取图片的四维数组
     *
     * @param bitmap bitmap对象
     * @param ddims  参数数组
     * @return 图片四维数组
     */
    public static float[][][][] getScaledMatrix(Bitmap bitmap, int[] ddims) {
        //新建一个1*256*256*3的四维数组
        float[][][][] inFloat = new float[ddims[0]][ddims[1]][ddims[2]][ddims[3]];
        //新建一个一维数组，长度是图片像素点的数量
        int[] pixels = new int[ddims[1] * ddims[2]];
        //把原图缩放成我们需要的图片大小
//        Bitmap bm = resizeBitmapByBlack(bitmap, ddims[1], ddims[2]);
        //把图片的每个像素点的值放到我们前面新建的一维数组中
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, ddims[1], ddims[2], false);
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, ddims[1], ddims[2]);
        int pixel = 0;
        //for循环，把每个像素点的值转换成RBG的值，存放到我们的目标数组中
        for (int i = 0; i < ddims[1]; ++i) {
            for (int j = 0; j < ddims[2]; ++j) {
                final int val = pixels[pixel++];
                float red = ((val >> 16) & 0xFF) / 255f;
                float green = ((val >> 8) & 0xFF) / 255f;
                float blue = (val & 0xFF) / 255f;
                float[] arr = {red, green, blue};
                inFloat[0][i][j] = arr;
            }
        }
        if (bm.isRecycled()) {
            bm.recycle();
        }
        return inFloat;
    }

    /**
     * 四维数组转成bitmap对象
     *
     * @param outArr 数组
     * @param ddims  格式
     * @return bitmap
     */
    public static Bitmap getBitmap(float[][][][] outArr, int[] ddims) {
        //获取图片的三维数组
        float[][][] temp = outArr[0];
        int n = 0;
        //新建一个接收的颜色数组，长度就是图片的宽高之积，类似于上面的像素那个数组
        int[] colorArr = new int[ddims[1] * ddims[2]];
        //for循环遍历把图片的ARGB色值转成一个颜色值，放入颜色数组中
        for (int i = 0; i < ddims[1]; i++) {
            for (int j = 0; j < ddims[2]; j++) {
                float[] arr = temp[i][j];
                int alpha = 255;
                int red = (int) (arr[0] * 255f);
                int green = (int) (arr[1] * 255f);
                int blue = (int) (arr[2] * 255f);
                int tempARGB = (alpha << 24) | (red << 16) | (green << 8) | blue;
                colorArr[n++] = tempARGB;
            }
        }
        //创建bitmap对象
        return Bitmap.createBitmap(colorArr, ddims[1], ddims[2], Bitmap.Config.ARGB_8888);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            //计算图片高度和我们需要高度的最接近比例值
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            //宽度比例值
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            //取比例值中的较大值作为inSampleSize
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Date randomDate(String beginDate, String endDate){
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = format.parse(beginDate);
            Date end = format.parse(endDate);

            if(start.getTime() >= end.getTime()){
                return null;
            }
            long date = random(start.getTime(),end.getTime());
            return new Date(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static long random(long begin,long end){
        long rtn = begin + (long)(Math.random() * (end - begin));
        if(rtn == begin || rtn == end){
            return random(begin,end);
        }
        return rtn;
    }
}
