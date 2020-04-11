package com.example.oldlib.hair;/*
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


import android.graphics.Bitmap;
import android.opengl.GLES20;


import com.example.oldlib.old.GPUImageFilter;
import com.example.oldlib.old.OpenGlUtils;
import com.example.oldlib.old.Rotation;

import java.nio.FloatBuffer;

;

public class GPUImageHairTextureFilter extends GPUImageFilter {
    private GLIndexArrayRender mGLIndexArrayRender;
    private float[] mTextureCoor;
    private float[] mVertexCoor;
    private int mTextureWidth;
    private int mTextureHeight;
    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    private Bitmap mBitmap;
    private float mIntensity = 0.8f;

    public GPUImageHairTextureFilter() {
    }

    public GPUImageHairTextureFilter(float[] textureCoor, float[] vertexCoor) {
        super();
        setRotation(Rotation.NORMAL, false, false);
        mTextureCoor = textureCoor;
        mVertexCoor = vertexCoor;
    }

    public void setTextureCoor(float[] textureCoor) {
        mTextureCoor = textureCoor;
    }

    public void setVertexCoor(float[] vertexCoor) {
        mVertexCoor = vertexCoor;
    }

    @Override
    public void onInit() {
        super.onInit();

        if (mBitmap != null&&!mBitmap.isRecycled()) {
            setBitmap(mBitmap);
        }
        mGLIndexArrayRender = new GLIndexArrayRender();
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
                    if (mBitmap == null || mBitmap.isRecycled()) {
                        return;
                    }
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture2 = OpenGlUtils.loadTexture(mBitmap, OpenGlUtils.NO_TEXTURE, false);
                    mTextureWidth = mBitmap.getWidth();
                    mTextureHeight = mBitmap.getHeight();
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
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!isInitialized()) {
            return;
        }
        mGLIndexArrayRender.setIntensity(mIntensity);
        mGLIndexArrayRender.draw(textureId, cubeBuffer, textureBuffer, mFilterSourceTexture2, mTextureWidth, mTextureHeight, mOutputWidth, mOutputHeight, mTextureCoor, mVertexCoor);
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        super.setRotation(rotation, flipHorizontal, flipVertical);
    }

    public void setIntensity(float intensity) {
        mIntensity = intensity;
    }
}

