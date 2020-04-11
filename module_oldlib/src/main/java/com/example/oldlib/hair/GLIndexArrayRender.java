package com.example.oldlib.hair;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;


import com.example.oldlib.old.OpenGlUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GLIndexArrayRender {
    private static final String TAG = "GLIndexArrayRender";
    private static final String VERTEX_SHADER =
            "attribute highp vec4 position;\n" +
                    "attribute highp vec2 textureCoord2;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "void main() {\n" +
                    "    gl_Position = position;\n" +
                    "    textureCoordinate = (gl_Position.xy + vec2(1.0)) / vec2(2.0);\n" + // 顶点坐标转纹理坐标
                    "    textureCoordinate2 = textureCoord2;\n" +
                    "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "uniform highp float intensity;\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform sampler2D inputImageTexture2;\n" +
                    "\n" +
                    "\n" +
                    "void main() {\n" +
                    "     mediump vec4 base = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "     mediump vec4 overlay = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                    "     gl_FragColor = vec4(base.rgb * (overlay.a * (base.rgb / base.a) + (2.0 * overlay.rgb * (1.0 - (base.rgb / base.a)))) + overlay.rgb * (1.0 - base.a) + base.rgb * (1.0 - overlay.a), base.a);\n" +
                    "     gl_FragColor = vec4(mix(gl_FragColor, base, 1.0 - intensity).rgb, 1.0);\n" +
                    "}";

    private int mVertexDataBufferId;
    private int mPointIndicesId;
    private int mProgramId = 0;

    private int mPositionHandle; // 屏幕坐标
    private int mTextureCoordHandle2; // 纹理坐标
    private int mInputImageTextureHandle;
    private int mInputImageTextureHandle2;
    private int mIntensityHandle;
    private float mIntensity = 1.0f;


    public GLIndexArrayRender() {
        init();
    }

    private int init() {
        int programId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mIntensityHandle = GLES20.glGetUniformLocation(programId, "intensity");
        mTextureCoordHandle2 = GLES20.glGetAttribLocation(programId, "textureCoord2");
        mInputImageTextureHandle = GLES20.glGetUniformLocation(programId, "inputImageTexture");
        mInputImageTextureHandle2 = GLES20.glGetUniformLocation(programId, "inputImageTexture2");


        if (programId <= 0) {
            throw new RuntimeException("Error creating point drawing program");
        }
        mProgramId = programId;

        /*顶点数据缓冲区*/
        int[] vertexDataBufferId = new int[1];
        GLES20.glGenBuffers(1, vertexDataBufferId, 0);
        mVertexDataBufferId = vertexDataBufferId[0];

        /*顶点索引缓冲区*/
        int[] pointIndices = new int[1];
        GLES20.glGenBuffers(1, pointIndices, 0);
        mPointIndicesId = pointIndices[0];

        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mTextureCoordHandle2, 2, GLES20.GL_FLOAT, false, 20, 12);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle2);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glLineWidth(3.0F);
        OpenGlUtils.checkGLError("init");
        return programId;
    }

    /**
     * 网格绘制算法
     *
     * @param textureId   待绘制图像
     * @param imageWidth  纹理图片的宽高
     * @param imageHeight
     * @param width       屏幕（输出）宽高
     * @param height
     * @param textureCoor 用作纹理坐标  设计给的点+四个顶点
     * @param vertexCoor  用作顶点坐标 人脸关键点的实际坐标+图片四个顶点
     */
    public void draw(int srcTexture, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer,
                     int textureId, int imageWidth, int imageHeight, int width, int height,
                     float[] textureCoor, float[] vertexCoor) {
        if (textureId == OpenGlUtils.NO_TEXTURE || vertexCoor == null ||
                vertexCoor.length != textureCoor.length) {
            return;
        }
        GLES20.glUseProgram(mProgramId);
        GLES20.glUniform1f(mIntensityHandle, mIntensity);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle2);

        int pointSize = textureCoor.length / 2;
        Map map = new HashMap();

        /*顶点索引  由三角剖分获得*/
        float[] tmp = new float[vertexCoor.length];
        float value = 0.0f;
        for (short i = 0; i < pointSize; i++) {
            tmp[i * 2] = vertexCoor[i * 2] * (imageWidth - 1);
            tmp[i * 2 + 1] = vertexCoor[i * 2 + 1] * (imageHeight - 1);
            if (tmp[i * 2] > 0.0) {
                value = tmp[i * 2];
            }
            map.put(i, new PointF(tmp[i * 2], tmp[i * 2 + 1]));
        }
        float[] triangleList = new float[]{0.0f, value, 0.0f, 0.0f, value, 0.0f, value, value, 0.0f, value, value, 0.0f};
        ArrayList indexArray = new ArrayList();
        int triangleCount = triangleList.length / 6;
        for (int i = 0; i < triangleCount; i++) {
            float ax, ay, bx, by, cx, cy;
            ax = triangleList[i * 6];
            ay = triangleList[i * 6 + 1];
            bx = triangleList[i * 6 + 2];
            by = triangleList[i * 6 + 3];
            cx = triangleList[i * 6 + 4];
            cy = triangleList[i * 6 + 5];
            if (
                    ax >= 0 && ax < imageWidth &&
                            ay >= 0 && ay < imageHeight &&
                            bx >= 0 && bx < imageWidth &&
                            by >= 0 && by < imageHeight &&
                            cx >= 0 && cx < imageWidth &&
                            cy >= 0 && cy < imageHeight) {
                PointF pA = new PointF(ax, ay);
                PointF pB = new PointF(bx, by);
                PointF pC = new PointF(cx, cy);
                Set<Short> keySet = map.keySet();
                for (Short key : keySet) {
                    if (map.get(key).equals(pA)) {
                        indexArray.add(key);
                    }
                    if (map.get(key).equals(pB)) {
                        indexArray.add(key);
                    }
                    if (map.get(key).equals(pC)) {
                        indexArray.add(key);
                    }
                }
            }
        }
        short[] indexBuffer = new short[indexArray.size()];
        for (int i = 0; i < indexArray.size(); i++) {
            indexBuffer[i] = (short) indexArray.get(i);
        }
        Log.e(TAG, "draw: " + Arrays.toString(tmp));
        Log.e(TAG, "draw: " + Arrays.toString(indexBuffer) + " indexArray.size = " + indexArray.size());
        ShortBuffer indexesBuffer = OpenGlUtils.createShortBuffer(indexBuffer);
        indexesBuffer.position(0);

        /*顶点数据，包括顶点坐标和纹理坐标*/
        FloatBuffer vertexBuffer = OpenGlUtils.createFloatBuffer(pointSize * 5);
        for (int i = 0; i < pointSize; i++) {
            /*顶点坐标 （0，1）换算到（-1，1）要注意计算正确，否则会导致纹理翻转*/
            vertexBuffer.put(2 * vertexCoor[i * 2] - 1);
            vertexBuffer.put(1 - 2 * vertexCoor[i * 2 + 1]);
            vertexBuffer.put(1.0F);
            /*纹理坐标*/
            vertexBuffer.put(textureCoor[i * 2] / width);
            vertexBuffer.put(textureCoor[i * 2 + 1] / height);
        }

        vertexBuffer.position(0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, srcTexture);
        GLES20.glUniform1i(mInputImageTextureHandle, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mInputImageTextureHandle2, 5);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle2, 2, GLES20.GL_FLOAT, false, 20, 12);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexBuffer.length, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle2);
        GLES20.glUseProgram(0);
    }

    public void release() {
        if (mProgramId > 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }

    public void setIntensity(float intensity) {
        mIntensity = intensity;
    }

}

