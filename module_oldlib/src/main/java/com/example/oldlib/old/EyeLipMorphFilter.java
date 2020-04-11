package com.example.oldlib.old;

import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class EyeLipMorphFilter  extends GPUImageFilter {
    private EyeLipCheckRender mEyeRender;
    private float[] mLandmarks;
    private int mAge ;
    private  int mProEye ;
    private int mPromouth ;

    public EyeLipMorphFilter(float[] landmarks , int age , int eye , int mouth) {
        super();
        mAge = age;
        mLandmarks = landmarks;
        mProEye = eye ;
        mPromouth = mouth ;

    }

    @Override
    public void onInit() {
        super.onInit();
        updateTextureCoord();
        mEyeRender = new EyeLipCheckRender();
        mEyeRender.setLandmarks(mLandmarks  ,mAge , mProEye , mPromouth);
    }


    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onDrawArraysPre() {
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        mEyeRender.onOutputSizeChanged(width , height);
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!isInitialized()) {
            return;
        }
        mEyeRender.onDraw(textureId , cubeBuffer , textureBuffer);

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
    }

}