package com.picstudio.photoeditorplus.stasm.renderer;

import android.opengl.GLES20;
import android.util.SparseArray;

import com.picstudio.photoeditorplus.stasm.OpenGlUtils;
import com.picstudio.photoeditorplus.stasm.StasmFaceDetectionSdk;

import java.nio.FloatBuffer;

public class BigEyeRenderer {

    private static final String VERTEX_SHADER =
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";

    private static final String FRAGMENT_SHADER =
            "precision highp float;\n" +
            "\n" +
            "uniform float width;\n" +
            "uniform float height;\n" +
            "\n" +
            "uniform vec3 eyesPos[10];\n" +
            "uniform int eyesSize;\n" +
            "uniform float leftLimit;\n" +
            "uniform float rightLimit;\n" +
            "uniform float topLimit;\n" +
            "uniform float downLimit;\n" +
            "\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 coordinate = vec2(textureCoordinate.x * width, textureCoordinate.y * height);\n" +
            "    vec2 referPoint = vec2(-1.0, -1.0);\n" +
            "\n" +
//            "    if (coordinate.x >= leftLimit && coordinate.x <= rightLimit && coordinate.y >= topLimit && coordinate.y <= downLimit)\n" +
//            "       for (int i = 0; i < eyesSize; i++) {\n" +
//            "           float radius = eyesPos[i].z;\n" +
//            "           if (radius > 0.0 && distance(eyesPos[i].xy, coordinate) <= radius) {\n" +
//            "               referPoint = eyesPos[i].xy;\n" +
//            "               float dx = coordinate.x - referPoint.x;\n" +
//            "               float dy = coordinate.y - referPoint.y;\n" +
//            "               float rsq = dx * dx + dy * dy;\n" +
//            "               float rnorm = sqrt(rsq / (radius * radius));\n" +
//            "               float a = 1.0 - 0.3 * (rnorm - 1.0) * (rnorm - 1.0);\n" +
//            "               coordinate.x = clamp(referPoint.x + a * dx, 0.0, width - 1.0) / width;\n" +
//            "               coordinate.y = clamp(referPoint.y + a * dy, 0.0, height - 1.0) / height;\n" +
//            "               gl_FragColor = texture2D(inputImageTexture, coordinate);\n" +
//            "               break;\n" +
//            "           }\n" +
//            "       }\n" +
//            "    if (referPoint.x < 0.0) {\n" +
            "        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
//            "        return;\n" +
            "    }\n" +
            "}";

    private int mProgramId;

    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mInputImageTextureHandle;

    private int mWidthHandle;
    private int mHeightHandle;
    private int mEyesPosHandle;
    private int mEyesSizeHandle;

    private int mLeftLimitHandle;
    private int mRightLimitHandle;
    private int mTopLimitHandle;
    private int mDownLimitHandle;

    private int mWidth;
    private int mHeight;

    private float mLeftLimit = mWidth - 1;
    private float mTopLimit = mHeight - 1;
    private float mRightLimit = 0;
    private float mDownLimit = 0;

    private float mMaxRadius;
    private float[] mEyesPos;
    private int mEyesSize;

    public BigEyeRenderer() {
    }

    public static BigEyeRenderer copyFrom(BigEyeRenderer renderer, SparseArray<Float> intensityMap,
                                          float maxIntensity, float minIntensity) {
        BigEyeRenderer result = new BigEyeRenderer();
        if (renderer != null) {
            result.mLeftLimit = renderer.mLeftLimit;
            result.mRightLimit = renderer.mRightLimit;
            result.mTopLimit = renderer.mTopLimit;
            result.mDownLimit = renderer.mDownLimit;
            result.mEyesSize = renderer.mEyesSize;
            result.mEyesPos = new float[renderer.mEyesPos.length];
            System.arraycopy(renderer.mEyesPos, 0,
                    result.mEyesPos, 0, renderer.mEyesPos.length);
            int count = result.mEyesSize / 2;
            for (int i = 0; i < count; i++) {
                Float intensity = intensityMap.get(i);
                if (intensity != null) {
                    result.updateIntensity(i,
                            intensity / 100f * (maxIntensity - minIntensity) + minIntensity);
                }
            }
        }
        return result;
    }

    public void init() {
        mProgramId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "position");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgramId, "inputTextureCoordinate");
        mInputImageTextureHandle = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");

        mWidthHandle = GLES20.glGetUniformLocation(mProgramId, "width");
        mHeightHandle = GLES20.glGetUniformLocation(mProgramId, "height");
        mEyesPosHandle = GLES20.glGetUniformLocation(mProgramId, "eyesPos");
        mEyesSizeHandle = GLES20.glGetUniformLocation(mProgramId, "eyesSize");

        mLeftLimitHandle = GLES20.glGetUniformLocation(mProgramId, "leftLimit");
        mRightLimitHandle = GLES20.glGetUniformLocation(mProgramId, "rightLimit");
        mTopLimitHandle = GLES20.glGetUniformLocation(mProgramId, "topLimit");
        mDownLimitHandle = GLES20.glGetUniformLocation(mProgramId, "downLimit");
    }

    public void onOutputSizeChange(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setMaxRadius(float maxRadius) {
        mMaxRadius = maxRadius;
    }

    public void updateLandmarks(int imageWidth, int imageHeight, float[] landmarks) {
        if (landmarks == null) {
            mEyesSize = 0;
            mEyesPos = new float[0];
            return;
        }
        int faceSize = landmarks.length / StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS;
        mEyesSize = faceSize * 2;
        if (mEyesPos == null || mEyesPos.length != mEyesSize * 3) {
            mEyesPos = new float[mEyesSize * 3];
        }
        int eyeCount = 0;
        mLeftLimit = mWidth - 1;
        mTopLimit = mHeight - 1;
        mRightLimit = 0;
        mDownLimit = 0;
        for (int i = 0; i < faceSize; i++) {
            float[] leftEye = StasmFaceDetectionSdk.getPointByIndex(landmarks, StasmFaceDetectionSdk.LANDMARKS.LEFT_PUPIL, i * StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);
            float[] rightEye = StasmFaceDetectionSdk.getPointByIndex(landmarks, StasmFaceDetectionSdk.LANDMARKS.RIGHT_PUPIL, i * StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);
            leftEye[0] = leftEye[0] / (float)imageWidth * mWidth;
            leftEye[1] = leftEye[1] / (float)imageHeight * mHeight;
            rightEye[0] = rightEye[0] / (float)imageWidth * mWidth;
            rightEye[1] = rightEye[1] / (float)imageHeight * mHeight;

            if ((leftEye[0] - mMaxRadius) < mLeftLimit) {
                mLeftLimit = leftEye[0] - mMaxRadius;
                if (mLeftLimit < 0) {
                    mLeftLimit = 0;
                }
            }
            if ((rightEye[0] + mMaxRadius) > mRightLimit) {
                mRightLimit = rightEye[0] + mMaxRadius;
                if (mRightLimit > mWidth - 1) {
                    mRightLimit = mWidth - 1;
                }
            }
            float tempUp;
            float tempDown;
            if (leftEye[1] < rightEye[1]) {
                tempUp = leftEye[1];
                tempDown = rightEye[1];
            } else {
                tempUp = rightEye[1];
                tempDown = leftEye[1];
            }
            if ((tempUp - mMaxRadius) < mTopLimit) {
                mTopLimit = tempUp - mMaxRadius;
                if (mTopLimit < 0) {
                    mTopLimit = 0;
                }
            }
            if ((tempDown + mMaxRadius) > mDownLimit) {
                mDownLimit = tempDown + mMaxRadius;
                if (mDownLimit > mHeight - 1) {
                    mDownLimit = mHeight - 1;
                }
            }

            System.arraycopy(leftEye, 0, mEyesPos, eyeCount * 3, 2);
            eyeCount++;
            System.arraycopy(rightEye, 0, mEyesPos, eyeCount * 3, 2);
            eyeCount++;
        }
    }

    public void updateIntensity(int index, float intensity) {
        if (index < mEyesSize / 2) {
            mEyesPos[index * 6 + 2] = intensity;
            mEyesPos[index * 6 + 5] = intensity;
        }
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mProgramId);

        GLES20.glUniform1f(mWidthHandle, mWidth);
        GLES20.glUniform1f(mHeightHandle, mHeight);
        GLES20.glUniform1i(mEyesSizeHandle, mEyesSize);
        GLES20.glUniform3fv(mEyesPosHandle, mEyesSize, mEyesPos, 0);

        GLES20.glUniform1f(mLeftLimitHandle, mLeftLimit);
        GLES20.glUniform1f(mRightLimitHandle, mRightLimit);
        GLES20.glUniform1f(mTopLimitHandle, mTopLimit);
        GLES20.glUniform1f(mDownLimitHandle, mDownLimit);

        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0,
                textureBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
        if (textureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mInputImageTextureHandle, 0);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void clearEffect() {
        mEyesSize = 0;
    }

    public void restoreEffect() {
        mEyesSize = mEyesPos != null ? mEyesPos.length / 3 : 0;
    }

    public void destroy() {
        if (mProgramId > 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }
}
