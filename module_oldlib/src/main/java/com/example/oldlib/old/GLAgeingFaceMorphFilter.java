package com.example.oldlib.old;

import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLAgeingFaceMorphFilter extends GPUImageFilter {
    private FaceCheckRender mRender;
    private float[] mLandmarks;
    private int mAge ;


    public GLAgeingFaceMorphFilter(float[] landmarks  ,int age) {
        super();
        mAge = age ;
        mLandmarks = landmarks;
    }

    @Override
    public void onInit() {
        super.onInit();
        updateTextureCoord();
        mRender = new FaceCheckRender();
        mRender.setLandmarks(mLandmarks , mAge);
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
        mRender.onOutputSizeChanged(width , height );
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!isInitialized()) {
            return;
        }
        mRender.onDraw(textureId , cubeBuffer , textureBuffer);

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
