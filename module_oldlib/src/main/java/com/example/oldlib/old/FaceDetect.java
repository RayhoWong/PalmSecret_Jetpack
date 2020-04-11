package com.example.oldlib.old;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import com.example.oldlib.R;
import com.example.oldlib.hair.AutoHairHelper;
import com.example.oldlib.hair.GPUImageFilterGroup;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class FaceDetect {
    public static final String TAG = "FaceDetect";

    public static final int OLD_AGE_50 = 50;
    public static final int OLD_AGE_70 = 70;
    public static final int OLD_AGE_90 = 90;

    public static void init(Context context) {
        FirebaseApp.initializeApp(context);

    }

    public static void faceDetectToContourPoint(final Bitmap bitmap, final OnSuccessListener<List<FirebaseVisionFace>> onSuccessListener, final OnFailureListener onFailureListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                                .build();

                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(highAccuracyOpts);

                detector.detectInImage(image)
                        .addOnSuccessListener(
                                onSuccessListener).addOnFailureListener(onFailureListener);


            }
        }).start();
    }

    public static void faceDetectToContourPoint(final Bitmap bitmap, final IContourPointListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                                .build();

                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(highAccuracyOpts);

                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...
                                        float[] aFloat = getFloat(faces);
                                        if (aFloat != null) {
                                            Log.e(TAG, "人脸识别成功");
                                            listener.success(aFloat);
                                        } else {
                                            listener.onFailure();
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        listener.onFailure();
                                        Log.e(TAG, "人脸识别失败----" + e.toString());
                                    }
                                });


            }
        }).start();

    }
    public static void faceDetectToContourPoint(final Bitmap bitmap, final IFacePointListener listener) {

        new Thread( new Runnable() {
            @Override
            public void run() {
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

                FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                                .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                                .build();

                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(highAccuracyOpts);

                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...
                                        List<List<PointF>> aFloat = getFloatList(faces);
                                        if (!aFloat.isEmpty() && aFloat.size() == 13) {
                                            Log.e(TAG, "人脸识别成功");
                                            listener.onSuccess(aFloat);
                                        } else {
                                            listener.onFailure();
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                        listener.onFailure();
                                        Log.e(TAG, "人脸识别失败----" + e.toString());
                                    }
                                });


            }
        }).start();

    }

    //人脸检测并进行变老
    public static void faceDetectToOld(final Application application, final float[] contourPoint, final Bitmap bitmap, final List<Integer> ages, final IAgeingListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<AgeBean> mData = new ArrayList<>();
                for (int age : ages) {
                    if (age == OLD_AGE_50) {
                        mData.add(new AgeBean(R.drawable.year_old_50, 50, R.drawable.age_texture_50, null, 0.4f, 0.3f));
                    } else if (age == OLD_AGE_70) {
                        mData.add(new AgeBean(R.drawable.year_old_70, 70, R.drawable.age_texture_70, null, 0.7f, 0.5f));
                    } else if (age == OLD_AGE_90) {
                        mData.add(new AgeBean(R.drawable.year_old_90, 90, R.drawable.age_texture_90, null, 1f, 0.5f));

                    }
                }


                try {

                    List<Bitmap> resultList = new ArrayList<>(mData.size());
                    for (AgeBean ageBean : mData) {
                        AutoHairHelper hairHelper = new AutoHairHelper(application);
                        Bitmap changeAge = changeAge(application, contourPoint, bitmap, ageBean);
                        Bitmap result;
                        if (ageBean.getAge() == OLD_AGE_50) {
                            result = changeAge;
                        } else {
                            result = hairHelper.getResultBitmap(application, bitmap, changeAge, null);
                        }
                        resultList.add(result);
                    }
                    listener.success(resultList);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure();
                }

            }
        }).start();

    }


    public static float[] getFloat(List<FirebaseVisionFace> faces) {
        float[] points = null;

        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            FirebaseVisionFaceContour contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS);
            points = new float[contour.getPoints().size() * 2];
            int index = 0;

            int[] pointType = new int[]{FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP, FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM,
                    FirebaseVisionFaceContour.LEFT_EYEBROW_TOP, FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM,
                    FirebaseVisionFaceContour.RIGHT_EYE, FirebaseVisionFaceContour.LEFT_EYE,
                    FirebaseVisionFaceContour.NOSE_BRIDGE, FirebaseVisionFaceContour.NOSE_BOTTOM,
                    FirebaseVisionFaceContour.UPPER_LIP_TOP, FirebaseVisionFaceContour.UPPER_LIP_BOTTOM,
                    FirebaseVisionFaceContour.LOWER_LIP_TOP, FirebaseVisionFaceContour.LOWER_LIP_BOTTOM,
                    FirebaseVisionFaceContour.FACE
            };

            for (int i = 0; i < pointType.length; i++) {
                contour = face.getContour(pointType[i]);
                for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
                    float px = point.getX();
                    points[index] = px;
                    index++;
                    float py = point.getY();
                    points[index] = py;
                    index++;
                }
            }
        }

        return points;


//        FaceDetectHelper d = new FaceDetectHelper();
//        d.doFacesDetect(MyApplication.getApplication() ,bitmap , points);
//        ageingFilter(points, bitmap, listener , type);
    }
    public static List<List<PointF>> getFloatList(List<FirebaseVisionFace> faces) {
//        float[] points = null;
        List<List<PointF>> points = new ArrayList<>();
        for (FirebaseVisionFace face : faces) {
            Rect bounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees

            FirebaseVisionFaceContour contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS);
//            int index = 0;

            int[] pointType = new int[]{FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP, FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM,
                    FirebaseVisionFaceContour.LEFT_EYEBROW_TOP, FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM,
                    FirebaseVisionFaceContour.RIGHT_EYE, FirebaseVisionFaceContour.LEFT_EYE,
                    FirebaseVisionFaceContour.NOSE_BRIDGE, FirebaseVisionFaceContour.NOSE_BOTTOM,
                    FirebaseVisionFaceContour.UPPER_LIP_TOP, FirebaseVisionFaceContour.UPPER_LIP_BOTTOM,
                    FirebaseVisionFaceContour.LOWER_LIP_TOP, FirebaseVisionFaceContour.LOWER_LIP_BOTTOM,
                    FirebaseVisionFaceContour.FACE
            };

            for (int i = 0; i < pointType.length; i++) {
                contour = face.getContour(pointType[i]);
                List<PointF> pointsItem = new ArrayList<>();
                for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : contour.getPoints()) {
                    float px = point.getX();
//                    points[index] = px;
//                    index++;
                    float py = point.getY();
//                    points[index] = py;
//                    index++;
                    PointF pointF = new PointF(px, py);
                    pointsItem.add(pointF);
                }
                points.add(pointsItem);
            }
        }

        return points;


//        FaceDetectHelper d = new FaceDetectHelper();
//        d.doFacesDetect(MyApplication.getApplication() ,bitmap , points);
//        ageingFilter(points, bitmap, listener , type);
    }


    /**
     * 优化方法
     *
     * @param landmarks
     * @param srcBitmap
     * @param ageBean
     */
    public static Bitmap changeAge(Application application, float[] landmarks, Bitmap srcBitmap, AgeBean ageBean) {
//        setPantingEnable(false);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        filterGroup.addFilter(new GPUImageFilter());
        GPUImageAgeingFilter ageingFilter = new GPUImageAgeingFilter(landmarks);
        ageingFilter.setAgeIntensity(ageBean.getAgeIntensity());

        ageingFilter.setBitmap(BitmapFactory.decodeResource(application.getResources(), ageBean.getAgeingTextureId(), options));
        filterGroup.addFilter(ageingFilter);

//       GPUImageAgeingBeardFilter ageingFilter1 = new GPUImageAgeingBeardFilter(landmarks);
//       ageingFilter1.setBitmap(BitmapFactory.decodeResource(MyApplication.getApplication().getResources(), R.drawable.ageing_beard_female));
//       filterGroup.addFilter(ageingFilter1);

//       FaceDetectHelper detectHelper = new FaceDetectHelper();
//       float[] dd = FaceCheckRender.extendLandMarkWithCheck(landmarks);
//       detectHelper.doFacesDetect(MyApplication.getApplication() ,srcBitmap , dd );
        Bitmap bitmap = getBitmapForFilter(srcBitmap, filterGroup);

        EyeLipMorphFilter ageingFilter3 = new EyeLipMorphFilter(landmarks, ageBean.getAge(), 0, 0);
        filterGroup = new GPUImageFilterGroup();
        filterGroup.addFilter(ageingFilter3);
        bitmap = getBitmapForFilter(bitmap, filterGroup);

        GLAgeingFaceMorphFilter ageingFilter2 = new GLAgeingFaceMorphFilter(landmarks, ageBean.getAge());
        filterGroup = new GPUImageFilterGroup();
        filterGroup.addFilter(ageingFilter2);
        bitmap = getBitmapForFilter(bitmap, filterGroup);


//       BeardColorFilter beardColorFilter = new BeardColorFilter(landmarks , 50);
//       filterGroup = new GPUImageFilterGroup();
//       filterGroup.addFilter(beardColorFilter);
//       Bitmap bitmap1 = getBitmapForFilter(srcBitmap , filterGroup);
//       AutoHairHelper hairHelper = new AutoHairHelper();
//       Bitmap bitmap2 = hairHelper.getResultBitmap(srcBitmap , bitmap1);

        return bitmap;


//
//        updateSrcBitmap(bitmap);
//
//
//        filterGroup = new GPUImageFilterGroup();
//        List<GPUImageFilter> filterList = mAgeBean.getHairBean().getFilterGroup().getFilters();
//        for (GPUImageFilter filter : filterList) {
//            if (filter instanceof GPUImageHairTextureFilter) {
//                ((GPUImageHairTextureFilter) filter).setVertexCoor(mHairVertexCoor);
//                ((GPUImageHairTextureFilter) filter).setTextureCoor(mHairTextureCoor);
//
//            }
//            filterGroup.addFilter(filter);
//        }
//        GPUImageIntensityBlendFilter intensityBlendFilter = new GPUImageIntensityBlendFilter();
//        intensityBlendFilter.setBitmap(mHighQualityOriginalBitmap);
//        intensityBlendFilter.setIntensity(mAgeBean.getHairIntensity());
//        filterGroup.addFilter(intensityBlendFilter);
//        mFilterBitmap = GPUImage.getBitmapForFilter(mHighQualityOriginalBitmap, filterGroup);
////        setPantingEnable(true);
//        showRateGuide();
//        invalidate();
    }


    public static void ageingFilter(Application application, float[] landmarks, Bitmap srcBitmap, IAgeingListener listener, AgeBean type) {
        RTGPUImageAgeingFilter filter = new RTGPUImageAgeingFilter();
        if (type.getAge() == 50) {
            filter.setAgeIntensity(RTGPUImageAgeingFilter.AGE_50);
        } else if (type.getAge() == 70) {
            filter.setAgeIntensity(RTGPUImageAgeingFilter.AGE_70);
        } else if (type.getAge() == 90) {
            filter.setAgeIntensity(RTGPUImageAgeingFilter.AGE_90);
        }
        filter.setLandmarks(landmarks);
        //皱纹图
        filter.setBitmap(BitmapFactory.decodeResource(application.getResources(), R.drawable.age_texture));


        Bitmap bitmap = getBitmapForFilter(srcBitmap, filter);

        if (bitmap != null) {
            ArrayList<Bitmap> bitmaps = new ArrayList<>();
            bitmaps.add(bitmap);
            listener.success(bitmaps);
            Log.e(TAG, "变老成功");
        } else {
            listener.onFailure();
        }
    }


    public static Bitmap getBitmapForFilter(final Bitmap bitmap, final GPUImageFilter filter) {
        GPUImageRenderer renderer = null;
        PixelBuffer buffer = null;
        try {
            renderer = new GPUImageRenderer(filter, null, false);
            renderer.setImageBitmap(bitmap, false);
            buffer = new PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
            if (filter == null) {
                return bitmap;
            }
            buffer.setRenderer(renderer);
            return buffer.getBitmap();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            filter.destroy();
            renderer.deleteImage();
            buffer.destroy();
        }
        return bitmap;
    }


    public static interface IAgeingListener {
        public void success(List<Bitmap> bitmaps);

        public void onFailure();
    }

    public interface IContourPointListener {
        void success(float[] contourPoints);

        void onFailure();
    }

    public interface IFacePointListener {
        void onSuccess(List<List<PointF>> contourPoints);

        void onFailure();
    }


}
