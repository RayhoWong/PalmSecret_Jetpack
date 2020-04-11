package com.example.oldlib.old;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GPUImageAgeMorphFilter extends GPUImageFilter {
    private GLAgeingMorphRender mRender;
    private int mTextureWidth;
    private int mTextureHeight;
    private float[] mLandmarks;
    public int mFilterSecondTextureCoordinateAttribute;
    public int mFilterInputTextureUniform2;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    private Bitmap mBitmap;
    private float mAgeIntensity;

    public static final float[] SRC_CONTROL_POINTS_ARRAY = new float[]
            {
                    379, 663, 454, 625, 451, 687, 410, 636, 410, 680, 501, 639, 495, 682,
                    880, 648, 803, 621, 809, 674, 759, 636, 766, 672, 847, 627, 849, 665,
                    363, 970, 402, 1041, 448, 1107, 502, 1168, 567, 1219,
                    962, 966, 923, 1039, 874, 1106, 815, 1167, 747, 1219,
                    524, 1033, 770, 1026, 643, 989, 643, 1023, 610, 982, 675, 981, 563, 1002,
                    726, 999, 584, 1024, 705, 1022, 643, 1043, 644, 1107, 583, 1040,
                    707, 1037, 550, 1070, 590, 1098, 700, 1094, 741, 1065,

            };

    public static final float[] DIS_CONTROL_POINTS_ARRAY = new float[]
            {
                    374, 675, 449, 636, 451, 690, 406, 649, 410, 686, 494, 644, 492, 683,
                    879, 663, 800, 626, 800, 680, 756, 635, 760, 673, 844, 637, 843, 675,
                    364, 975, 394, 1052, 431, 1137, 495, 1199, 567, 1227,
                    962, 972, 932, 1055, 890, 1139, 824, 1201, 745, 1230,
                    528, 1050, 758, 1040, 645, 998, 645, 1023, 614, 993, 675, 990, 568, 1020,
                    720, 1010, 589, 1031, 699, 1025, 646, 1061, 646, 1104, 588, 1058,
                    701, 1054, 558, 1078, 600, 1099, 693, 1094, 731, 1096,
            };

    private static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
            1, 3, 4, 5, 6, 7, 8,
            11, 12, 13, 14, 15, 16, 17,
            68, 69, 70, 71, 72,
            76, 77, 78, 79, 80,
            44, 45, 46, 47, 48, 49, 50,
            51, 52, 53, 54, 55, 56,
            57, 58, 59, 60, 61
    };

    public GPUImageAgeMorphFilter(float[] landmarks) {
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

        mRender = new GLAgeingMorphRender();
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
                SRC_CONTROL_POINTS_ARRAY, DIS_CONTROL_POINTS_ARRAY , TARGET_POINT_INDEXES_ARRAY);
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