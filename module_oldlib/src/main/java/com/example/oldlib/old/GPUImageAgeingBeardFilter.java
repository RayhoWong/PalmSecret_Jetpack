package com.example.oldlib.old;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GPUImageAgeingBeardFilter extends GPUImageFilter {
    private GLAgeingBeardRender mRender;
    private int mTextureWidth;
    private int mTextureHeight;
    private float[] mLandmarks;
    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    private Bitmap mBitmap;
    private float mAgeIntensity;

    private static final float[] SRC_CONTROL_POINTS_ARRAY = new float[]
            {
                    388, 476, 454, 475, 425, 460, 426 , 492 ,

                    418, 403, 368, 436, 477, 428,

                    563, 478, 621, 475, 597, 464, 602 , 493 ,

                    615, 405, 561, 426, 663, 419,

                    516, 624, 448, 672, 573, 669,514 ,663,

                    323, 475, 696, 479, 518, 799 ,
                    464, 690, 514 ,668,514 ,701 , 566 ,686 ,

                    306 , 523 ,  313 ,565 , 325 ,607, 341 ,647,365 ,686 ,396  ,722,428 ,754,461,781,
                    707 ,525,702 ,567,695 ,608,686  ,651,661  ,687,633,721,595,755,556,787,


            };
   /* private static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
            1, 2, 3, 4,
            20, 18, 19,
            10, 11, 12,13,
            28, 26, 27,
            46, 44, 45,47,
            62, 63, 64 ,
            58 , 54 , 55 ,61 ,
            65 , 66 ,67 ,68, 69,70 ,71,72 ,
            73, 74 , 75 , 76, 77,78,79,80
    };*/

    private static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
            37, 43, 40,48,
            12, 15, 19,
            21, 27, 24,32,
            2, 9, 5,
            62, 57, 67, 72,
            125, 102, 113,
            94 , 81 , 90 ,87,
            124 ,123 , 122 , 121 ,119, 118,116,115,
            103 , 104 ,105, 106 , 108, 109, 111, 112

    };


   /* private static final float[] SRC_CONTROL_POINTS_ARRAY = new float[]{
            257, 573, 425, 584, 347, 528, 341, 601,
            314, 430, 211, 477, 426, 474,
            626, 572, 795, 562, 714, 512,720, 591,
            706, 411, 601, 466, 808, 455,
            529, 989, 380, 1009, 680, 1014, 529, 1021,

            48, 574, 989, 550, 533, 1299,
            421, 1049, 529, 1025, 530, 1080, 643, 1052,
            54, 672, 73, 757, 90, 847, 103, 944, 138, 1037, 193, 1133, 293, 1222, 391, 1263,
            996, 634, 986, 728, 974, 824, 960, 918, 932, 1017, 891, 1100, 800, 1210, 678, 1267


    };*/
//   private static final float[] SRC_CONTROL_POINTS_ARRAY = new float[] {
//           257, 573, 425, 584, 347, 528, 341, 601,
//           314, 430, 211, 477, 426, 474,
//           626, 572, 795, 562, 714, 512,720,591 ,
//           706, 411, 601, 466, 808, 455,
//           528, 981, 379, 997, 678, 1002, 528, 1012,
//
//           51, 572, 982, 548, 531, 1285,
//           419, 1041, 528, 1016, 529, 1071, 642, 1043,
//           59, 670, 76, 755, 94, 845, 131, 942, 168, 1035, 233, 1136, 317, 1210, 414, 1257,
//           989, 631, 980, 726, 967, 822, 936, 932, 885, 1033, 817, 1143, 735, 1212, 648, 1267
//
//   };
//    private static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
//            1, 2, 3, 4,
//            20, 18, 19,
//            10, 11, 12,13,
//            28, 26, 27,
//            46, 44, 45,47,
//            62, 63, 64 ,
//            58 , 54 , 55 ,61 ,
//            65 , 66 ,67 ,68, 69,70 ,71,72 ,
//            73, 74 , 75 , 76, 77,78,79,80
//    };

    public GPUImageAgeingBeardFilter(float[] landmarks) {
        super();
        mLandmarks = landmarks;
    }

    @Override
    public void onInit() {
        super.onInit();

        mFilterSecondTextureCoordinateAttribute = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2"); // This does assume a name of "inputImageTexture2" for second input texture in the fragment shader
        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);

        updateTextureCoord();

        if (mBitmap != null && !mBitmap.isRecycled()) {
            setBitmap(mBitmap);
        }

        mRender = new GLAgeingBeardRender();
        mRender.setIntensity(mAgeIntensity);
    }

    public void setBitmap(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }
        mBitmap = bitmap;
        if (mBitmap == null) {
            return;
        }

        runOnDraw(new Runnable() {
            public void run() {
                if (mFilterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
                    if (bitmap == null || bitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture2 = OpenGlUtils.loadTexture(bitmap, OpenGlUtils.NO_TEXTURE, false);
                    mTextureWidth = bitmap.getWidth();
                    mTextureHeight = bitmap.getHeight();
                }
            }
        });
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{
                mFilterSourceTexture2
        }, 0);
        mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    }

    @Override
    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(mFilterSecondTextureCoordinateAttribute);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);
        GLES20.glUniform1i(mFilterInputTextureUniform2, 3);

        mTexture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mFilterSecondTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!isInitialized()) {
            return;
        }
        mRender.draw(textureId, mLandmarks, mOutputWidth, mOutputHeight,
                mFilterSourceTexture2, mTextureWidth, mTextureHeight,
                SRC_CONTROL_POINTS_ARRAY, TARGET_POINT_INDEXES_ARRAY);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        super.setRotation(rotation, flipHorizontal, flipVertical);
    }

    @Override
    public void onRotationChanged() {
        super.onRotationChanged();
        updateTextureCoord();
    }

    public void updateTextureCoord() {
        float[] buffer = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
        };
        Matrix matrix = new Matrix();
        if (mFlipHorizontal) {
            matrix.postScale(-1, 1, 0.5f, 0.5f);
        }
        if (mFlipVertical) {
            matrix.postScale(1, -1, 0.5f, 0.5f);
        }
        if (mRotation.asInt() != 0) {
            matrix.postRotate(mRotation.asInt(), 0.5f, 0.5f);
        }
        matrix.mapPoints(buffer, buffer);
        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();
        mTexture2CoordinatesBuffer = bBuffer;
    }

    public void setAgeIntensity(float ageIntensity) {
        mAgeIntensity = ageIntensity;
        if (mRender != null) {
            mRender.setIntensity(ageIntensity);
        }
    }
}
