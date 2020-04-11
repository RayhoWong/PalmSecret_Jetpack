package com.picstudio.photoeditorplus.stasm;

public class MlsUtils {

    static {
        System.loadLibrary("mls-jni");
    }

    /**
     * 使用刚性变换的移动最小二乘图像变形算法
     * @param srcGridPoints 原始网格点，数据是从左到右从上到下排列
     * @param srcControlPoints 控制点
     * @param targetControlPoints 目标控制点
     * @return 变形后的网格点
     */
    public static native float[] mlsWithRigid(int cacheKey, float[] srcGridPoints, float[] srcControlPoints, float[] targetControlPoints, boolean isRigid);

    /**
     * 清空mls算法相关计算缓存
     */
    public static native void clearMlsTempCache();
}
