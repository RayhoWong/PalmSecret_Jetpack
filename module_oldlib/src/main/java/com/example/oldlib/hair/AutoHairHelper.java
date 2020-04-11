package com.example.oldlib.hair;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.widget.ImageView;


import com.example.oldlib.R;
import com.example.oldlib.old.FaceDetect;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class AutoHairHelper {
    private static final String TAG = "AutoHairHelper";
    private static final int TextureId = R.drawable.hair_texture;


    private static final int INPUT_SIZE = 112; // 224 448  672 896 1120
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private static final String MODEL_FILE = "pb/converted_model_v2_1.tflite";

    private Interpreter tflite;
    private Interpreter.Options tfliteOptions;
    protected ByteBuffer imgData = null;
    private float[][][] outputArray = null;

    private float[] mHairTextureCoor;
    private float[] mHairVertexCoor;

    private RectF mHairMinRect;

    public AutoHairHelper() {
    }

    public AutoHairHelper(Context context) {
        imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * INPUT_SIZE * INPUT_SIZE * DIM_PIXEL_SIZE * 4);
        imgData.order(ByteOrder.nativeOrder());

        tfliteOptions = new Interpreter.Options();
//        tfliteOptions.setAllowFp16PrecisionForFp32(true);
//        tfliteOptions.setUseNNAPI(false);
//        tfliteOptions.setNumThreads(1);
        long time = System.currentTimeMillis();
        tflite = new Interpreter(loadModelFile(context), tfliteOptions);
        Log.d(TAG, "AutoHairHelper init : " + (System.currentTimeMillis() - time));
    }

    protected void addPixelValue(int pixelValue) {
        imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
        imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
    }

    private MappedByteBuffer loadModelFile(Context context) {
        try {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getMask(Bitmap src) {
        int srcW = src.getWidth() / 2;
        int srcH = src.getHeight() / 2;
        Bitmap bitmap = Bitmap.createScaledBitmap(src, INPUT_SIZE, INPUT_SIZE, false);
        int bW = bitmap.getWidth();
        int bH = bitmap.getHeight();
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];

        imgData.rewind();
        bitmap.getPixels(intValues, 0, bW, 0, 0, bW, bH);
        int pixel = 0, val;
        for (int i = 0; i < INPUT_SIZE; ++i) {
            for (int j = 0; j < INPUT_SIZE; ++j) {
                val = intValues[pixel++];
                addPixelValue(val);
            }
        }

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        outputArray = new float[INPUT_SIZE][INPUT_SIZE][1];
        long time = System.currentTimeMillis();
        tflite.run(imgData, outputArray);
        Log.d(TAG, "tflite run cost: " + (System.currentTimeMillis() - time));
        tflite.close();

        for (int i = 0; i < outputArray.length; i++) {
            for (int j = 0; j < outputArray.length; j++) {
                float tmp = outputArray[i][j][0];
                intValues[i * INPUT_SIZE + j] =
                        0xFF000000
                                | (((int) (tmp * 255f)) << 16)
                                | (((int) (tmp * 255f)) << 8)
                                | ((int) (tmp * 255f));
            }
        }

        imgData.clear();
        outputArray = null;

        Bitmap mask = Bitmap.createBitmap(intValues, bW, bH, Bitmap.Config.ARGB_8888);
        return Bitmap.createScaledBitmap(mask, srcW, srcH, true);
    }

    public Bitmap getResultBitmap(Application application, Bitmap srcBitmap, Bitmap oldBitmap, ImageView imageView) {
//        Bitmap src = BitmapFactory.decodeFile(path);
        Bitmap mask = getMask(srcBitmap);

        Bitmap transparentHair = getHollowBitmap(oldBitmap, mask);

        if (mask != null && !mask.isRecycled()) {
            mask.recycle();
        }
//        setmHairMinRect(MinRectHelper.getMaskMinRect(mask), imageView);
//        MinRectHelper.deleteAlphaSpace(mask);


        Bitmap result = getBottomBitmap(application, srcBitmap);

        Canvas canvas = new Canvas(result);
        float scaleX = ((float) result.getWidth()) / ((float) transparentHair.getWidth());
        float scaleY = ((float) result.getHeight()) / ((float) transparentHair.getHeight());
        float scale = Math.max(scaleX, scaleY);
        Matrix matrix2 = new Matrix();
        matrix2.setScale(scale, scale);
        canvas.drawBitmap(transparentHair, matrix2, new Paint());


        if (transparentHair != null && !transparentHair.isRecycled()) {
            transparentHair.recycle();
        }

        if (oldBitmap != null && !oldBitmap.isRecycled()) {
            oldBitmap.recycle();
        }

        canvas = null;

        return result;
    }

    public Bitmap getResultBitmap(Application application, Bitmap oldBitmap, Bitmap mask) {

        Bitmap transparentHair = getHollowBitmap(oldBitmap, mask);

        if (mask != null && !mask.isRecycled()) {
            mask.recycle();
        }


        Bitmap bmpGrayscale = Bitmap.createBitmap(oldBitmap.getWidth(), oldBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(oldBitmap, 0, 0, paint);

        Bitmap result = getBottomBitmap(application, oldBitmap);

        Canvas canvas = new Canvas(result);
        float scaleX = ((float) result.getWidth()) / ((float) transparentHair.getWidth());
        float scaleY = ((float) result.getHeight()) / ((float) transparentHair.getHeight());
        float scale = Math.max(scaleX, scaleY);
        Matrix matrix2 = new Matrix();
        matrix2.setScale(scale, scale);
        canvas.drawBitmap(transparentHair, matrix2, new Paint());


        if (transparentHair != null && !transparentHair.isRecycled()) {
            transparentHair.recycle();
        }

        canvas = null;

        return result;
    }

    public Bitmap getHollowBitmap(Bitmap src, Bitmap mask) {
        int dstW = (int) (src.getWidth() / 2f);
        int dstH = (int) (src.getHeight() / 2f);
        mask = Bitmap.createScaledBitmap(mask, dstW, dstH, true);
        HairColorFilter hairColorFilter = new HairColorFilter();
        hairColorFilter.setBlendColor(Color.parseColor("#FF0998"));
        hairColorFilter.setMaskBitmap(mask);
        Bitmap halfSrc = Bitmap.createScaledBitmap(src, dstW, dstH, true);
        return FaceDetect.getBitmapForFilter(halfSrc, hairColorFilter);
    }


    public Bitmap getBottomBitmap(Application application, Bitmap src) {
        setupHairCoor(src);
        Bitmap mFilterBitmap;

        GPUImageHueBlendFilter filter1 = new GPUImageHueBlendFilter();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        filter1.setBitmap(BitmapFactory.decodeResource(application.getResources(), TextureId, options));

        GPUImageHairTextureFilter filter2 = new GPUImageHairTextureFilter(mHairTextureCoor, mHairVertexCoor);
        filter2.setBitmap(BitmapFactory.decodeResource(application.getResources(), TextureId, options));
//        filter2.setVertexCoor(mHairVertexCoor);
//        filter2.setTextureCoor(mHairTextureCoor);

        GPUImageLookupFilter filter3 = new GPUImageLookupFilter();
        Bitmap lookup = BitmapFactory.decodeResource(application.getResources(), R.drawable.lookup, options);
        filter3.setBitmap(lookup);

        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        filterGroup.addFilter(filter1);
        filterGroup.addFilter(filter2);
        filterGroup.addFilter(filter3);

        GPUImageIntensityBlendFilter intensityBlendFilter = new GPUImageIntensityBlendFilter();
        intensityBlendFilter.setBitmap(src);
        intensityBlendFilter.setIntensity(GPUImageIntensityBlendFilter.AGE_90);

        filterGroup.addFilter(intensityBlendFilter);

        mFilterBitmap = FaceDetect.getBitmapForFilter(src, filterGroup);
        return mFilterBitmap;
    }


    private void setupHairCoor(Bitmap src) {
        Log.e(TAG, "setupHairCoor: " + mHairMinRect);
        float outputWidth = src.getWidth();
        float outputHeight = src.getHeight();

        mHairVertexCoor = new float[]{
                0, 0,
                1, 0,
                0, 1,
                1, 1
        };
        RectF textureRect = new RectF(mHairMinRect);
        RectF realTextureRect = new RectF();
        float sw = 1f / textureRect.width();
        float sh = 1f / textureRect.height();
        float w1 = textureRect.left * sw;
        float h1 = textureRect.top * sh;
        realTextureRect.left = -w1;
        realTextureRect.top = -h1;
        realTextureRect.right = realTextureRect.left + src.getWidth() * sw;
        realTextureRect.bottom = realTextureRect.top + src.getHeight() * sh;
        mHairTextureCoor = new float[]{
                realTextureRect.left * outputWidth, realTextureRect.top * outputHeight,
                realTextureRect.right * outputWidth, realTextureRect.top * outputHeight,
                realTextureRect.left * outputWidth, realTextureRect.bottom * outputHeight,
                realTextureRect.right * outputWidth, realTextureRect.bottom * outputHeight
        };

    }


    public RectF getmHairMinRect() {
        return mHairMinRect;
    }

//    public void setmHairMinRect(RotatedRect hairMinRect, ImageView imageView) {
//        float angle = (float) hairMinRect.angle;
//        float cx = (float) hairMinRect.center.x;
//        float cy = (float) hairMinRect.center.y;
//        float width = (float) hairMinRect.size.width;
//        float height = (float) hairMinRect.size.height;
//        RectF rectF = new RectF(cx - width / 2, cy - height / 2, cx + width / 2, cy + height / 2);
//        float[] points = new float[]{
//                rectF.left, rectF.top,
//                rectF.left, rectF.bottom,
//                rectF.right, rectF.top,
//                rectF.right, rectF.bottom,
//        };
//        Matrix tmpM = new Matrix();
//        tmpM.setRotate(angle, cx, cy);
//        tmpM.mapPoints(points);
//        imageView.getImageMatrix().mapPoints(points);
//        mHairMinRect = new RectF(
//                Math.min(rectF.left, rectF.right),
//                Math.min(rectF.top, rectF.bottom),
//                Math.max(rectF.left, rectF.right),
//                Math.max(rectF.top, rectF.bottom));
//        DHLoger.e(TAG, mHairMinRect.toShortString());
//    }
}
