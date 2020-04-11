package com.picstudio.photoeditorplus.stasm;

import android.graphics.PointF;
import android.util.SparseArray;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

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
    public static final int FULL_COUNT_PER_LANDMARKS = COUNT_LANDMARKS * 2 + COUNT_EXTRA_INFO + COUNT_EXTENDED_POINTS * 2;
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

//    public static void initStasm(Context context) {
//        if (sEnable) { // 首先保证能够成功加载so库
//            File dataDir = Utils.getDirFile(context, DATA_DIR_NAME);
//            if (!dataDir.exists()) {
//                dataDir.mkdirs();
//            }
//            boolean modelFile1Enable = !TextUtils.isEmpty(Utils.tryExportResource(context, R.raw.haarcascade_frontalface_alt2, DATA_DIR_NAME));
//            boolean modelFile2Enable = !TextUtils.isEmpty(Utils.tryExportResource(context, R.raw.haarcascade_mcs_lefteye, DATA_DIR_NAME));
//            boolean modelFile3Enable = !TextUtils.isEmpty(Utils.tryExportResource(context, R.raw.haarcascade_mcs_righteye, DATA_DIR_NAME));
//            boolean modelFile4Enable = !TextUtils.isEmpty(Utils.tryExportResource(context, R.raw.haarcascade_mcs_mouth, DATA_DIR_NAME));
//            sEnable = modelFile1Enable && modelFile2Enable && modelFile3Enable && modelFile4Enable && nativeInitStasm(dataDir.getAbsolutePath());
//            Log.e(TAG, "check model file status: " + modelFile1Enable + " " + modelFile2Enable + " " + modelFile3Enable + " " + modelFile4Enable);
//            Log.e(TAG, "nativeInitStasm status: " + sEnable);
//        } else {
//            Log.e(TAG, "native libraries haven't been loaded!");
//        }
//    }

//    public static float[] facesDetect(int width, int height, byte[] imageData) {
//        if (!sEnable) {
//            Log.e(TAG, "StasmFaceDetection not enable yet! Had called initStasm at first?");
//            return null;
//        }
//        float[] result = null;
//        if (imageData != null && width * height * 4 == imageData.length) {
//            result = nativeFacesDetect(width, height, imageData);
//        }
//        return result;
//    }

    public static List<PointF[]> getTriangleList(int width, int height, float[] points, boolean extended) {
        float[] triangleData;
        if (extended) {
            triangleData = nativeGetTriangleList(width, height, points, FULL_COUNT_PER_LANDMARKS + COUNT_EXTENDED_POINTS * 2, COUNT_EXTRA_INFO);
        } else {
            triangleData = nativeGetTriangleList(width, height, points, 50*2, 0);
        }
        int triangleSize = triangleData.length / 6;
        List<PointF[]> result = new ArrayList<>(triangleSize);
        for (int i = 0; i < triangleSize; i++) {
            PointF[] triangle = new PointF[3];
            triangle[0] = new PointF(triangleData[i * 6], triangleData[i * 6 + 1]);
            triangle[1] = new PointF(triangleData[i * 6 + 2], triangleData[i * 6 + 3]);
            triangle[2] = new PointF(triangleData[i * 6 + 4], triangleData[i * 6 + 5]);
            result.add(triangle);
        }
        return result;
    }

//    public static float[] extendHalfFaceLandmarks(float[] points, int offset) {
//        float[] operationPoints  = Arrays.copyOfRange(points, offset, offset + FULL_COUNT_PER_LANDMARKS);
//        Matrix matrix = new Matrix();
//        float centerX = operationPoints[StasmFaceDetectionSdk.LANDMARK_INDEX_CENTER_X];
//        float centerY = operationPoints[StasmFaceDetectionSdk.LANDMARK_INDEX_CENTER_Y];
//        matrix.postScale(1.4f, 1.4f, centerX, centerY);
//        float[] data = getPointsByIndexes(operationPoints, LANDMARKS.EXTRA_FACE_CONTOUR_REFERENCE);
//        matrix.mapPoints(data);
//        return data;
//    }

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
        points[COUNT_EXTRA_INFO + index * 2] = newPoints[0];
        points[COUNT_EXTRA_INFO + index * 2 + 1] = newPoints[1];
        return points;
    }

    public static float[] getPointByIndex(float[] points, int index) {
        if (points == null || points.length <= (COUNT_EXTRA_INFO + index * 2 + 1)) {
            return null;
        }
        return new float[] {points[COUNT_EXTRA_INFO + index * 2], points[COUNT_EXTRA_INFO + index * 2 + 1]};
    }

    public static float[] getPointByIndex(float[] points, int index, int offset) {
        if (points == null || points.length < (offset + COUNT_EXTRA_INFO + index * 2 + 1)) {
            return null;
        }
        return new float[] {points[offset + COUNT_EXTRA_INFO + index * 2], points[offset + COUNT_EXTRA_INFO + index * 2 + 1]};
    }


    public static List<Point> getPointListByIndexes(float[] points, int[] indexes) {
        return getPointListByIndexes(points, indexes, 0);
    }

    public static List<Point> getPointListByIndexes(float[] points, int[] indexes, int offset) {
        if (points == null) {
            return null;
        }
        List<Point> result = new ArrayList<>(indexes.length);
        for (int index : indexes) {
            float[] temp = getPointByIndex(points, index, offset);
            if (temp != null) {
                result.add(new Point(temp[0], temp[1]));
            }
        }
        return result;
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

    public static Rect getBoundingRect(float[] points) {
        MatOfPoint pointsMat = new MatOfPoint();
        List<Point> pointList = new ArrayList<>(COUNT_LANDMARKS);
        for (int i = 0; i < COUNT_LANDMARKS; i++) {
            float[] data = getPointByIndex(points, i);
            pointList.add(new Point(data[0], data[1]));
        }
        pointsMat.fromList(pointList);
        return Imgproc.boundingRect(pointsMat);
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

//    private native static boolean nativeInitStasm(String classifierDir);

//    private native static float[] nativeFacesDetect(int width, int height, byte[] imageData);

    public native static float[] nativeGetTriangleList(int width, int height, float[] points, int length, int offset);

    private native static float[] nativeGetMinAreaRect(float[] points, int[] indexes);

    public native static void loadTexture(long imageDataAddr, int width, int height, int textureId);
}
