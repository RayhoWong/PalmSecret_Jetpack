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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class RTGPUImageAgeingFilter extends GPUImageFilter {
    public static float AGE_50 = 0.3f;
    public static float AGE_70 = 0.5f;
    public static float AGE_90 = 0.8f;

    private RTGLAgeingRender mRender;
    private int mTextureWidth;
    private int mTextureHeight;
    private float[] mLandmarks;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private ByteBuffer mTexture2CoordinatesBuffer;
    private Bitmap mBitmap;
    private float mAgeIntensity = 1.0f;
    private String mCurTexturePath;

    public RTGPUImageAgeingFilter() {
        super();
    }

    /*外部传入人脸识别数据*/
    public void setLandmarks(final float[] landmarks) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                mLandmarks = landmarks;
            }
        });
    }

    /*外部传入变老素材（皱纹图）*/
    public void setBitmap(final String path) {
        if (!path.equals(mCurTexturePath) || mBitmap == null) {
            runOnDraw(new Runnable() {
                public void run() {
                    mCurTexturePath = path;
                    mBitmap = BitmapFactory.decodeFile(path);
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture2 = OpenGlUtils.loadTexture(mBitmap, OpenGlUtils.NO_TEXTURE, false);
                    mTextureWidth = mBitmap.getWidth();
                    mTextureHeight = mBitmap.getHeight();
                }
            });
        }
    }

    /*外部传入变老素材（皱纹图）*/
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
//                    mCurTexturePath = path;
//                    mBitmap = BitmapFactory.decodeFile(path);
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture2 = OpenGlUtils.loadTexture(mBitmap, OpenGlUtils.NO_TEXTURE, false);
                    mTextureWidth = mBitmap.getWidth();
                    mTextureHeight = mBitmap.getHeight();
                }
            }
        });

    }

    @Override
    public void onInit() {
        super.onInit();

        updateTextureCoord();

        mRender = new RTGLAgeingRender();
        mRender.setIntensity(mAgeIntensity);
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
                mFilterSourceTexture2);
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

    private static Bitmap loadBitmap(String maskFilePath) {
        File maskFile = new File(maskFilePath);
        if (!maskFile.exists()) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(maskFile.getPath(), options);
    }
}

