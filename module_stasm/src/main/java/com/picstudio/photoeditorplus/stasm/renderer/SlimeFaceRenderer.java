package com.picstudio.photoeditorplus.stasm.renderer;

import android.opengl.GLES20;

import com.picstudio.photoeditorplus.stasm.OpenGlUtils;
import com.picstudio.photoeditorplus.stasm.StasmFaceDetectionSdk;

import java.nio.FloatBuffer;

import static com.picstudio.photoeditorplus.stasm.StasmFaceDetectionSdk.LANDMARKS.LEFT_EARLOBE;
import static com.picstudio.photoeditorplus.stasm.StasmFaceDetectionSdk.LANDMARKS.RIGHT_EARLOBE;

public class SlimeFaceRenderer {

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

    private static final String FRAGMENT_SHADER = "" +
            "precision highp float;  \n" +
            "const int MAX_FACE_COUNT = 6;\n" +
            "const int MAX_CONTOUR_POINT_COUNT = 5 * MAX_FACE_COUNT;\n" +
            "  \n" +
            "varying highp vec2 textureCoordinate;  \n" +
            "uniform sampler2D inputImageTexture;  \n" +
            "  \n" +
            "uniform int faceCount;  \n" +
            "uniform highp float radius[MAX_FACE_COUNT];  \n" +
            "  \n" +
            "uniform highp float aspectRatio;  \n" +
            "  \n" +
            "uniform float leftContourPoints[MAX_CONTOUR_POINT_COUNT*2];  \n" +
            "uniform float rightContourPoints[MAX_CONTOUR_POINT_COUNT*2];  \n" +
            "uniform float deltaArray[MAX_FACE_COUNT];  \n" +
            "uniform int arraySize;  \n" +
            "  \n" +
            "highp vec2 warpPositionToUse(vec2 currentPoint, vec2 contourPointA,  vec2 contourPointB, float radius, float delta, float aspectRatio)  \n" +
            "{  \n" +
            "    if (radius <= 0.0)\n" +
            "       return currentPoint;\n" +
            "    vec2 positionToUse = currentPoint;  \n" +
            "      \n" +
            "    vec2 currentPointToUse = vec2(currentPoint.x, currentPoint.y * aspectRatio + 0.5 - 0.5 * aspectRatio);  \n" +
            "    vec2 contourPointAToUse = vec2(contourPointA.x, contourPointA.y * aspectRatio + 0.5 - 0.5 * aspectRatio);  \n" +
            "      \n" +
            "    float r = distance(currentPointToUse, contourPointAToUse);  \n" +
            "    if(r < radius)  \n" +
            "    {  \n" +
            "        vec2 dir = normalize(contourPointB - contourPointA);  \n" +
            "        float dist = radius * radius - r * r;  \n" +
            "        float alpha = dist / (dist + (r-delta) * (r-delta));  \n" +
            "        alpha = alpha * alpha;  \n" +
            "          \n" +
            "        positionToUse = positionToUse - alpha * delta * dir;  \n" +
            "          \n" +
            "    }  \n" +
            "      \n" +
            "    return positionToUse;  \n" +
            "      \n" +
            "}  \n" +
            "  \n" +
            "  \n" +
            "void main()  \n" +
            "{  \n" +
            "    vec2 positionToUse = textureCoordinate;  \n" +
            "      \n" +
            "    for (int j = 0; j < faceCount; j++) \n" +
            "    {\n" +
            "       int offset = j * 10;\n" +
            "       for(int i = 0; i < arraySize; i++)  \n" +
            "       {  \n" +
            "           positionToUse = warpPositionToUse(positionToUse, vec2(leftContourPoints[offset + i * 2], leftContourPoints[offset + i * 2 + 1]), vec2(rightContourPoints[offset + i * 2], rightContourPoints[offset + i * 2 + 1]), radius[j], deltaArray[j], aspectRatio);  \n" +
            "           positionToUse = warpPositionToUse(positionToUse, vec2(rightContourPoints[offset + i * 2], rightContourPoints[offset + i * 2 + 1]), vec2(leftContourPoints[offset + i * 2], leftContourPoints[offset + i * 2 + 1]), radius[j], deltaArray[j], aspectRatio);  \n" +
            "       }  \n" +
            "    }\n" +
            "    gl_FragColor = texture2D(inputImageTexture, positionToUse);  \n" +
            "}";

    private int mProgramId;

    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mInputImageTextureHandle;

    private int mFaceCountHandle;
    private int mRadiusHandle;
    private int mAspectRatioHandle;

    private int mLeftContourPointsHandle;
    private int mRightContourPointsHandle;
    private int mDeltaArrayHandle;
    private int mArraySizeHandle;

    private int mFaceCount;
    private float[] mMaxRadius;
    private float[] mRadius;
    private float mAspectRatio;
    private float[] mLeftContourPoints;
    private float[] mRightContourPoints;
    private float[] mDeltaArray;
    private int mArraySize;

    public static SlimeFaceRenderer copyFrom(SlimeFaceRenderer src) {
        SlimeFaceRenderer renderer = new SlimeFaceRenderer();
        if (src != null) {
            renderer.mFaceCount = src.mFaceCount;
            renderer.mRadius = src.mRadius;
            renderer.mMaxRadius = src.mMaxRadius;
            renderer.mLeftContourPoints = src.mLeftContourPoints;
            renderer.mRightContourPoints = src.mRightContourPoints;
            renderer.mDeltaArray = src.mDeltaArray;
            renderer.mArraySize = src.mArraySize;
        }
        return renderer;
    }

    public void init() {
        mProgramId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(mProgramId, "position");
        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgramId, "inputTextureCoordinate");
        mInputImageTextureHandle = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");

        mFaceCountHandle = GLES20.glGetUniformLocation(mProgramId, "faceCount");
        mRadiusHandle = GLES20.glGetUniformLocation(mProgramId, "radius");
        mAspectRatioHandle = GLES20.glGetUniformLocation(mProgramId, "aspectRatio");

        mLeftContourPointsHandle = GLES20.glGetUniformLocation(mProgramId, "leftContourPoints");
        mRightContourPointsHandle = GLES20.glGetUniformLocation(mProgramId, "rightContourPoints");
        mDeltaArrayHandle = GLES20.glGetUniformLocation(mProgramId, "deltaArray");
        mArraySizeHandle = GLES20.glGetUniformLocation(mProgramId, "arraySize");
    }

    public void onOutputSizeChange(int width, int height) {
        mAspectRatio = (float)height / (float)width;
    }

    public void setRadiusIntensity(int index, float intensity) {
        if (mMaxRadius != null && index < mMaxRadius.length) {
            if (intensity == 0) {
                mRadius[index] = 0.0f;
            } else {
                mRadius[index] = mMaxRadius[index] / 2 + mMaxRadius[index] * intensity / 200.0f;
            }
        }
    }

    public void updateLandmarks(int imageWidth, int imageHeight, float[] landmarks) {
        if (landmarks == null) {
            mFaceCount = 0;
            mMaxRadius = new float[0];
            mRadius = new float[0];
            mDeltaArray = new float[0];
            mLeftContourPoints = new float[0];
            mRightContourPoints = new float[0];
            return;
        }
        int faceCount = landmarks.length / StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS;
        int perSize = StasmFaceDetectionSdk.LANDMARKS.LEFT_FACE_CONTOUR.length;
        if (faceCount > 6) faceCount = 6;
        mFaceCount = faceCount;
        mArraySize = perSize;
        mMaxRadius = new float[faceCount];
        mRadius = new float[faceCount];
        mDeltaArray = new float[faceCount];
        mLeftContourPoints = new float[mArraySize * 2 * faceCount];
        mRightContourPoints = new float[mArraySize * 2 * faceCount];

        for (int j = 0; j < faceCount; j++) {
            float[] leftContourPoints = StasmFaceDetectionSdk.getPointsByIndexes(landmarks,
                    StasmFaceDetectionSdk.LANDMARKS.LEFT_FACE_CONTOUR,
                    j * StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);
            float[] rightContourPoints = StasmFaceDetectionSdk.getPointsByIndexes(landmarks,
                    StasmFaceDetectionSdk.LANDMARKS.RIGHT_FACE_CONTOUR,
                    j * StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);

            float distance = StasmFaceDetectionSdk.distance(
                    StasmFaceDetectionSdk.getPointByIndex(landmarks, 72),
                    StasmFaceDetectionSdk.getPointByIndex(landmarks, 64));
            mMaxRadius[j] = distance / (float) imageWidth * 2.5f;

            distance = StasmFaceDetectionSdk.distance(
                    StasmFaceDetectionSdk.getPointByIndex(landmarks, LEFT_EARLOBE),
                    StasmFaceDetectionSdk.getPointByIndex(landmarks, RIGHT_EARLOBE));
            float faceWidthRatio = distance / (float) imageWidth;
            mDeltaArray[j] = faceWidthRatio;
            for (int i = 0; i < perSize; i++) {
                mLeftContourPoints[j * mArraySize * 2 + i * 2] = leftContourPoints[i * 2] / imageWidth;
                mLeftContourPoints[j * mArraySize * 2 + i * 2 + 1] = leftContourPoints[i * 2 + 1] / imageHeight;
                mRightContourPoints[j * mArraySize * 2 + i * 2] = rightContourPoints[i * 2] / imageWidth;
                mRightContourPoints[j * mArraySize * 2 + i * 2 + 1] = rightContourPoints[i * 2 + 1] / imageHeight;
            }
        }
    }

    public void onDraw(final int textureId, final FloatBuffer cubeBuffer,
                       final FloatBuffer textureBuffer) {
        GLES20.glUseProgram(mProgramId);

        GLES20.glUniform1i(mFaceCountHandle, mFaceCount);
        GLES20.glUniform1fv(mRadiusHandle, mFaceCount, mRadius, 0);
        GLES20.glUniform1f(mAspectRatioHandle, mAspectRatio);

        GLES20.glUniform1i(mArraySizeHandle, mArraySize);
        GLES20.glUniform1fv(mLeftContourPointsHandle, mArraySize * 2 * mFaceCount, mLeftContourPoints, 0);
        GLES20.glUniform1fv(mRightContourPointsHandle, mArraySize * 2 * mFaceCount, mRightContourPoints, 0);
        GLES20.glUniform1fv(mDeltaArrayHandle, mFaceCount, mDeltaArray, 0);

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
        mFaceCount = 0;
    }

    public void restoreEffect() {
        mFaceCount = mRadius != null ? mRadius.length : 0;
    }

    public void destroy() {
        if (mProgramId > 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }
}
