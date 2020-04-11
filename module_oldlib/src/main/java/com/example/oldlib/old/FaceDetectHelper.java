package com.example.oldlib.old;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.picstudio.photoeditorplus.stasm.StasmFaceDetectionSdk;
import com.picstudio.photoeditorplus.stasm.Utils;

/**
 * 生成GL绘制坐标顺序的帮助类
 * 输入：标准人脸图
 * 输出：
 *      face_triangles_area_no_numbers_extended.jpg ：三角剖分人脸图
 *      triangles.txt ：纹理坐标
 *      texture_position.txt ：顶点坐标
 */

public class FaceDetectHelper {
    private static final String TAG = "MainActivity";

    private Paint mPaint;
    private Paint mCenterPaint;
    private Paint mTextPaint;
    private Paint mLinePaint;
//    private int[] sPictures = new int[] {R.drawable.test_people_face};
    private Random mRandom = new Random();

    public FaceDetectHelper() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLUE);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeWidth(1);

        mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCenterPaint.setColor(Color.GREEN);
        mCenterPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(8 * 3);
        mTextPaint.setColor(Color.GREEN);
    }

    public void doFacesDetect(Context context/*, View view*/, Bitmap bitmap , float[]landmarks ) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inMutable = true;
//        int resId = sPictures[mRandom.nextInt(sPictures.length)];
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
//        byte[] bitmapData = Utils.bitmapToByteArray(bitmap);
        long time = System.currentTimeMillis();

//        FaceppHolder.setFaceConfigLocked(bitmap.getWidth(), bitmap.getHeight(), 0, false);
//        float[] landmarks = FaceppHolder.detect(bitmapData, bitmap.getWidth(), bitmap.getHeight(), Facepp.IMAGEMODE_RGBA, false, 0, 0, false, false);
//        RotatedRectF rotatedRectF = StasmFaceDetectionSdk.getMinFaceAreaRect(StasmFaceDetectionSdk.getSingleFaceLandmarks(landmarks, 0));

        // 所有的扩展点
        StringBuilder stringBuilder = new StringBuilder();

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        for (int i = 0; i < landmarks.length/2; i+=2) {
            stringBuilder.append(landmarks[i] / imageWidth);
            stringBuilder.append("f, ");
            stringBuilder.append(landmarks[i + 1] / imageHeight);
            stringBuilder.append("f, ");
        }

//        for (int i = 0; i < StasmFaceDetectionSdk.COUNT_EXTENDED_POINTS; i++) {
//            stringBuilder.append(extendedPoints[i * 2] / imageWidth);
//            stringBuilder.append("f, ");
//            stringBuilder.append(extendedPoints[i * 2 + 1] / imageHeight);
//            stringBuilder.append("f, ");
//        }
        com.picstudio.photoeditorplus.stasm.Utils.saveStringToFile(stringBuilder.toString(), new File(context.getFilesDir(), "texture_position.txt").getPath()); // 纹理坐标

        Map<PointF, Integer> landmark_index_map = new HashMap<>();
        for (int i = 0; i < landmarks.length/2; i++) {
            landmark_index_map.put(new PointF(landmarks[ i * 2], landmarks[1 + i * 2]), i);
        }

        long endTime = System.currentTimeMillis() - time;
        Log.d(TAG, "faces detect cost = " + endTime);
        if (landmarks != null) {
            int faceCount = 1;
            Log.d(TAG, "face detected " + faceCount + " face(s) cost = " + endTime);

            Canvas canvas = new Canvas(bitmap);
            // 绘制三角剖分
            RectF bitmapRect = new RectF(0 , 0, bitmap.getWidth(), bitmap.getHeight());
            StringBuilder triangleString = new StringBuilder();
            int count = 0;
            List<PointF[]> triangles = StasmFaceDetectionSdk.getTriangleList(bitmap.getWidth(), bitmap.getHeight(), landmarks, false);
            for (PointF[] triangle : triangles) {
                PointF A = triangle[0];
                PointF B = triangle[1];
                PointF C = triangle[2];
                if (bitmapRect.contains(A.x, A.y) && bitmapRect.contains(B.x, B.y) && bitmapRect.contains(C.x, C.y)) {
                    canvas.drawLine(A.x, A.y, B.x, B.y, mLinePaint);
                    canvas.drawLine(B.x, B.y, C.x, C.y, mLinePaint);
                    canvas.drawLine(C.x, C.y, A.x, A.y, mLinePaint);
                    triangleString.append(landmark_index_map.get(A));
                    triangleString.append(", ");
                    triangleString.append(landmark_index_map.get(B));
                    triangleString.append(", ");
                    triangleString.append(landmark_index_map.get(C));
                    triangleString.append(", ");
                    count++;
                    if (count % 5 == 0) {
                        triangleString.append("\n");
                    }
                }
            }
            Utils.saveStringToFile(triangleString.toString(), new File(context.getFilesDir(), "triangles.txt").getPath());

            int offset;
            for (int i = 0; i < faceCount; i++) {
                offset = 0;
//                float center[] = FaceppHolder.getFaceCenter(landmarks);
//                canvas.drawCircle(center[0], center[1], 4, mCenterPaint);
                for (int j = 0; j < landmarks.length/2; j++) {
                    canvas.drawCircle(landmarks[offset + j * 2], landmarks[offset + j * 2 + 1], 3, mPaint);
                    canvas.drawText(String.valueOf(j), landmarks[offset + j * 2], landmarks[offset + j * 2 + 1], mTextPaint);
                }

//                // 绘制扩展点
//                float[] extendedPoints2 =landmarks;
//                int extendedPointSize = extendedPoints2.length / 2;
//                for (int j = 0; j < extendedPointSize; j++) {
//                    canvas.drawCircle(extendedPoints2[j * 2], extendedPoints2[j * 2 + 1], 3, mPaint);
//                    canvas.drawText(String.valueOf(StasmFaceDetectionSdk.COUNT_LANDMARKS + j), extendedPoints2[j * 2], extendedPoints2[j * 2 + 1], mTextPaint);
//                }
            }

//            PointF[] minArea = new PointF[4];
//            rotatedRectF.points(minArea);
//            canvas.drawLine(minArea[0].x, minArea[0].y, minArea[1].x, minArea[1].y, mLinePaint);
//            canvas.drawLine(minArea[1].x, minArea[1].y, minArea[2].x, minArea[2].y, mLinePaint);
//            canvas.drawLine(minArea[2].x, minArea[2].y, minArea[3].x, minArea[3].y, mLinePaint);
//            canvas.drawLine(minArea[3].x, minArea[3].y, minArea[0].x, minArea[0].y, mLinePaint);
            Utils.saveBitmapToFile(bitmap, new File(context.getFilesDir(), "face_triangles_area_no_numbers_extended.jpg").getPath());
        } else {
            Log.e(TAG, "face detect fail!");
        }
    }
}
