package com.example.oldlib.old;

import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.SparseArray;


import com.picstudio.photoeditorplus.stasm.MlsUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

public class GLAgeingBeardRender {
    private static final String TAG = "GLAgeingRender";
    private static final String VERTEX_SHADER =
            "attribute highp vec4 position;\n" + // 顶点坐标
                    "attribute highp vec2 textureCoord;\n" + // 纹理坐标
                    "attribute highp vec2 textureCoord2;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "uniform highp mat4 matrix; \n" +
                    "void main() {\n" +
                    "    gl_Position = matrix * position;\n" +
                    "    gl_Position.y = -gl_Position.y;\n" +
                    "    textureCoordinate = (gl_Position.xy + vec2(1.0)) / vec2(2.0);\n" + // 顶点坐标转纹理坐标
                    "    textureCoordinate2 = textureCoord;\n" +
                    "}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform sampler2D inputImageTexture2;\n" +
                    "\n" +
                    "\n" +
                    " void main()\n" +
                    " {\n" +
                    "     lowp vec4 c2 = texture2D(inputImageTexture, textureCoordinate);\n" +
                    "\t lowp vec4 c1 = texture2D(inputImageTexture2, textureCoordinate2);\n" +
                    "     \n" +
                    "     lowp vec4 outputColor;\n" +
                    "     \n" +
                    "     outputColor.r = c1.r + c2.r * c2.a * (1.0 - c1.a);\n" +
                    "\n" +
                    "     outputColor.g = c1.g + c2.g * c2.a * (1.0 - c1.a);\n" +
                    "     \n" +
                    "     outputColor.b = c1.b + c2.b * c2.a * (1.0 - c1.a);\n" +
                    "     \n" +
                    "     outputColor.a = c1.a + c2.a * (1.0 - c1.a);\n" +
                    "     \n" +
                    "     gl_FragColor = outputColor;\n" +
                    " }";

    private int mVertexDataBufferId;
    private int mPointIndicesId;
    private int mProgramId = 0;

    private int mPositionHandle; // 屏幕坐标
    private int mTextureCoordHandle; // 纹理坐标
    //    private int mTextureCoordHandle2; // 纹理坐标
    private int mMatrixHandle;
    private int mInputImageTextureHandle;
    private int mInputImageTextureHandle2;

    public static float[] mMVPMatrix = new float[16];


    public GLAgeingBeardRender() {
        init();
    }

    private int init() {
        int programId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mTextureCoordHandle = GLES20.glGetAttribLocation(programId, "textureCoord");
        mMatrixHandle = GLES20.glGetUniformLocation(programId, "matrix");
        mInputImageTextureHandle2 = GLES20.glGetUniformLocation(programId, "inputImageTexture2");
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
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
//        GLES20.glVertexAttribPointer(mTextureCoordHandle2, 2, GLES20.GL_FLOAT, false, 20, 12);
//        GLES20.glEnableVertexAttribArray(mTextureCoordHandle2);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glLineWidth(3.0F);
        OpenGlUtils.checkGLError("init");
        return programId;
    }

    public void draw(int srcTexture, float[] landMarks, int width, int height,
                     int maskTextureId, int textureWidth, int textureHeight,
                     float[] srcControlPoints, int[] targetPointIndexes) {
        int areaCount = 50; // 将图像切割成 5 * 5 的区域

        float[] targetControlPoints = new float[srcControlPoints.length];
        for (int k = 0; k < targetPointIndexes.length; k++) {
            int index = targetPointIndexes[k];
            float[] pointData = StasmFaceDetectionSdk.getPointByIndex(landMarks, index);
            targetControlPoints[k * 2] = pointData[0];
            targetControlPoints[k * 2 + 1] = pointData[1];
        }
        float[] gridPoints = createMeshPoints(textureWidth, textureHeight, areaCount , targetControlPoints);
//        for (int i = targetPointIndexes.length ; i < targetPointIndexes.length + 5 ; i++){
//            int index = 35;
//            float[] pointData = StasmFaceDetectionSdk.getPointByIndex(landMarks, index);
//            if(i - targetPointIndexes.length == 0){
//                targetControlPoints[i * 2] = pointData[0] - 200;
//                targetControlPoints[i * 2 + 1] = pointData[1] ;
//            }
//            if(i - targetPointIndexes.length == 1){
//                targetControlPoints[i * 2] = pointData[0] - 100;
//                targetControlPoints[i * 2 + 1] = pointData[1];
//            }
//            if(i - targetPointIndexes.length == 2){
//                targetControlPoints[i * 2] = pointData[0] ;
//                targetControlPoints[i * 2 + 1] = pointData[1];
//            }
//            if(i - targetPointIndexes.length == 3){
//                targetControlPoints[i * 2] = pointData[0] + 100;
//                targetControlPoints[i * 2 + 1] = pointData[1];
//            }
//            if(i - targetPointIndexes.length == 4){
//                targetControlPoints[i * 2] = pointData[0] + 200;
//                targetControlPoints[i * 2 + 1] = pointData[1];
//            }
//        }
        float[] result = MlsUtils.mlsWithRigid(maskTextureId, gridPoints, srcControlPoints, targetControlPoints, false);
        drawMesh(srcTexture, maskTextureId, textureWidth, textureHeight, width, height, gridPoints, result, (areaCount + 1) * 2);
    }

    /* 将图像切割成 areaCount * areaCount 的网格区域 */
    private float[] createMeshPoints(int imageWidth, int imageHeight, int areaCount , float[] tarPoint) {
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

    public void drawMesh(int srcTextureId, int textureId, int imageWidth, int imageHeight, int width, int height,
                         float[] srcMeshPoints, float[] targetMeshPoints, int stride) {
        if (textureId == OpenGlUtils.NO_TEXTURE || targetMeshPoints == null ||
                targetMeshPoints.length != srcMeshPoints.length) {
            return;
        }

        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);
//        GLES20.glEnableVertexAttribArray(mTextureCoordHandle2);

        int pointSize = srcMeshPoints.length / 2;
        int rows = srcMeshPoints.length / stride;
        int cols = stride / 2;

        FloatBuffer vertexBuffer = createVertexBuffer(width, height,
                imageWidth, imageHeight, pointSize,
                srcMeshPoints, targetMeshPoints);
        ShortBuffer indexesBuffer = getOrCreateMeshDrawingIndexBuffer(rows, cols);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, srcTextureId);
        GLES20.glUniform1i(mInputImageTextureHandle, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mInputImageTextureHandle2, 5);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0); // 顶点数据(X, Y, Z)
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12); // 纹理数据
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, (rows - 1) * (cols - 1) * 6, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);
    }

    // 网格绘制索引值缓存
    private final Map<Point, ShortBuffer> DRAW_MESH_INDEXES_CACHE = new HashMap<>();
    private final SparseArray<FloatBuffer> VERTEX_BUFFER_CACHE = new SparseArray<>();
    private FloatBuffer createVertexBuffer(int width, int height, int imageWidth, int imageHeight,
                                           int pointSize, float[] srcMeshPoints, float[] targetMeshPoints) {
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

            vertexBuffer.put(x); // 顶点数据
            vertexBuffer.put(y);
            vertexBuffer.put(1.0F);
            vertexBuffer.put(srcMeshPoints[index] * imageWidthRatio); // 纹理数据
            vertexBuffer.put(srcMeshPoints[index + 1] * imageHeightRatio);
        }
        vertexBuffer.position(0);
        return vertexBuffer;
    }

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

    public void release() {
        if (mProgramId > 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }

    public void setIntensity(float intensity) {
    }
}
