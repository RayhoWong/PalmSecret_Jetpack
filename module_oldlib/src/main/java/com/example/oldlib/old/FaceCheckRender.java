package com.example.oldlib.old;

import android.opengl.GLES20;
import android.opengl.Matrix;


import com.example.oldlib.utils.TransformUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static com.example.oldlib.old.StasmFaceDetectionSdk.FULL_COUNT_PER_LANDMARKS;


/**
 * author:lwb
 * date  :2019/4/17 11:20
 * desc  :
 * 人脸 脸颊点进行扩展
 */
public class FaceCheckRender {

    private static final String TAG = "RTGAgeingFaceShadeRende";

    /**人脸脸颊扩展三角剖分，整个下巴区域分成三角形**/
    private static short[] extensionCheekDrawingIndices = {

            43, 39, 44, 48, 49, 17, 48, 31, 32, 31, 48, 1, 10, 20, 18,
            20, 10, 11, 1, 2, 31, 2, 1, 17, 19, 2, 17, 2, 19, 3,
            3, 33, 32, 33, 3, 4, 2, 3, 32, 20, 11, 12, 4, 35, 34,
            35, 4, 5, 4, 3, 19, 34, 35, 30, 5, 4, 21, 4, 19, 21,
            35, 5, 36, 5, 6, 36, 6, 5, 29, 18, 25, 26, 25, 18, 24,
            6, 7, 37, 7, 6, 28, 7, 27, 8, 27, 7, 28, 36, 6, 37,
            7, 8, 38, 0, 8, 26, 8, 0, 38, 20, 12, 13, 48, 17, 1,
            40, 49, 41, 49, 40, 9, 23, 13, 14, 13, 23, 22, 9, 10, 18,
            10, 9, 40, 41, 10, 40, 10, 41, 11, 6, 29, 28, 42, 11, 41,
            11, 42, 12, 21, 17, 29, 17, 21, 19, 43, 13, 12, 13, 43, 44,
            24, 14, 15, 14, 24, 23, 13, 44, 45, 14, 13, 45, 15, 25, 24,
            25, 15, 16, 15, 14, 46, 14, 45, 46, 0, 16, 47, 16, 0, 26,
            16, 15, 47, 17, 49, 18, 17, 18, 26, 9, 18, 49, 5, 21, 29,
            13, 22, 20, 18, 20, 22, 18, 22, 23, 26, 8, 27, 18, 23, 24,
            25, 16, 26, 17, 26, 27, 17, 27, 28, 17, 28, 29, 2, 32, 31,
            34, 30, 48, 48, 32, 33, 48, 33, 34, 33, 4, 34, 37, 7, 38,
            12, 42, 43, 15, 46, 47,


            /* 43, 39, 44, 10, 20, 18, 20, 10, 11, 1, 2, 31, 2, 1, 17,
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
             37, 7, 38, 12, 42, 43, 15, 46, 47*/


//            1,17,19,1,19,2,2,19,3,3,19,21,3,21,4,421,29,4,29,5,5,29,28,5,28,6,6,28,27,6,27,7,7,27,26,7,26,8,8,26,0,0,26,25,0,25,16,16,25,15,15,25,24,15,24,14,14,24,13,13,24,23,13,23,12,12,23,22,12,22,11,11,22,10,10,22,20,10,20,9,9,20,18,
//            1,2,31,2,31,32,2,32,33,32,33,3,33,44,33,34,4,34,5,5,34,30,5,30,35,5,356,6,35,36,6,36,7,7,36,37,3,37,8,8,37,38,8,0,38,
//            9,10,40,10,40,41,10,41,11,11,41,42,11,42,12,12,42,43,12,43,13,13,43,39,13,39,44,1344,14,14,44,45,14,45,15,15,46,16,16,46,47
             };



    private static int[] checkIndexInLandMarkIndex = {
            113,
            121 , 120 , 119 ,118,117,116,115,114,
            105,106,107,108,109,110,111,112,
            54,56,
            57,67,
            94, 86 ,87,88,89,90,91,92,93,
    };

    /***
     * 脸颊变换索引和强度
     * 每三个数一组，（起点索引，终点索引，变形强度）
     */
    private static final short[] checkIndexAndIntensityForTransform1 = new short[]{
            9,37,10,
            10,42,10,
            11,43,10,
            12,39,10,
            13,44,20,
            14,44,20,
            15,45,20,
            16,46,20,
    };
    private static final short[] checkIndexAndIntensityForTransform = new short[]{
            1,37,10,
            2,33,10,
            3,34,10,
            4,30,10,
            5,35,20,
            6,35,20,
            7,36,20,
            8,37,20
    };


    /**辅助测试使用：人脸脸颊扩展三角剖分所有的线**/
    private static short[] extensionCheekDrawingLineIndices ;



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
                    "\n" +
                    "\n" +
                    "void main() {\n" +
                    "     mediump vec4 base = texture2D(inputImageTexture,textureCoordinate);\n" + //提取用户中的纹素
                    "     gl_FragColor = base;\n" +
                    "}";




    private static final String LINE_VERTEX_SHADER =
                    "attribute highp vec4 position;\n" + // 顶点坐标
                    "uniform highp mat4 matrix; \n" +
                    "void main() {\n" +
                    "    gl_Position = matrix * position;\n" +
                    "    gl_PointSize = 20.0;\n" +
                    "}";


    private static final String LINE_FRAGMENT_SHADER =
                    "precision mediump float;\n" +
                    "\n" +
                    "\n" +
                    "void main() {\n" +
                    "     gl_FragColor =  vec4(0.0,1.0,0.0,1.0);\n" + /**绿色**/
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






    /**绘制网格辅助线使用**/
    private int mLineProgramId = 0;

    private int mLinePositionHandle;

    /***变换矩阵句柄***/
    private int mLineMatrixHandle;



    public static float[] mMVPMatrix = new float[16];

    public FaceCheckRender() {
        init();
    }

    private int init() {

        int programId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        mPositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mMatrixHandle = GLES20.glGetUniformLocation(programId, "matrix");
        mTextureCoordHandle = GLES20.glGetAttribLocation(programId, "textureCoord");

        mInputImageTextureHandle = GLES20.glGetUniformLocation(programId, "inputImageTexture");

        if (programId <= 0) {
            throw new RuntimeException("Error creating point drawing program");
        }
        mProgramId = programId;


        int lineProgramId = OpenGlUtils.loadProgram(LINE_VERTEX_SHADER, LINE_FRAGMENT_SHADER);
        if (lineProgramId <= 0) {
            throw new RuntimeException("Error creating point drawing lineProgramId");
        }
        mLineProgramId = lineProgramId;

        mLinePositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mLineMatrixHandle = GLES20.glGetUniformLocation(programId, "matrix");



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

        extensionCheekDrawingLineIndices =   lineIndices(extensionCheekDrawingIndices);

        return programId;
    }

    /**辅助测试使用：三个索引点组成一个三角形，有三条边**/
    private short[] lineIndices(short tringleIndices[]){
        int tringleCount = tringleIndices.length/3;
        short lineIndices[] = new short[tringleCount *6];
        int count = 0;
        for(int i=0;i<tringleCount;i++){
            short a =  tringleIndices[3* i];
            short b =  tringleIndices[3* i+1];
            short c =  tringleIndices[3* i+2];
            lineIndices[count *6 +0] =a;
            lineIndices[count *6 +1] =b;
            lineIndices[count *6 +2] =b;
            lineIndices[count *6 +3] =c;
            lineIndices[count *6 +4] =c;
            lineIndices[count *6 +5] =a;
            count ++;
        }
        return lineIndices;
    }




    private float[] mLandmarks;
    protected int mOutputWidth;
    protected int mOutputHeight;
    private float ageIntensity ;
    private String mName;
    private int mAge ;
    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }
    public void setLandmarks(final float[] landmarks , int age) {
        mLandmarks = landmarks;
        mAge = age;
        if(age == 50){
            ageIntensity = 0 ;
        }else if(age == 70){
            ageIntensity = 0.8f;
        }else if(age == 90) {
            ageIntensity = 1.2f;
        }
    }

    /****
     * 对单张脸进行脸颊扩展
     * @param singleLandMark
     * @return 扩展后的脸颊checkLandMark  这个扩展顺序好像有点问题
     */
    public static float[] extendLandMarkWithCheck(float[] singleLandMark ){
        float[] checkLandMark = new float[50 * 2];

        float[] p0 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, 113);/**扩展人脸关键点数组 （x,y）**/
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


        float[] p48 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, 122);
        float[] p49 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, 104);
        checkLandMark[j * 2] = p48[0];
        checkLandMark[j * 2 + 1] =   p48[1];
        j++;

        checkLandMark[j * 2] = p49[0];
        checkLandMark[j * 2 + 1] =   p49[1];
        j++;

//        checkLandMark[j * 2] = p50[0];
//        checkLandMark[j * 2 + 1] =   p50[1];
//        j++;
//
//        checkLandMark[j * 2] = p51[0];
//        checkLandMark[j * 2 + 1] =   p51[1];
//        j++;

        return checkLandMark;
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
    public void onDraw(int srcTexture, FloatBuffer cubeBuffer, FloatBuffer textureBuffer) {

        if(srcTexture == OpenGlUtils.NO_TEXTURE){
            return;
        }

        onBaseDraw(srcTexture,cubeBuffer, textureBuffer);

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
            transformCheckExtentLandMark(vertexCoor);
            drawMesh(srcTexture, width, height, textureCoor, vertexCoor);
        }
    }



    /**变换扩展脸颊 比如变胖**/
    private  void transformCheckExtentLandMark(float[] checkExtentLandMark){
        if(checkExtentLandMark ==null){
            return;
        }
        for(int i = 0; i< checkIndexAndIntensityForTransform.length; i=i+3){
            int startIndex = checkIndexAndIntensityForTransform[i];
            int endIndex = checkIndexAndIntensityForTransform[i+1];
            float intensity = checkIndexAndIntensityForTransform[i+2] * ageIntensity;
            double[] start = new double[]{checkExtentLandMark[startIndex*2],checkExtentLandMark[startIndex*2 +1]};
            double[] end = new double[]{checkExtentLandMark[endIndex*2],checkExtentLandMark[endIndex*2 +1]};
            float result[] = TransformUtils.transform(start , true ,intensity);
            checkExtentLandMark[startIndex*2] = result[0];
            checkExtentLandMark[startIndex*2 +1]= result[1];
        }
        for(int i = 0; i< checkIndexAndIntensityForTransform1.length; i=i+3){
            int startIndex = checkIndexAndIntensityForTransform1[i];
            int endIndex = checkIndexAndIntensityForTransform1[i+1];
            float intensity = checkIndexAndIntensityForTransform1[i+2] * ageIntensity;
            double[] start = new double[]{checkExtentLandMark[startIndex*2],checkExtentLandMark[startIndex*2 +1]};
            double[] end = new double[]{checkExtentLandMark[endIndex*2],checkExtentLandMark[endIndex*2 +1]};
            float result[] = TransformUtils.transform(start , false ,intensity);
            checkExtentLandMark[startIndex*2] = result[0];
            checkExtentLandMark[startIndex*2 +1]= result[1];
        }
        float bottom = Math.max(checkExtentLandMark[8*2 +1] , checkExtentLandMark[16*2 +1]);
        if(checkExtentLandMark[1] < bottom){
            checkExtentLandMark[1] = bottom ;
        }
    }


    public void onBaseDraw(final int textureId, final FloatBuffer cubeBuffer, final FloatBuffer textureBuffer){
        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
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

        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
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


    private void drawTriangle(FloatBuffer vertexBuffer, ShortBuffer indexesBuffer){


        GLES20.glUseProgram(mLineProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mLineMatrixHandle, 1, false, mMVPMatrix, 0);/***向顶点Shader传入变换矩阵**/


        GLES20.glLineWidth(3.0f);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);/***绑定顶点缓冲区mVertexDataBufferId对象**/
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW );/***向顶点缓冲区对象mVertexDataBufferId 传入数据vertexBuffer，方式为写入一次数据，然后绘制若干次**/

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);/***绑定索引数组**/
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);/**静态绘制模式**/

        GLES20.glVertexAttribPointer(mLinePositionHandle, 3, GLES20.GL_FLOAT, false, 0, 0); /**将传递顶点坐标Buffer**/// 顶点数据(X, Y, Z)
        GLES20.glEnableVertexAttribArray(mLinePositionHandle);
        GLES20.glDrawElements(GLES20.GL_LINES, extensionCheekDrawingLineIndices.length, GLES20.GL_UNSIGNED_SHORT, 0);/***使能好纹理坐标后，通过索引的方式进行绘制****/

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mLinePositionHandle);
        GLES20.glUseProgram(0);

    }










    public void release() {
        if (mProgramId > 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
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

    /***创建变老人脸素材遮罩 + 人脸顶点坐标Buffer   1、将用户人脸关键点坐标转换为GL坐标，2、将人脸关键点坐标和纹理坐标转化为Buffer**/
    private FloatBuffer createVertexBuffer2(int width, int height, int pointSize, float[] vertexCoor) {
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
        }

        vertexBuffer.position(0);
        return vertexBuffer;
    }


}
