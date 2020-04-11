package com.example.oldlib.hair;

import android.graphics.Bitmap;
import android.graphics.Color;


public class MinRectHelper {
    private static final String TAG = "MinRectHelper";
//    public static List<Point> getContourPoint(Bitmap srcBmp) {
//        Mat src = new Mat();
//        Utils.bitmapToMat(srcBmp, src);
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); // 检测轮廓
//        List<Point> points = new ArrayList<>();
//        for (MatOfPoint mp : contours) {
//            Collections.addAll(points, mp.toArray());
//        }
//        return points;
//    }
//
//    public static RotatedRect getMaskMinRect(Bitmap srcBmp) {
//        Mat src = new Mat();
//        Utils.bitmapToMat(srcBmp, src);
//        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2GRAY);
//        List<MatOfPoint> contours = new ArrayList<>();
//        Imgproc.findContours(src, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE); // 检测轮廓
//        List<Point> points = new ArrayList<>();
//        MatOfPoint2f matOfPoints = new MatOfPoint2f();
//        for (MatOfPoint mp : contours) {
//            Collections.addAll(points, mp.toArray());
//        }
//        matOfPoints.fromList(points);
//        // 检测最小矩形
//        try {
//            return Imgproc.minAreaRect(matOfPoints);
//        } catch (Exception e) {
//            e.printStackTrace();
//            float w = srcBmp.getWidth();
//            float h = srcBmp.getHeight();
//            return new RotatedRect(new Point(w / 2f, h / 2f), new Size(w, h), 0d);
//        }
//    }
//
//    public static RotatedRect getPointsMinRect(float[] landmarks, int[] indexes) {
//        MatOfPoint2f matOfPoints = new MatOfPoint2f();
//        List<Point> pointList = StasmFaceDetectionSdk.getPointListByIndexes(landmarks, indexes);
//        matOfPoints.fromList(pointList);
//        RotatedRect minRect = Imgproc.minAreaRect(matOfPoints);
//        return minRect;
//    }


    /**
     * 去除透明
     * @param originBitmap
     * @return
     */
    public static Bitmap deleteAlphaSpace(Bitmap originBitmap) {
        long start = System.currentTimeMillis();
        int bitmapHight = originBitmap.getHeight();
        int bitmapWidth = originBitmap.getWidth();
        int top = 0;
        int left = 0;
        int right = 0;
        int bottom = 0;
        for (int h = 0; h < bitmapHight; h++) {
            boolean holdBlackPix = false;
            for (int w = 0; w < bitmapWidth; w = w + 3) {
                int alpha = Color.alpha(originBitmap.getPixel(w, h));
                if (alpha != 0) {
                    holdBlackPix = true;
                    break;
                }
            }
            if (holdBlackPix) {
                break;
            }
            top++;
        }

        for (int w = 0; w < bitmapWidth; w++) {
            boolean holdBlackPix = false;
            for (int h = 0; h < bitmapHight; h = h + 3) {
                int alpha = Color.alpha(originBitmap.getPixel(w, h));
                if (alpha != 0) {
                    holdBlackPix = true;
                    break;
                }
            }
            if (holdBlackPix) {
                break;
            }
            left++;
        }

        for (int w = bitmapWidth - 1; w >= 0; w--) {
            boolean holdBlackPix = false;
            for (int h = 0; h < bitmapHight; h = h + 3) {
                int alpha = Color.alpha(originBitmap.getPixel(w, h));
                if (alpha != 0) {
                    holdBlackPix = true;
                    break;
                }
            }
            if (holdBlackPix) {
                break;
            }
            right++;
        }

        for (int h = bitmapHight - 1; h >= 0; h--) {
            boolean holdBlackPix = false;
            for (int w = 0; w < bitmapWidth; w = w + 3) {
                int alpha = Color.alpha(originBitmap.getPixel(w, h));
                if (alpha != 0) {
                    holdBlackPix = true;
                    break;
                }
            }
            if (holdBlackPix) {
                break;
            }
            bottom++;
        }

        int cropHeight = originBitmap.getHeight() - bottom - top;
        int cropWidth = originBitmap.getWidth() - left - right;

//        if(Log.isD()){
//            Log.e(TAG, "裁剪矩形时间3:" + (System.currentTimeMillis() - start));
//        }
        return Bitmap.createBitmap(originBitmap, left, top, cropWidth, cropHeight);
    }
}

