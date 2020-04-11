/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.oldlib.old;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GPUImageAgeingFilter extends GPUImageFilter {
    private GLAgeingRender mRender;
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
                    388, 476, 454, 475, 425, 460,

                    418, 403, 368, 436, 477, 428,

                    563, 478, 621, 475, 597, 464,

                    615, 405, 561, 426, 663, 419,

                    516, 624, 448, 672, 573, 669,514 ,663,

                    323, 475, 696, 479, 518, 799 ,
                    464, 690, 514 ,668,514 ,701 , 566 ,686
            };
//    public static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
//            1, 2, 3,
//            20, 18, 19,
//            10, 11, 12,
//            28, 26, 27,
//            46, 44, 45, 47,
//            62, 63, 64,
//            58 , 54 , 55 ,61
//    };

    public static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
            37, 43, 40,
            12, 15, 19,
            21, 27, 24,
            2, 9, 5,
            62, 57, 67, 72,
            125, 102, 113,
            94 , 81 , 90 ,87
    };

    public GPUImageAgeingFilter(float[] landmarks) {
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

        mRender = new GLAgeingRender();
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

