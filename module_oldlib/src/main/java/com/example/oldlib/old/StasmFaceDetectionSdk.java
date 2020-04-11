package com.example.oldlib.old;

import android.util.SparseArray;

import java.nio.FloatBuffer;

/**
 * @author ruanjiewei
 * @since 2018.04.10
 * 基于Stasm的人脸识别工具类
 */
public class StasmFaceDetectionSdk {

    private static final String TAG = "StasmFaceDetectionSdk";

    /**
     * 重要关键点常量
     */
    public static class LANDMARKS {
        public static final int LEFT_PUPIL = 0;
        public static final int RIGHT_PUPIL = 9;

        public static final int[] LEFT_EYE_BROW_CONTOUR = new int[] {18, 22, 20, 24, 19, 25, 21, 23};
        public static final int[] RIGHT_EYE_BROW_CONTOUR = new int[] {26, 30, 28, 32, 27, 33, 29, 31};

        public static final int NOSE_CENTER = 34;
        public static final int[] NOSE_CONTOUR = new int[] {62, 61, 55, 60, 57, 59, 58, 52, 53, 54};

        public static final int[] LEFT_FACE_CONTOUR = new int[] {68, 69, 70, 71, 72};
        public static final int[] RIGHT_FACE_CONTOUR = new int[] {76, 77, 78, 79, 80};

        public static final int LEFT_EARLOBE = 66;
        public static final int RIGHT_EARLOBE = 74;

        public static final int UP_LIP_INNER_CENTER = 47;
        public static final int DOWN_LIP_INNER_CENTER = 54;
        public static final int UP_LIP_OUTER_CENTER = 46;
        public static final int DOWN_LIP_OUTER_CENTER = 55;

        public static final int[] FACE_CONTOUR = new int[] {
                62, 66, 67, 69, 70, 72,
                64,
                63, 73, 75, 77, 78, 80,
                81, 82, 83, 87, 86, 85, 84};

    }

    public static int[] FACEPP_KEY_POINT_INDEX = {
            // 眼睛
            1, 5, 3, 7, 2, 0, 6, 4, 8,
            10, 14, 12, 16, 11, 9, 15, 13, 17,
            // 眉毛
            18, 22, 24, 19, 23, 25,
            26, 30, 32, 27, 31, 33,
            // 鼻子
            38, 34, 39,
            40, 42, 35, 43, 41,
            42, 34, 43,
            // 嘴巴
            44, 50, 48, 46, 49, 51, 45,
            52, 47, 53,
            56, 54, 57,
            58, 59, 55, 60, 61,
            // 脸部轮廓
            62, 66, 67, 69, 70, 72,
            64,
            63, 73, 75, 77, 78, 80,
            // 脸部轮廓扩展
            88, 90, 91, 93, 94, 95, 97, 98, 99, 101, 102, 104, 106,
            // 额头
            81, 82, 83, 87, 86, 85, 84
    };
    public static int[] STASM_KEY_POINT_INDEX = {
            // 眼睛
            38, 37, 36, 35, 34, 42, 39, 40, 41,
            44, 45, 46, 47, 48, 43, 51, 50, 49,
            // 眉毛
            20, 21, 20, 25, 23, 24,
            26, 27, 28, 29, 31, 30,
            // 鼻子
            54, 53, 52,
            62, 61, 60, 59, 58,
            55, 56, 57,
            // 嘴巴
            63, 64, 65, 66, 67, 68, 69,
            72, 71, 70,
            73, 74, 75,
            80, 79, 78, 77, 76,
            // 脸部轮廓
            0, 1, 2, 3, 4, 5,
            6,
            12, 11, 10, 9, 8, 7,
            // 脸部轮廓扩展
            81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
            // 额头
            19, 18, 17, 16, 15, 14, 13
    };

    // 存放模型文件的目录名字
//    private static final String DATA_DIR_NAME = "detection-models";

    public static final int COUNT_LANDMARKS = 81;

    private static SparseArray<Integer> cache = new SparseArray<>(92);

    /**
     * 特征数据的额外信息位数
     * 0: 当前面部数据的索引
     * 1: 面部以z为轴的偏转弧度
     * 2: face center x
     * 3: face center y
     */
    public static final int COUNT_EXTRA_INFO = 4;
    public static final int LANDMARK_INDEX_TRACK_ID = 0;
    public static final int LANDMARK_INDEX_ROLL = 2;

    public static final int COUNT_EXTENDED_POINTS = 26;
    public static final int COUNT_FULL_POINTS = COUNT_LANDMARKS + COUNT_EXTENDED_POINTS;
    public static final int FULL_COUNT_PER_LANDMARKS = 131*2;
    private static boolean sEnable = true;

    static {
        try {
            System.loadLibrary("stasm-face-detection-jni");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            sEnable = false;
        }
    }

    public static boolean isEnable() {
        return sEnable;
    }

    public static boolean checkMouthOpened(float[] landMarks) {
        double distance = distance(getPointByIndex(landMarks, LANDMARKS.DOWN_LIP_INNER_CENTER),
                                    getPointByIndex(landMarks, LANDMARKS.UP_LIP_INNER_CENTER));
        double distanceOutside = distance(getPointByIndex(landMarks, LANDMARKS.DOWN_LIP_OUTER_CENTER),
                                            getPointByIndex(landMarks, LANDMARKS.UP_LIP_OUTER_CENTER));
        return (distance / distanceOutside) >= 0.30;
    }

    public static float[] merge(float[] array1, float[] array2) {
        float[] result = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    private static FloatBuffer mLandmarksBuffer = FloatBuffer.allocate(StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);

    public static float[] getSingleFaceLandmarks(float[] src, int index) {
        if (src == null || src.length < (index + 1) * FULL_COUNT_PER_LANDMARKS) return null;
        mLandmarksBuffer.clear();
        mLandmarksBuffer.put(src, index * FULL_COUNT_PER_LANDMARKS, FULL_COUNT_PER_LANDMARKS);
        mLandmarksBuffer.position(0);
        return mLandmarksBuffer.array();
    }

    public static float[] getFaceCenter(float[] landmark) {
        // 求人脸的最中心点
        float[] point36 = getPointByIndex(landmark, 36);
        float[] point37 = getPointByIndex(landmark, 37);
        float[] tempCenter = new float[]{(point36[0] + point37[0]) / 2, (point36[1] + point37[1]) / 2}; // 鼻子顶端的中点
        float[] point34 = getPointByIndex(landmark, 34);
        float faceCenterX = (tempCenter[0] + point34[0]) / 2;
        float faceCenterY = (tempCenter[1] + point34[1]) / 2;
        float[] faceCenter = new float[]{faceCenterX, faceCenterY};
        return faceCenter;
    }

    public static float[] resetPointByIndex(float[] points , int index , float[] newPoints){
        if (points == null || points.length <= (COUNT_EXTRA_INFO + index * 2 + 1)) {
            return points;
        }
        points[ index * 2] = newPoints[0];
        points[ index * 2 + 1] = newPoints[1];
        return points;
    }

    public static float[] getPointByIndex(float[] points, int index) {
        if (points == null || points.length <= (COUNT_EXTRA_INFO + index * 2 + 1)) {
            return null;
        }
        return new float[] {points[ index * 2], points[ index * 2 + 1]};
    }

    public static float[] getPointByIndex(float[] points, int index, int offset) {
        if (points == null || points.length < (offset + COUNT_EXTRA_INFO + index * 2 + 1)) {
            return null;
        }
        return new float[] {points[offset + COUNT_EXTRA_INFO + index * 2], points[offset + COUNT_EXTRA_INFO + index * 2 + 1]};
    }



    public static float[] getPointsByIndexes(float[] points, int[] indexes) {
        return getPointsByIndexes(points, indexes, 0);
    }

    public static float[] getPointsByIndexes(float[] points, int[] indexes, int offset) {
        if (points == null) {
            return null;
        }
        float[] result = new float[indexes.length * 2];
        for (int i = 0; i < indexes.length; i++) {
            float[] temp = getPointByIndex(points, indexes[i], offset);
            if (temp != null) {
                result[i * 2] = temp[0];
                result[i * 2 + 1] = temp[1];
            }
        }
        return result;
    }

    public static float distance(float[] p1, float[] p2) {
        return (float) Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
    }

    public static float[] center(float[] p1, float[] p2) {
        float[] result = new float[2];
        result[0] = (p1[0] + p2[0]) / 2.0f;
        result[1] = (p1[1] + p2[1]) / 2.0f;
        return result;
    }

    /**
     * 旧关键点映射到新关键点
     * @return
     */
    public static int map(int stasmKeyPoint) {
        if (cache.indexOfKey(stasmKeyPoint) >= 0) {
            return cache.get(stasmKeyPoint);
        }
        for (int i = 0; i < STASM_KEY_POINT_INDEX.length; i++) {
            if (STASM_KEY_POINT_INDEX[i] == stasmKeyPoint) {
                cache.put(stasmKeyPoint, FACEPP_KEY_POINT_INDEX[i]);
                return FACEPP_KEY_POINT_INDEX[i];
            }
        }
        try {
            throw new Exception("no point map!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stasmKeyPoint;
    }

}
