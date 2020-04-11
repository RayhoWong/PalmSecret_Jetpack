package com.picstudio.photoeditorplus.stasm.renderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.SparseArray;

import com.picstudio.photoeditorplus.stasm.MlsUtils;
import com.picstudio.photoeditorplus.stasm.OpenGlUtils;
import com.picstudio.photoeditorplus.stasm.StasmFaceDetectionSdk;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ruanjiewei
 * @since 2018.4.17
 * 基于Opengl实现面具绘制算法或基于掩码绘制算法
 */
public class GLMaskRenderer {

    private static final String VERTEX_SHADER_MASK =
            "attribute highp vec4 position;\n" +
            "attribute highp vec2 textureCoord;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "varying highp vec2 baseTextureCoordinate;\n" +
            "uniform highp mat4 matrix; \n" +
            "void main() {\n" +
            "    gl_Position = matrix * position;\n" +
            "    textureCoordinate = textureCoord;\n" +
            "    baseTextureCoordinate = (gl_Position.xy + vec2(1.0)) / vec2(2.0);\n" +
            "}";
    private static final String FRAGMENT_SHADER_MASK = "    precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            "varying highp vec2 baseTextureCoordinate;\n" +
            "uniform sampler2D inputImageTexture;\n" +
            "uniform highp float scaling;\n" +
            "uniform highp float alphaRatio;\n" +
            "uniform highp vec3 solidColor;\n" +
            "\n" +
            "uniform int robustTexture;\n" +
            "uniform sampler2D baseTexture;\n" +
            "uniform sampler2D lookupTexture;\n" +
            "\n" +
            "void main() {\n" +
            "   if (robustTexture > 0) {\n" +
            "       mediump vec4 baseColor = texture2D(baseTexture, baseTextureCoordinate);\n" +
            "       mediump vec4 curMask = texture2D(inputImageTexture, textureCoordinate);\n" +
            "       if (curMask.r > 0.001) {\n" +
            "           mediump vec4 newColor;\n" +
            "           if (solidColor.r >= 0.0 || solidColor.g >= 0.0 || solidColor.b >= 0.0) {\n" +
            "               mediump vec4 overlay = vec4(solidColor.rgb, 1.0);\n" +
            "               mediump vec4 whiteColor = vec4(1.0);\n" +
            "               newColor = vec4((whiteColor - (whiteColor - overlay) * overlay.a).rgb * baseColor.rgb, 1.0);\n" +
            "           } else {\n" +
            "               mediump float blueColor = baseColor.b * 15.0;\n" +
            "\n" +
            "               mediump vec2 quad1;\n" +
            "               quad1.y = floor(floor(blueColor) / 4.0);\n" +
            "               quad1.x = floor(blueColor) - (quad1.y * 4.0);\n" +
            "\n" +
            "               mediump vec2 quad2;\n" +
            "               quad2.y = floor(ceil(blueColor) / 4.0);\n" +
            "               quad2.x = ceil(blueColor) - (quad2.y * 4.0);\n" +
            "               quad1 = clamp(quad1, vec2(0.0), vec2(3.0));\n" +
            "               quad2 = clamp(quad2, vec2(0.0), vec2(3.0));\n" +
            "\n" +
            "               highp vec2 texPos1;\n" +
            "               texPos1.x = (quad1.x * 0.25) + 0.5/64.0 + ((0.25 - 1.0/64.0) * baseColor.r);\n" +
            "               texPos1.y = (quad1.y * 0.25) + 0.5/64.0 + ((0.25 - 1.0/64.0) * baseColor.g);\n" +
            "\n" +
            "               highp vec2 texPos2;\n" +
            "               texPos2.x = (quad2.x * 0.25) + 0.5/64.0 + ((0.25 - 1.0/64.0) * baseColor.r);\n" +
            "               texPos2.y = (quad2.y * 0.25) + 0.5/64.0 + ((0.25 - 1.0/64.0) * baseColor.g);\n" +
            "\n" +
            "               mediump vec4 newColor1 = texture2D(lookupTexture, texPos1);\n" +
            "               mediump vec4 newColor2 = texture2D(lookupTexture, texPos2);\n" +
            "               newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
            "           }\n" +
            "           gl_FragColor = mix(baseColor, vec4(newColor.rgb, baseColor.a), alphaRatio * curMask.r);\n" +
            "       } else {\n" +
            "           gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);\n" +
            "       }\n" +
            "       return;\n" +
            "   }\n" +
            "   if (solidColor.r >= 0.0 || solidColor.g >= 0.0 || solidColor.b >= 0.0) {\n" +
            "       highp float outputAlpha = alphaRatio * texture2D(inputImageTexture, textureCoordinate).r;\n" +
            "       gl_FragColor = vec4(vec3(scaling),1.) * vec4(solidColor * outputAlpha, outputAlpha);\n" +
            "   } else {\n" +
            "       gl_FragColor = vec4(vec3(scaling),1.) * alphaRatio * texture2D(inputImageTexture, textureCoordinate);\n" +
            "   }\n" +
            "}";


    private static short[] mLandMarkIndexes = new short[] {
            0, 7, 8, 7, 0, 3, 0, 4, 6, 4, 0, 8, 0, 5, 3,
            5, 0, 6, 65, 6, 66, 6, 65, 1, 1, 65, 62, 14, 9, 15,
            9, 14, 12, 39, 37, 10, 37, 39, 38, 19, 7, 25, 7, 19, 2,
            21, 25, 3, 25, 21, 20, 5, 6, 1, 8, 7, 2, 34, 38, 39,
            38, 34, 42, 3, 5, 21, 5, 1, 23, 5, 23, 21, 6, 4, 66,
            7, 3, 25, 26, 10, 37, 10, 26, 14, 4, 8, 38, 8, 2, 38,
            9, 13, 15, 13, 9, 17, 2, 19, 36, 33, 16, 29, 16, 33, 11,
            39, 10, 15, 36, 19, 37, 41, 39, 13, 39, 41, 43, 17, 9, 16,
            73, 11, 63, 11, 73, 17, 16, 9, 12, 26, 31, 14, 31, 26, 30,
            14, 15, 10, 17, 16, 11, 41, 13, 75, 12, 14, 31, 37, 19, 26,
            15, 13, 39, 36, 38, 2, 38, 36, 37, 16, 12, 29, 11, 27, 63,
            27, 11, 33, 13, 17, 74, 13, 74, 75, 81, 62, 88, 62, 81, 18,
            21, 23, 20, 1, 18, 23, 18, 1, 62, 19, 25, 24, 25, 20, 24,
            87, 26, 19, 26, 87, 30, 20, 23, 22, 83, 24, 20, 24, 83, 87,
            20, 22, 82, 18, 22, 23, 22, 18, 81, 30, 87, 86, 19, 24, 87,
            29, 12, 31, 29, 31, 28, 83, 20, 82, 63, 84, 106, 84, 63, 27,
            28, 31, 30, 28, 33, 29, 33, 28, 32, 82, 22, 81, 28, 30, 86,
            27, 32, 84, 32, 27, 33, 32, 28, 85, 38, 40, 66, 40, 38, 42,
            34, 35, 42, 35, 34, 43, 41, 51, 43, 51, 41, 45, 49, 35, 43,
            35, 49, 46, 4, 38, 66, 34, 39, 43, 40, 50, 44, 50, 40, 42,
            74, 17, 73, 42, 35, 48, 48, 35, 46, 46, 47, 48, 47, 46, 49,
            40, 44, 68, 67, 40, 68, 40, 67, 66, 101, 77, 102, 77, 101, 78,
            45, 41, 76, 41, 75, 76, 47, 49, 54, 48, 47, 52, 50, 52, 56,
            52, 50, 48, 53, 51, 57, 51, 53, 49, 47, 56, 52, 56, 47, 59,
            50, 42, 48, 43, 51, 49, 44, 50, 58, 49, 53, 54, 51, 45, 61,
            50, 56, 58, 58, 56, 59, 57, 51, 61, 57, 60, 53, 60, 57, 61,
            47, 54, 59, 54, 53, 60, 54, 55, 59, 55, 54, 60, 55, 72, 59,
            72, 55, 64, 44, 58, 70, 69, 44, 70, 44, 69, 68, 58, 59, 71,
            58, 71, 70, 55, 60, 80, 61, 79, 60, 79, 61, 78, 61, 45, 78,
            45, 77, 78, 77, 45, 76, 88, 62, 89, 81, 88, 82, 63, 105, 73,
            105, 63, 106, 64, 96, 72, 96, 64, 97, 64, 55, 80, 62, 65, 89,
            65, 66, 90, 65, 90, 89, 66, 67, 91, 90, 66, 91, 67, 68, 91,
            68, 69, 92, 68, 92, 91, 69, 70, 93, 71, 59, 72, 69, 93, 92,
            70, 71, 94, 70, 94, 93, 71, 72, 95, 95, 72, 96, 104, 74, 73,
            74, 104, 103, 103, 75, 74, 75, 103, 76, 102, 76, 103, 76, 102, 77,
            78, 100, 79, 100, 78, 101, 79, 100, 99, 60, 79, 80, 79, 99, 80,
            80, 99, 98, 64, 80, 98, 64, 98, 97, 28, 86, 85, 32, 85, 84,
            106, 84, 85, 94, 71, 95, 104, 73, 105};

    private static float[] mTexPositions = {0.35724115f, 0.3495687f, 0.2986952f, 0.35293356f, 0.41444468f, 0.36194533f, 0.35692626f, 0.33624002f, 0.35292587f, 0.36726674f, 0.32410958f, 0.34058106f, 0.32271212f, 0.36246556f, 0.39030963f, 0.34376612f, 0.38504156f, 0.36535573f, 0.6330655f, 0.34377083f, 0.57500553f, 0.35862106f, 0.6878436f, 0.34610704f, 0.6305858f, 0.33112606f, 0.6367275f, 0.3635153f, 0.5980494f, 0.340088f, 0.60449296f, 0.36225286f, 0.6632746f, 0.33438954f, 0.66603374f, 0.35724804f, 0.24303105f, 0.3032631f, 0.4266042f, 0.3210332f, 0.3380254f, 0.29394805f, 0.33292893f, 0.31160673f, 0.28783196f, 0.28981432f, 0.28793365f, 0.30556923f, 0.38548943f, 0.30262664f, 0.3784585f, 0.3189298f, 0.56120247f, 0.31909496f, 0.7489651f, 0.2973035f, 0.65018463f, 0.28349572f, 0.6563077f, 0.3032218f, 0.60020626f, 0.29462594f, 0.6099645f, 0.31328613f, 0.70267963f, 0.2806497f, 0.7020712f, 0.2974506f, 0.5005088f, 0.48414567f, 0.4997111f, 0.51313615f, 0.44940495f, 0.361941f, 0.5353187f, 0.36027274f, 0.4407414f, 0.45505258f, 0.55266154f, 0.4528817f, 0.42498592f, 0.48998478f, 0.5708264f, 0.48731005f, 0.45851588f, 0.505403f, 0.5395383f, 0.504093f, 0.38842657f, 0.5596441f, 0.6069294f, 0.55654716f, 0.49679828f, 0.55352175f, 0.4978244f, 0.5728455f, 0.47183457f, 0.5493493f, 0.52114123f, 0.54856974f, 0.43004653f, 0.55477375f, 0.5637102f, 0.5527814f, 0.44377455f, 0.5684687f, 0.5515452f, 0.56695145f, 0.49845538f, 0.57424635f, 0.49964845f, 0.60449356f, 0.44336194f, 0.56952536f, 0.5526909f, 0.5679392f, 0.41875002f, 0.5814506f, 0.45330024f, 0.5977174f, 0.54521185f, 0.59679466f, 0.5786413f, 0.5793434f, 0.20908436f, 0.35206452f, 0.7893579f, 0.3377424f, 0.5013966f, 0.68830407f, 0.21501444f, 0.39938492f, 0.22655146f, 0.44580525f, 0.24266031f, 0.49145997f, 0.26532254f, 0.5359068f, 0.29871377f, 0.5763974f, 0.34044942f, 0.61245763f, 0.3859659f, 0.6463274f, 0.43750587f, 0.6771479f, 0.7861341f, 0.38586187f, 0.7769084f, 0.4335512f, 0.76258165f, 0.4808706f, 0.7425332f, 0.5273634f, 0.71268386f, 0.5707394f, 0.6735955f, 0.6102737f, 0.62779367f, 0.6462648f, 0.57363063f, 0.6765607f, 0.23078659f, 0.28214374f, 0.29257712f, 0.22024813f, 0.38153464f, 0.17551711f, 0.7612152f, 0.2696495f, 0.69154495f, 0.21119781f, 0.5946261f, 0.17085531f, 0.4847379f, 0.1570901f, 0.09414399f, 0.3238398f, 0.1024461f, 0.39008838f, 0.118597895f, 0.45507684f, 0.14115028f, 0.51899344f, 0.17287737f, 0.58121896f, 0.21962513f, 0.63790584f, 0.27805504f, 0.6883902f, 0.34177822f, 0.7358079f, 0.4139341f, 0.77895665f, 0.5033811f, 0.79457515f, 0.6045087f, 0.7781344f, 0.68033713f, 0.7357203f, 0.74445957f, 0.6853327f, 0.7991833f, 0.6299847f, 0.8409723f, 0.56925833f, 0.86904013f, 0.5041684f, 0.88909763f, 0.4379212f, 0.9020137f, 0.37115616f, 0.9065269f, 0.3037888f};

    private int mVertexDataBufferId;
    private int mPointIndicesId;
    private int[] mTextureId = new int[1];
    private static Mat[] mImage;
    private String mMaskFile = "";
    private int mProgramId = 0;
    private int mPointSize;

    private static float[] mMVPMatrix = new float[16];
    private float[] mLandMarksBuffer;
    private float[] mFaceLandmarkBuffer;
    private FloatBuffer mBuffer;
    private FloatBuffer mVertexBuffer;

    private ShortBuffer mLandMarkIndexesBuffer;

    private int mPositionHandle;
    private int mTextureCoordHandle;
    private int mMatrixHandle;
    private int mInputImageTextureHandle;
    private int mScaleHandle;
    private int mAlphaRatioHandle;
    private int mSolidColorHandle;

    // 增加对Lookup渲染支持
    private int mRobustTextureHandle;
    private int mBaseTextureHandle;
    private int mLookupTextureHandle;

    private int mCurrentBmpIndex;

    public GLMaskRenderer() {
        mImage = new Mat[2];
        mImage[0] = new Mat();
        mImage[1] = new Mat();
        mBuffer = FloatBuffer.allocate(StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);
        mFaceLandmarkBuffer = new float[StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS];
        mLandMarksBuffer = new float[mFaceLandmarkBuffer.length /*+ mExtraFaceData.length*/];
        mLandMarkIndexesBuffer = OpenGlUtils.createShortBuffer(mLandMarkIndexes);
        mPointSize = StasmFaceDetectionSdk.COUNT_FULL_POINTS;
        mVertexBuffer = OpenGlUtils.createFloatBuffer(mPointSize * 5);
    }

    public void setTextureIdCache(int[] textureIds) {
        mTextureId[0] = textureIds[0];
    }

    public void init() {
        int programId = OpenGlUtils.loadProgram(VERTEX_SHADER_MASK, FRAGMENT_SHADER_MASK);
        mPositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mTextureCoordHandle = GLES20.glGetAttribLocation(programId, "textureCoord");
        mMatrixHandle = GLES20.glGetUniformLocation(programId, "matrix");
        mInputImageTextureHandle = GLES20.glGetUniformLocation(programId, "inputImageTexture");
        mScaleHandle = GLES20.glGetUniformLocation(programId, "scaling");
        mAlphaRatioHandle = GLES20.glGetUniformLocation(programId, "alphaRatio");
        mSolidColorHandle = GLES20.glGetUniformLocation(programId, "solidColor");

        mRobustTextureHandle = GLES20.glGetUniformLocation(programId, "robustTexture");
        mBaseTextureHandle = GLES20.glGetUniformLocation(programId, "baseTexture");
        mLookupTextureHandle = GLES20.glGetUniformLocation(programId, "lookupTexture");

        mProgramId = programId;
        GLES20.glUseProgram(programId);

        GLES20.glUniform1i(mRobustTextureHandle, -1);
        GLES20.glUniform1f(mScaleHandle, 1.0F);
        GLES20.glUniform1f(mAlphaRatioHandle, 1.0F);
        GLES20.glUniform3fv(mSolidColorHandle, 1, new float[] {-1.0f, -1.0f, -1.0f}, 0);

        int[] vertexDataBufferId = new int[1];
        GLES20.glGenBuffers(1, vertexDataBufferId, 0);
        mVertexDataBufferId = vertexDataBufferId[0];

        int[] pointIndices = new int[1];
        GLES20.glGenBuffers(1, pointIndices, 0);
        mPointIndicesId = pointIndices[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glLineWidth(3.0F);
        OpenGlUtils.checkGLError("init");
    }

    public void drawMeshWithMultiply(int baseTextureId, int maskTextureId, ColorInfo colorInfo,
                                     int imageWidth, int imageHeight, int width, int height,
                                     float[] srcMeshPoints, float[] targetMeshPoints, int stride, float alphaRatio) {
        drawRobustMesh(baseTextureId, maskTextureId, OpenGlUtils.NO_TEXTURE, colorInfo, imageWidth, imageHeight,
                width, height, srcMeshPoints, targetMeshPoints, stride, alphaRatio);
    }

    public void drawMeshWithLookup(int baseTextureId, int maskTextureId, int lookupTextureId,
                                   int imageWidth, int imageHeight, int width, int height,
                                   float[] srcMeshPoints, float[] targetMeshPoints, int stride, float alphaRatio) {
        drawRobustMesh(baseTextureId, maskTextureId, lookupTextureId, null, imageWidth, imageHeight,
                width, height, srcMeshPoints, targetMeshPoints, stride, alphaRatio);
    }

    public void drawRobustMesh(int baseTextureId, int maskTextureId, int lookupTextureId, ColorInfo colorInfo,
                               int imageWidth, int imageHeight, int width, int height,
                               float[] srcMeshPoints, float[] targetMeshPoints, int stride, float alphaRatio) {
        if (targetMeshPoints == null || targetMeshPoints.length != srcMeshPoints.length) {
            return;
        }
        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        if (colorInfo != null) {
            GLES20.glUniform3fv(mSolidColorHandle, 1, colorInfo.getNormaliziedColor(), 0);
        } else {
            GLES20.glUniform3fv(mSolidColorHandle, 1, new float[]{-1, -1, -1}, 0);
        }
        GLES20.glUniform1i(mRobustTextureHandle, 1);
        GLES20.glUniform1f(mAlphaRatioHandle, alphaRatio);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        int pointSize = srcMeshPoints.length / 2;
        int rows = srcMeshPoints.length / stride;
        int cols = stride / 2;

        FloatBuffer vertexBuffer = createVertexBuffer(width, height, imageWidth, imageHeight,
                pointSize, targetMeshPoints, srcMeshPoints);
        ShortBuffer indexesBuffer = getOrCreateMeshDrawingIndexBuffer(rows, cols);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, baseTextureId);
        GLES20.glUniform1i(mBaseTextureHandle, 3);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        if (lookupTextureId != OpenGlUtils.NO_TEXTURE) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, lookupTextureId);
            GLES20.glUniform1i(mLookupTextureHandle, 4);
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, maskTextureId);
        GLES20.glUniform1i(mInputImageTextureHandle, 5);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, (rows - 1) * (cols - 1) * 6, GLES20.GL_UNSIGNED_SHORT, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);
    }

    /**
     * 网格绘制算法
     * @param textureId 待绘制图像
     * @param srcMeshPoints 用作纹理坐标
     * @param targetMeshPoints 用作GL坐标
     * @param stride points里每行的float个数
     */
    public void drawMesh(int textureId, int imageWidth, int imageHeight, int width, int height,
                         float[] srcMeshPoints, float[] targetMeshPoints, int stride,
                         float solidR, float solidG, float solidB, float alphaRatio) {
        if (textureId == OpenGlUtils.NO_TEXTURE || targetMeshPoints == null ||
                targetMeshPoints.length != srcMeshPoints.length) {
            return;
        }

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform3fv(mSolidColorHandle, 1, new float[] {solidR, solidG, solidB}, 0);
        GLES20.glUniform1i(mRobustTextureHandle, -1);
        GLES20.glUniform1f(mAlphaRatioHandle, alphaRatio);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        int pointSize = srcMeshPoints.length / 2;
        int rows = srcMeshPoints.length / stride;
        int cols = stride / 2;

        FloatBuffer vertexBuffer = createVertexBuffer(width, height, imageWidth, imageHeight,
                pointSize, targetMeshPoints, srcMeshPoints);
        ShortBuffer indexesBuffer = getOrCreateMeshDrawingIndexBuffer(rows, cols);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mInputImageTextureHandle, 5);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, (rows - 1) * (cols - 1) * 6, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);
    }

    public void drawMesh(int textureId, int imageWidth, int imageHeight, int width, int height,
                         float[] srcMeshPoints, float[] targetMeshPoints, int stride, float alphaRatio) {
        drawMesh(textureId, imageWidth, imageHeight, width, height, srcMeshPoints, targetMeshPoints, stride, -1.0f, -1.0f, -1.0f, alphaRatio);
    }

    public void drawMask(float[] landMarks, int width, int height, int maskTexture, boolean enableBlend) {
        if (landMarks == null) {
            return;
        }

        if (enableBlend) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform3fv(mSolidColorHandle, 1, new float[] {-1.0f, -1.0f, -1.0f}, 0);
        GLES20.glUniform1i(mRobustTextureHandle, -1);
        GLES20.glUniform1f(mAlphaRatioHandle, 1.0F);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        int faceCount = landMarks.length / StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS;
        for (int i = 0; i < faceCount; i++) {
            mBuffer.position(0);
            mBuffer.put(landMarks, i * StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS, StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);
            mBuffer.position(0);
            mBuffer.get(mFaceLandmarkBuffer);

            System.arraycopy(mFaceLandmarkBuffer, 0, mLandMarksBuffer, 0, mFaceLandmarkBuffer.length);

            int pointSize = mPointSize;
            float[] landMarksBuffer = mLandMarksBuffer;
            float[] texPositions = mTexPositions;
            FloatBuffer vertexBuffer = mVertexBuffer;
            ShortBuffer landMarkIndexesBuffer = mLandMarkIndexesBuffer;
            short[] landMarkIndexes = mLandMarkIndexes;

            vertexBuffer.clear();
            for (int i1 = 0; i1 < pointSize; i1++) { // 关键点转化为GL顶点坐标
                float x = 2.0F * landMarksBuffer[4 + (2 * i1)] / width - 1.0f;
                float y = 2.0F * landMarksBuffer[4 + 2 * i1 + 1] / height - 1.0F;

                vertexBuffer.put(x);
                vertexBuffer.put(y);
                vertexBuffer.put(1.0F);
                vertexBuffer.put(texPositions[(2 * i1)]);
                vertexBuffer.put(texPositions[(2 * i1 + 1)]);
            }
            vertexBuffer.position(0);
            landMarkIndexesBuffer.position(0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, maskTexture);
            GLES20.glUniform1i(mInputImageTextureHandle, 4);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, landMarkIndexesBuffer.capacity() * 2, landMarkIndexesBuffer, GLES20.GL_STATIC_DRAW);
            // GL_ARRAY_BUFFER空间里面有两种坐标
            // 前三个是x,y,z顶点坐标, 后两个是纹理坐标
            // 注意到stride = 4(float 字节数) * 5 = 20
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0); // 无offset，且字节数为3 * 4 = 12
            GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12); // offset刚好为12, 偏移了顶点坐标的字节数
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, landMarkIndexes.length / 3 * 3, GLES20.GL_UNSIGNED_SHORT, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);
        if (enableBlend) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
        OpenGlUtils.checkGLError("draw");
    }

    public void drawMask(float[] landMarks, int width, int height, String maskFile, boolean enableBlend, boolean unPremultiplyAlpha) {
        if (maskFile == null || landMarks == null) {
            return;
        }

        if (enableBlend) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform3fv(mSolidColorHandle, 1, new float[] {-1.0f, -1.0f, -1.0f}, 0);
        GLES20.glUniform1i(mRobustTextureHandle, -1);
        GLES20.glUniform1f(mAlphaRatioHandle, 1.0F);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        int faceCount = landMarks.length / StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS;
        for (int i = 0; i < faceCount; i++) {
            mBuffer.position(0);
            mBuffer.put(landMarks, i * StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS, StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS);
            mBuffer.position(0);
            mBuffer.get(mLandMarksBuffer);

            int pointSize = mPointSize;
            float[] landMarksBuffer = mLandMarksBuffer;
            float[] texPositions = mTexPositions;
            FloatBuffer vertexBuffer = mVertexBuffer;
            ShortBuffer landMarkIndexesBuffer = mLandMarkIndexesBuffer;
            short[] landMarkIndexes = mLandMarkIndexes;
            int textureId = mTextureId[0];

            if (!maskFile.equals(mMaskFile)) {
                loadBitmap(maskFile, mImage[0], unPremultiplyAlpha);
                int lastMarkIndex = maskFile.lastIndexOf(File.separator);
                if (lastMarkIndex > 0) {
                    String openMouthMaskFile = maskFile.substring(0, lastMarkIndex + 1) + "om_" +
                            maskFile.substring(lastMarkIndex + 1);
                    loadBitmap(openMouthMaskFile, mImage[1], unPremultiplyAlpha);
                }
                mMaskFile = maskFile;
                byte[] imageData = new byte[mImage[0].cols() * mImage[0].rows() * 4];
                mImage[0].get(0, 0, imageData);
                ByteBuffer imageDataBuffer = ByteBuffer.wrap(imageData);
                imageDataBuffer.position(0);
                GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mImage[0].cols(), mImage[0].rows(), 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, imageDataBuffer);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                mCurrentBmpIndex = 0;
            }

            vertexBuffer.clear();
            for (int i1 = 0; i1 < pointSize; i1++) { // 关键点转化为GL顶点坐标
                float x = 2.0F * landMarksBuffer[4 + (2 * i1)] / width - 1.0f;
                float y = 2.0F * landMarksBuffer[4 + 2 * i1 + 1] / height - 1.0F;

                vertexBuffer.put(x);
                vertexBuffer.put(y);
                vertexBuffer.put(1.0F);
                vertexBuffer.put(texPositions[(2 * i1)]);
                vertexBuffer.put(texPositions[(2 * i1 + 1)]);
            }
            vertexBuffer.position(0);
            landMarkIndexesBuffer.position(0);
            int targetBitmap = StasmFaceDetectionSdk.checkMouthOpened(mFaceLandmarkBuffer) ? 1 : 0;
            if (mCurrentBmpIndex != targetBitmap) {
                if (!mImage[targetBitmap].empty()) {
                    StasmFaceDetectionSdk.loadTexture(mImage[targetBitmap].dataAddr(),
                            mImage[targetBitmap].width(), mImage[targetBitmap].height(), textureId);
                    mCurrentBmpIndex = targetBitmap;
                }
            }
            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(mInputImageTextureHandle, 4);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, landMarkIndexesBuffer.capacity() * 2, landMarkIndexesBuffer, GLES20.GL_STATIC_DRAW);
            // GL_ARRAY_BUFFER空间里面有两种坐标
            // 前三个是x,y,z顶点坐标, 后两个是纹理坐标
            // 注意到stride = 4(float 字节数) * 5 = 20
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0); // 无offset，且字节数为3 * 4 = 12
            GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12); // offset刚好为12, 偏移了顶点坐标的字节数
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, landMarkIndexes.length / 3 * 3, GLES20.GL_UNSIGNED_SHORT, 0);

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);
        if (enableBlend) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
        OpenGlUtils.checkGLError("draw");
    }

    public void drawMaskPair(float[] landMarks, int width, int height,
                              int[] maskTextureIds, int[] leftMaskSize, int[] rightMaskSize,
                              float[][] srcControlPointsArray, int[][] targetPointIndexesArray) {
        int[][] imageSizeArray = new int[][] {
                leftMaskSize,
                rightMaskSize
        };
        int areaCount = 5; // 将图像切割成 5 * 5 的区域
        float[] leftGridPoints = createMeshPoints(imageSizeArray[0][0], imageSizeArray[0][1], areaCount);
        float[] rightGridPoints = createMeshPoints(imageSizeArray[1][0], imageSizeArray[1][1], areaCount);
        float[][] gridPointsArray = new float[][] {
                leftGridPoints,
                rightGridPoints
        };

        for (int j = 0; j < 2; j++) {
            float[] srcControlPoints = srcControlPointsArray[j];
            int[] targetPointIndexes = targetPointIndexesArray[j];
            float[] targetControlPoints = new float[srcControlPoints.length];
            float[] gridPoints = gridPointsArray[j];
            int imageWidth = imageSizeArray[j][0];
            int imageHeight = imageSizeArray[j][1];

            for (int k = 0; k < targetPointIndexes.length; k++) {
                int index = targetPointIndexes[k];
                float[] pointData = StasmFaceDetectionSdk.getPointByIndex(landMarks, index);
                targetControlPoints[k * 2] = pointData[0];
                targetControlPoints[k * 2 + 1] = pointData[1];
            }
            float[] result = MlsUtils.mlsWithRigid(maskTextureIds[j], gridPoints, srcControlPoints, targetControlPoints, false);
            drawMesh(maskTextureIds[j], imageWidth, imageHeight, width, height, gridPoints, result, (areaCount + 1) * 2, 1.0f);
        }
    }

    /**
     * 保留底图特征的绘制方式
     * @param landMarks 单一人脸数据
     * @param maskTextureId 掩码图纹理id
     * @param maskImageWidth 掩码图宽度
     * @param maskImageHeight 掩码图长度
     * @param lookupImageTextureId Lookup颜色纹理id
     * @param colorInfo 颜色信息
     */
    public void drawRobustMaskMakeup(int srcTexture, float[] landMarks, int maskTextureId, int maskImageWidth, int maskImageHeight,
                                      int outputWidth, int outputHeight, int lookupImageTextureId, ColorInfo colorInfo, float[][] srcControlPointsArray,
                                      int[][] targetPointIndexesArray, float alphaRatio) {
        if (srcControlPointsArray.length != 1 || targetPointIndexesArray.length != 1 ||
                (lookupImageTextureId == OpenGlUtils.NO_TEXTURE && colorInfo == null)) {
            return;
        }

        int areaCount = 5;
        float[] srcMeshPoints = createMeshPoints(maskImageWidth, maskImageHeight, areaCount);

        int[] targetPointIndexes = targetPointIndexesArray[0];
        float[] targetControlPoints = new float[srcControlPointsArray[0].length];
        for (int k = 0; k < targetPointIndexes.length; k++) {
            int index = targetPointIndexes[k];
            float[] pointData = StasmFaceDetectionSdk.getPointByIndex(landMarks, index);
            targetControlPoints[k * 2] = pointData[0];
            targetControlPoints[k * 2 + 1] = pointData[1];
        }
        float[] result = MlsUtils.mlsWithRigid(maskTextureId, srcMeshPoints, srcControlPointsArray[0], targetControlPoints, false);
        if (lookupImageTextureId != OpenGlUtils.NO_TEXTURE) {
            drawMeshWithLookup(srcTexture, maskTextureId, lookupImageTextureId, maskImageWidth, maskImageHeight, outputWidth, outputHeight,
                    srcMeshPoints, result, (areaCount + 1) * 2, alphaRatio);
        } else {
            drawMeshWithMultiply(srcTexture, maskTextureId, colorInfo, maskImageWidth, maskImageHeight, outputWidth, outputHeight,
                    srcMeshPoints, result, (areaCount + 1) * 2, alphaRatio);
        }
    }

    /**
     * 直接使用textures进行mls绘制
     * @param landmarks 单一人脸数据
     * @param textures 图片纹理ids
     * @param imageSizes 图片尺寸数组
     * @param srcControlPointsArray 图片控制点数组
     * @param targetPointIndexesArray 目标控制点人脸关键点索引数组
     * @param alphaRatio 透明度 [0.0f, 1.0f]
     */
    public void drawStickersMakeup(float[] landmarks, int[] textures, int[][] imageSizes,
                                    float[][] srcControlPointsArray, int[][] targetPointIndexesArray,
                                    float alphaRatio, int outputWidth, int outputHeight) {
        int imageCount = imageSizes.length;
        if (imageSizes.length == textures.length &&
                imageSizes.length == srcControlPointsArray.length &&
                imageSizes.length == targetPointIndexesArray.length &&
                landmarks != null) {
            float[][] gridPointsArray = new float[imageCount][];
            int areaCount = 5;
            for (int i = 0; i < imageCount; i++) {
                gridPointsArray[i] = createMeshPoints(imageSizes[i][0], imageSizes[i][1], areaCount);
            }

            drawMesh(landmarks, textures, null, srcControlPointsArray, targetPointIndexesArray, outputWidth, outputHeight, gridPointsArray, imageSizes, areaCount, alphaRatio, false);
        }
    }

    public void drawMesh(float[] landMarks, int[] texIds, ColorInfo colorInfo, float[][] srcControlPointsArray, int[][] targetPointIndexesArray,
                         int outputWidth, int outputHeight, float[][] gridPointsArray, int[][] imageSizes, int areaCount, float alphaRatio, boolean useMlsCache) {
        int imgSize = texIds.length;
        for (int j = 0; j < imgSize; j++) {
            float[] srcControlPoints = srcControlPointsArray[j];
            int[] targetPointIndexes = targetPointIndexesArray[j];
            float[] gridPoints = gridPointsArray[j];
            float[] targetControlPoints = new float[srcControlPoints.length];
            int imageWidth = imageSizes[j][0];
            int imageHeight = imageSizes[j][1];

            for (int k = 0; k < targetPointIndexes.length; k++) {
                int index = targetPointIndexes[k];
                float[] pointData = StasmFaceDetectionSdk.getPointByIndex(landMarks, index);
                targetControlPoints[k * 2] = pointData[0];
                targetControlPoints[k * 2 + 1] = pointData[1];
            }
            int cacheKey = useMlsCache ? texIds[j] : -1;
            float[] result = MlsUtils.mlsWithRigid(cacheKey, gridPoints, srcControlPoints, targetControlPoints, false);
            if (colorInfo != null) {
                // 绘制纯色mask
                drawMesh(texIds[j], imageWidth, imageHeight, outputWidth, outputHeight, gridPoints, result, (areaCount + 1) * 2,
                        (float)colorInfo.getColorR() / 255.0f, (float)colorInfo.getColorG() / 255.0f, (float)colorInfo.getColorB() / 255.0f, alphaRatio);
            } else {
                // 保持输入图片的颜色
                drawMesh(texIds[j], imageWidth, imageHeight, outputWidth, outputHeight, gridPoints, result, (areaCount + 1) * 2, alphaRatio);
            }
        }
    }

    /* 将图像切割成 areaCount * areaCount 的网格区域 */
    private float[] createMeshPoints(int imageWidth, int imageHeight, int areaCount) {
        int pointCount = areaCount + 1;
        float widthStep = (float)imageWidth / (float)areaCount;
        float heightStep = (float)imageHeight / (float)areaCount;

        float[] gridPoints = new float[pointCount * pointCount * 2];
        for (int i = 0; i < pointCount; i++) {
            for (int j = 0; j < pointCount; j++) {
                int pointIndex = i * pointCount + j;
                gridPoints[pointIndex * 2] = j * widthStep;
                gridPoints[pointIndex * 2 + 1] = i * heightStep;
            }
        }
        return gridPoints;
    }

    public void release() {
        DRAW_MESH_INDEXES_CACHE.clear();
        VERTEX_BUFFER_CACHE.clear();
        GLES20.glDeleteProgram(mProgramId);
        if (mImage[0] != null && !mImage[0].empty()) {
            mImage[0].release();
        }
        if (mImage[1] != null && !mImage[1].empty()) {
            mImage[1].release();
        }
        mMaskFile = "";
    }

    private static boolean loadBitmap(String maskFilePath, Mat outMat, boolean unPremultiplyAlpha) {
        File maskFile = new File(maskFilePath);
        if (!maskFile.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(maskFile.getPath(), options);
        if (bitmap == null) {
            return false;
        }
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.preScale(1.0F, -1.0F);
        Bitmap verticalFlipBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        outMat.create(verticalFlipBitmap.getHeight(), verticalFlipBitmap.getWidth(), CvType.CV_8U);
        Utils.bitmapToMat(bitmap, outMat, unPremultiplyAlpha);
        bitmap.recycle();
        verticalFlipBitmap.recycle();
        return true;
    }

    // 网格绘制索引值缓存
    private final Map<Point, ShortBuffer> DRAW_MESH_INDEXES_CACHE = new HashMap<>();
    private final SparseArray<FloatBuffer> VERTEX_BUFFER_CACHE = new SparseArray<>();

    private ShortBuffer getOrCreateMeshDrawingIndexBuffer(int rows, int cols) {
        Point sizePoint = new Point(rows, cols);
        ShortBuffer indexesBuffer = DRAW_MESH_INDEXES_CACHE.get(sizePoint);
        if (indexesBuffer == null) {
            short[] drawingIndexes = new short[(rows - 1) * (cols - 1) * 6];
            int indicator = 0;
            for (int i = 0; i < rows - 1; i++) {
                for (int j = 0; j < cols - 1; j++) {
                    short a = (short) (i * cols + j);
                    short b = (short) (a + 1);
                    short c = (short) (a + cols);
                    short d = (short) (c + 1);
                    drawingIndexes[indicator++] = a;
                    drawingIndexes[indicator++] = b;
                    drawingIndexes[indicator++] = c;
                    drawingIndexes[indicator++] = b;
                    drawingIndexes[indicator++] = c;
                    drawingIndexes[indicator++] = d;
                }
            }
            indexesBuffer = OpenGlUtils.createShortBuffer(drawingIndexes);
            DRAW_MESH_INDEXES_CACHE.put(sizePoint, indexesBuffer);
        }
        indexesBuffer.position(0);
        return indexesBuffer;
    }

    private FloatBuffer createVertexBuffer(int width, int height, int imageWidth, int imageHeight,
                                           int pointSize, float[] targetMeshPoints, float[] srcMeshPoints) {
        float widthRatio = 1.0f / width;
        float heightRatio = 1.0f / height;
        float imageWidthRatio = 1.0f / imageWidth;
        float imageHeightRatio = 1.0f / imageHeight;

        FloatBuffer vertexBuffer = VERTEX_BUFFER_CACHE.get(pointSize);
        if (vertexBuffer == null) {
            vertexBuffer = OpenGlUtils.createFloatBuffer(pointSize * 5);
            VERTEX_BUFFER_CACHE.put(pointSize, vertexBuffer);
        }
        vertexBuffer.clear();
        int index;
        for (int i = 0; i < pointSize; i++) {
            index = i << 1;
            float x = (targetMeshPoints[index] + targetMeshPoints[index]) * widthRatio - 1.0f;
            float y = (targetMeshPoints[index + 1] + targetMeshPoints[index + 1]) * heightRatio - 1.0F;

            vertexBuffer.put(x);
            vertexBuffer.put(y);
            vertexBuffer.put(1.0F);
            vertexBuffer.put(srcMeshPoints[index] * imageWidthRatio);
            vertexBuffer.put(srcMeshPoints[index + 1] * imageHeightRatio);
        }
        vertexBuffer.position(0);
        return vertexBuffer;
    }
}
