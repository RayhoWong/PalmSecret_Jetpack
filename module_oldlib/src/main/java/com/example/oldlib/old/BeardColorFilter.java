package com.example.oldlib.old;

import android.graphics.Matrix;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.example.oldlib.old.StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS;


public class BeardColorFilter  extends GPUImageFilter {
    private float[] mLandmarks;
    private int mAge;


    /***输入两个纹理坐标、一个变换矩阵，输出一个通过变换后的纹理坐标，另一个输出的纹理坐标等于输入坐标***/
    private static final String VERTEX_SHADER =
            "attribute highp vec4 position;\n" + // 输入顶点坐标
                    "attribute highp vec2 textureCoord;\n" + // 输入用户纹理坐标
                    "varying highp vec2 textureCoordinate;\n" + //输出用户纹理坐标
                    "uniform highp mat4 matrix; \n" +    //变换矩阵
                    "void main() {\n" +
                    "    gl_Position = matrix * position;\n" +
                    "    textureCoordinate = textureCoord;\n" +
                    "}";



    /***输入两个纹理坐标和两个纹理，mix(overlay操作结果 ，base,1.0-intensity)作为输出结果***/
    private static final String FRAGMENT_SHADER =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +  //输入用户纹理坐标
                    "uniform sampler2D inputImageTexture;\n" +  //输入用户纹理
                    "uniform highp float intensity;\n" +
                    "\n" +
                    "\n" +
                    "void main() {\n" +
                    "     mediump vec4 base = texture2D(inputImageTexture,textureCoordinate);\n" + //提取用户中的纹素
                    "    if (intensity == 1.0f) {\n" +
                        "    if (base.r > 0.1 && base.g > 0.1 && base.b > 0.1) {\n" +
                        "        gl_FragColor = vec4(0.0,0.0,0.0,1.0);;\n" +
                        "    } else {\n" +
                        "     mediump vec4 base1 = vec4(1.0,1.0,1.0,1.0);\n" +
                        "     gl_FragColor = vec4(1.0,1.0,1.0,1.0);\n" +
                        "    }"+
                    "    } else {\n" +
                    "     gl_FragColor = vec4(0.0,0.0,0.0,1.0);\n" +
                    "    }"+

                    "}";


    /***顶点句柄***/
    private int mPositionHandle;

    /***纹理坐标句柄***/
    private int mTextureCoordHandle;

    /***顶点缓冲数组句柄***/
    private int mVertexDataBufferId;

    /***顶点索引句柄***/
    private int mPointIndicesId;

    /***用户纹理句柄***/
    private int mInputImageTextureHandle;

    /***变换矩阵句柄***/
    private int mMatrixHandle;

    private int mProgramId = 0;
    private int mIntensityHandle;



    public static float[] mMVPMatrix = new float[16];

    /**人脸脸颊扩展三角剖分，整个下巴区域分成三角形**/
    private static short[] extensionCheekDrawingIndices = {


            43, 39, 44, 10, 20, 18, 20, 10, 11, 1, 2, 31, 2, 1, 17,
            19, 2, 17, 2, 19, 3, 3, 33, 32, 33, 3, 4, 2, 3, 32,
            20, 11, 12, 4, 35, 34, 35, 4, 5, 4, 3, 19, 34, 35, 30,
            5, 4, 21, 4, 19, 21, 35, 5, 36, 5, 6, 36, 6, 5, 29,
            18, 25, 26, 25, 18, 24, 6, 7, 37, 7, 6, 28, 7, 27, 8,
            27, 7, 28, 36, 6, 37, 7, 8, 38, 0, 8, 26, 8, 0, 38,
            20, 12, 13, 1, 9, 17, 23, 13, 14, 13, 23, 22, 9, 10, 18,
            10, 9, 40, 41, 10, 40, 10, 41, 11, 6, 29, 28, 42, 11, 41,
            11, 42, 12, 21, 17, 29, 17, 21, 19, 43, 13, 12, 13, 43, 44,
            24, 14, 15, 14, 24, 23, 13, 44, 45, 14, 13, 45, 15, 25, 24,
            25, 15, 16, 15, 14, 46, 14, 45, 46, 0, 16, 47, 16, 0, 26,
            16, 15, 47, 17, 9, 18, 17, 18, 26, 5, 21, 29, 13, 22, 20,
            18, 20, 22, 18, 22, 23, 26, 8, 27, 18, 23, 24, 25, 16, 26,
            17, 26, 27, 17, 27, 28, 17, 28, 29, 2, 32, 31, 33, 4, 34,
            37, 7, 38, 12, 42, 43, 15, 46, 47

    };



    private static int[] checkIndexInLandMarkIndex = {
            113,
            121 , 120 , 119 ,118,117,116,115,114,
            105,106,107,108,109,110,111,112,
            54,56,
            57,67,
            94, 86 ,87,88,89,90,91,92,93,
    };

    public BeardColorFilter(float[] landmarks, int age) {
        super();
        mAge = age;
        mLandmarks = landmarks;
    }


    private int initdata() {

        int programId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        mPositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mMatrixHandle = GLES20.glGetUniformLocation(programId, "matrix");
        mTextureCoordHandle = GLES20.glGetAttribLocation(programId, "textureCoord");

        mInputImageTextureHandle = GLES20.glGetUniformLocation(programId, "inputImageTexture");

        if (programId <= 0) {
            throw new RuntimeException("Error creating point drawing program");
        }
        mProgramId = programId;
        mIntensityHandle = GLES20.glGetUniformLocation(programId, "intensity");


        /**坐标数据缓冲区 (顶点+纹理坐标)**/
        int[] vertexDataBufferId = new int[1];
        GLES20.glGenBuffers(1, vertexDataBufferId, 0);
        mVertexDataBufferId = vertexDataBufferId[0];

        /****顶点索引缓冲区*****/
        int[] pointIndices = new int[1];
        GLES20.glGenBuffers(1, pointIndices, 0);
        mPointIndicesId = pointIndices[0];

        GLES20.glLineWidth(3.0F);
        OpenGlUtils.checkGLError("init");

        return programId;
    }

    public static float[] extendLandMarkWithCheck(float[] singleLandMark ){
        float[] checkLandMark = new float[48 * 2];

        float[] p0 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, 113) ;/**扩展人脸关键点数组 （x,y）**/
        p0[1] = p0[1] + 200 ;
        float[] p1= StasmFaceDetectionSdk.getPointByIndex(singleLandMark, 121);/**扩展人脸关键点数组 （x,y）**/
        float[] p7 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, 105);/**扩展人脸关键点数组 （x,y）**/

        /**求过p0垂直p0p7的垂足**/
        float[] p30 = new float[]{p1[0] , p0[1]};
        float[] p39 = new float[]{p7[0] , p0[1]};

        float[] p31 = new float[] {p1[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,120)[1]};
        float[] p32 = new float[] {p1[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,119)[1]};
        float[] p33 = new float[] {p1[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,118)[1]};
        float[] p34 = new float[] {p1[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,117)[1]};

        float[] p35 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,118)[0], p0[1] };
        float[] p36 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,117)[0], p0[1] };
        float[] p37 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,116)[0], p0[1] };
        float[] p38 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,115)[0], p0[1] };


        float[] p40 = new float[] {p7[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,106)[1]};
        float[] p41 = new float[] {p7[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,107)[1]};
        float[] p42 = new float[] {p7[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,108)[1]};
        float[] p43 = new float[] {p7[0] , StasmFaceDetectionSdk.getPointByIndex(singleLandMark,109)[1]};

        float[] p44 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,108)[0], p0[1] };
        float[] p45 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,109)[0], p0[1] };
        float[] p46 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,110)[0], p0[1] };
        float[] p47 = new float[] {StasmFaceDetectionSdk.getPointByIndex(singleLandMark,111)[0], p0[1] };



        int j=0;
        for(; j< checkIndexInLandMarkIndex.length; j++){
            float pp[]  =  StasmFaceDetectionSdk.getPointByIndex(singleLandMark, checkIndexInLandMarkIndex[j]);
            checkLandMark[j * 2]  = pp[0];
            checkLandMark[j * 2 +1 ]  = pp[1];
        }

        checkLandMark[j * 2] = p30[0];
        checkLandMark[j * 2 + 1] =   p30[1];
        j++;
        checkLandMark[j * 2] = p31[0];
        checkLandMark[j * 2 + 1] =   p31[1];
        j++;
        checkLandMark[j * 2] = p32[0];
        checkLandMark[j * 2 + 1] =   p32[1];
        j++;
        checkLandMark[j * 2] = p33[0];
        checkLandMark[j * 2 + 1] =   p33[1];
        j++;
        checkLandMark[j * 2] = p34[0];
        checkLandMark[j * 2 + 1] =   p34[1];
        j++;
        checkLandMark[j * 2] = p35[0];
        checkLandMark[j * 2 + 1] =   p35[1];
        j++;
        checkLandMark[j * 2] = p36[0];
        checkLandMark[j * 2 + 1] =   p36[1];
        j++;
        checkLandMark[j * 2] = p37[0];
        checkLandMark[j * 2 + 1] =   p37[1];
        j++;
        checkLandMark[j * 2] = p38[0];
        checkLandMark[j * 2 + 1] =   p38[1];
        j++;
        checkLandMark[j * 2] = p39[0];
        checkLandMark[j * 2 + 1] =   p39[1];
        j++;
        checkLandMark[j * 2] = p40[0];
        checkLandMark[j * 2 + 1] =   p40[1];
        j++;
        checkLandMark[j * 2] = p41[0];
        checkLandMark[j * 2 + 1] =   p41[1];
        j++;
        checkLandMark[j * 2] = p42[0];
        checkLandMark[j * 2 + 1] =   p42[1];
        j++;
        checkLandMark[j * 2] = p43[0];
        checkLandMark[j * 2 + 1] =   p43[1];
        j++;
        checkLandMark[j * 2] = p44[0];
        checkLandMark[j * 2 + 1] =   p44[1];
        j++;
        checkLandMark[j * 2] = p45[0];
        checkLandMark[j * 2 + 1] =   p45[1];
        j++;
        checkLandMark[j * 2] = p46[0];
        checkLandMark[j * 2 + 1] =   p46[1];
        j++;
        checkLandMark[j * 2] = p47[0];
        checkLandMark[j * 2 + 1] =   p47[1];
        j++;

        return checkLandMark;
    }

    @Override
    public void onInit() {
        super.onInit();
        updateTextureCoord();
        initdata();
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
    }

    @Override
    public void onDraw(int textureId, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {
        super.onDraw(textureId, cubeBuffer, textureBuffer);
        GLES20.glUseProgram(mGLProgId);
        runPendingOnDrawTasks();
        if (!isInitialized()) {
            return;
        }
//        onBaseDraw(textureId, cubeBuffer , textureBuffer);
        doonDraw(textureId, cubeBuffer, textureBuffer);

    }

    public void onBaseDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer){
        GLES20.glUseProgram(mProgramId);
        android.opengl.Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniform1f(mIntensityHandle, 0.0f);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);/***向顶点Shader传入变换矩阵**/
        cubeBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 2, GLES20.GL_FLOAT, false, 0, cubeBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
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

    /****
     *
     * @param srcTexture  用户人脸纹理
     * @param cubeBuffer  用户人脸所在图片顶点坐标buffer
     * @param textureBuffer 用户人脸纹理buffer
     *  步骤：
     *   1、遍历取出单张人脸关键Landmark
     *   2、扩展人脸脸颊关键点
     *   3、有人脸关键点坐标计算纹理坐标（除以宽高归一化操作）
     *   4、对扩展后的人脸脸颊关键点进行变形操作
     *   5、根据纹理srcTexture、纹理坐标、扩展后的脸颊顶点坐标进行
     */
    public void doonDraw(int srcTexture, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {

        if(srcTexture == OpenGlUtils.NO_TEXTURE){
            return;
        }

        float[] landMarks = mLandmarks;
        int width = mOutputWidth;
        int height = mOutputHeight;

        int originLength = FULL_COUNT_PER_LANDMARKS;/***单张人脸是83个关键点（不包括主动扩展）**/
        int faceCount = landMarks.length / originLength;/**人脸个数**/
        float[] singleLandMark = new float[landMarks.length];//****//*
        float[] extendLandMark;
        for (int i = 0; i < faceCount; i++) {
            System.arraycopy(landMarks, 0, singleLandMark, 0, landMarks.length);
            extendLandMark =  extendLandMarkWithCheck( singleLandMark );
            float[] vertexCoor = extendLandMark;
            float[] textureCoor = new float[extendLandMark.length ];
            for (int j = 0; j < vertexCoor.length / 2; j++) {
                /***用户人脸纹理坐标是根据顶点坐标计算得到***/
                textureCoor[j * 2] = vertexCoor[j*2] / width;
                textureCoor[j * 2 + 1] = vertexCoor[j*2 +1] / height;
            }
            /**人脸微变形 只对关键点顶点操作即可**/
            drawMesh(srcTexture, width, height, textureCoor, vertexCoor);
        }
    }

    /****
     *
     * @param srcTextureId 用户人脸纹理
     * @param width        照片宽度  1200
     * @param height       照片高度  1600
     * @param textureCoor  用户纹理坐标
     * @param vertexCoor   用户人脸(扩展脸颊)关键点坐标
     *                     步骤：
     *                     1、创建顶点坐标和纹理合并到一个FloatBuffer中
     *                     2、创建顶点索引ShortBuffer.
     *                     3、使用mProgramId
     *                     4、传入变换矩阵
     *                     4、使用顶点句柄、纹理句柄
     *
     */
    public void drawMesh(int srcTextureId, int width, int height, float[] textureCoor, float[] vertexCoor ) {

        if (vertexCoor == null || vertexCoor.length != textureCoor.length) {
            return;
        }

        int pointSize = textureCoor.length / 2; /**纹理坐标点数量**/

        /***下面所以内容围绕：绘制变老纹理（通过顶点坐标 + 纹理坐标 + 顶点索引坐标）****/
        FloatBuffer vertexBuffer = createVertexBuffer(width, height, pointSize, textureCoor, vertexCoor);/***创建人脸顶点坐标 + 变老素材纹理坐标  Buffer**/
        ShortBuffer indexesBuffer =  OpenGlUtils.createShortBuffer(extensionCheekDrawingIndices);/**创建关键点索引ShortBuffer**/
        GLES20.glUniform1f(mIntensityHandle, 1.0f);
        GLES20.glUseProgram(mProgramId);
        android.opengl.Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);/***向顶点Shader传入变换矩阵**/

        /**使能顶点坐标句柄、纹理坐标句柄**/
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, srcTextureId);
        GLES20.glUniform1i(mInputImageTextureHandle, 0);



        /***绑定顶点缓冲区mVertexDataBufferId对象**/
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        /***向顶点缓冲区对象mVertexDataBufferId 传入数据vertexBuffer，方式为写入一次数据，然后绘制若干次**/
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW );

        /***绑定索引数组**/
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);/**静态绘制模式**/

        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0); /**将传递顶点坐标Buffer**/// 顶点数据(X, Y, Z)
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12); /**将传递纹理坐标Buffer**/// 纹理数据

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, extensionCheekDrawingIndices.length, GLES20.GL_UNSIGNED_SHORT, 0);/***使能好纹理坐标后，通过索引的方式进行绘制****/

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);

//        FloatBuffer vertexBuffer2 = createVertexBuffer2(width, height, pointSize, vertexCoor);/***创建人脸顶点坐标 + 变老素材纹理坐标  Buffer**/
//        ShortBuffer indexesBuffer2 =  OpenGlUtils.createShortBuffer(extensionCheekDrawingLineIndices);/**创建关键点索引ShortBuffer**/
//        drawTriangle( vertexBuffer2,  indexesBuffer2);
    }



    /***创建变老人脸素材遮罩 + 人脸顶点坐标Buffer   1、将用户人脸关键点坐标转换为GL坐标，2、将人脸关键点坐标和纹理坐标转化为Buffer**/
    private FloatBuffer createVertexBuffer(int width, int height, int pointSize, float[] textureCoor, float[] vertexCoor) {
        float widthRatio = 1.0f / width;
        float heightRatio = 1.0f / height;
        FloatBuffer vertexBuffer = OpenGlUtils.createFloatBuffer(pointSize * 5);/** 5 = 纹理(x,y)为2  + 顶点 (x,y,0)为3**/
        vertexBuffer.clear();


        int index = 0;
        for (int i = 0; i < pointSize; i++) {
            index = 2 * i;
            float x = vertexCoor[index] * 2 * widthRatio - 1.0f;
            float y = vertexCoor[index + 1] * 2 * heightRatio - 1.0f;

            vertexBuffer.put(x);
            vertexBuffer.put(-y);
            vertexBuffer.put(1.0f);

            /**纹理坐标**/
            vertexBuffer.put(textureCoor[index]);
            vertexBuffer.put(textureCoor[index + 1]);

        }

        vertexBuffer.position(0);
        return vertexBuffer;
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