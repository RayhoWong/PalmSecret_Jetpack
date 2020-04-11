package com.example.oldlib.old;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;


import com.example.oldlib.utils.TransformUtils;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * author:lwb
 * date  :2019/4/17 11:20
 * desc  :
 * 人脸 脸颊点进行扩展
 */
public class EyeLipCheckRender {

    private static final String TAG = "RTGAgeingFaceShadeRende";

    /**人脸脸颊扩展三角剖分，整个下巴区域分成三角形**/
  /*  private static short[] extensionCheekDrawingIndices = new short[] {
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
            106, 84, 85, 94, 71, 95, 104, 73, 105};*/

    private static short[] extensionCheekDrawingIndices = new short[] {
            0, 101, 102, 101, 0, 100, 28, 5, 102, 5, 28, 27, 2, 99, 1,
            99, 2, 98, 3, 97, 2, 97, 3, 96, 0, 1, 100, 1, 0, 5,
            1, 5, 6, 2, 1, 6, 130, 13, 129, 13, 130, 14, 3, 2, 7,
            24, 8, 7, 8, 24, 23, 16, 11, 12, 11, 16, 15, 3, 4, 96,
            4, 3, 8, 9, 8, 22, 8, 9, 4, 7, 8, 3, 5, 0, 102,
            6, 5, 27, 7, 2, 6, 7, 6, 25, 32, 24, 31, 24, 32, 23,
            21, 9, 22, 9, 21, 20, 4, 9, 19, 52, 19, 9, 19, 52, 44,
            11, 15, 10, 10, 126, 11, 126, 10, 125, 16, 37, 15, 37, 16, 38,
            95, 4, 14, 4, 95, 96, 11, 126, 127, 12, 11, 127, 12, 13, 17,
            13, 12, 129, 95, 14, 130, 15, 37, 36, 13, 14, 18, 14, 4, 19,
            10, 15, 124, 19, 18, 14, 18, 19, 42, 49, 122, 50, 122, 49, 54,
            12, 17, 16, 13, 18, 17, 16, 17, 39, 17, 18, 40, 38, 16, 39,
            43, 46, 42, 46, 43, 45, 19, 44, 43, 9, 20, 52, 25, 24, 7,
            24, 25, 31, 20, 21, 35, 22, 8, 23, 21, 22, 34, 22, 23, 33,
            25, 6, 26, 31, 25, 30, 26, 30, 25, 30, 26, 29, 26, 6, 27,
            29, 26, 27, 29, 27, 28, 56, 66, 65, 66, 56, 67, 56, 65, 64,
            23, 32, 33, 29, 28, 103, 30, 104, 31, 104, 30, 103, 30, 29, 103,
            56, 31, 104, 31, 56, 32, 22, 33, 34, 56, 53, 35, 53, 56, 55,
            32, 56, 33, 34, 35, 21, 35, 34, 56, 35, 53, 20, 34, 33, 56,
            48, 40, 41, 40, 48, 49, 20, 53, 52, 57, 120, 54, 120, 57, 119,
            15, 36, 124, 48, 41, 47, 36, 37, 51, 47, 41, 42, 37, 38, 51,
            39, 17, 40, 40, 18, 41, 38, 39, 50, 42, 46, 47, 39, 40, 49,
            41, 18, 42, 42, 19, 43, 45, 43, 44, 52, 53, 44, 45, 54, 46,
            54, 45, 53, 44, 53, 45, 39, 49, 50, 46, 54, 47, 38, 50, 51,
            48, 47, 54, 48, 54, 49, 54, 121, 122, 121, 54, 120, 36, 51, 123,
            51, 50, 123, 54, 55, 61, 55, 54, 53, 119, 57, 118, 60, 54, 61,
            54, 60, 59, 60, 69, 85, 69, 60, 70, 55, 56, 63, 67, 56, 106,
            56, 64, 63, 118, 57, 94, 57, 54, 58, 94, 117, 118, 117, 94, 93,
            57, 58, 68, 58, 54, 59, 68, 58, 59, 60, 85, 59, 61, 55, 62,
            84, 69, 83, 69, 84, 85, 60, 61, 70, 62, 55, 63, 72, 62, 73,
            62, 72, 71, 61, 62, 71, 73, 62, 63, 77, 76, 86, 76, 77, 65,
            63, 64, 74, 64, 65, 75, 103, 28, 102, 82, 71, 72, 71, 82, 83,
            65, 66, 76, 67, 106, 107, 66, 67, 76, 106, 56, 105, 57, 68, 94,
            68, 59, 85, 61, 71, 70, 70, 83, 69, 83, 70, 71, 73, 63, 74,
            72, 81, 82, 81, 72, 73, 81, 73, 80, 77, 86, 87, 74, 64, 75,
            74, 79, 80, 79, 74, 75, 73, 74, 80, 77, 75, 65, 75, 77, 78,
            76, 67, 86, 75, 78, 79, 78, 77, 87, 79, 78, 88, 78, 87, 88,
            80, 79, 89, 79, 88, 89, 81, 80, 90, 80, 89, 90, 81, 90, 91,
            82, 81, 91, 83, 82, 92, 82, 91, 92, 84, 83, 92, 68, 85, 94,
            85, 84, 93, 94, 85, 93, 67, 107, 108, 86, 67, 109, 87, 86, 109,
            109, 67, 108, 88, 87, 110, 87, 109, 110, 89, 88, 111, 88, 110, 111,
            84, 92, 93, 90, 89, 112, 89, 111, 112, 91, 90, 114, 90, 112, 113,
            92, 91, 115, 114, 90, 113, 93, 92, 116, 115, 91, 114, 92, 115, 116,
            128, 12, 127, 12, 128, 129, 2, 97, 98, 1, 99, 100, 56, 104, 105,
            93, 116, 117, 50, 122, 123, 36, 123, 124, 10, 124, 125 };
    /**脸颊索引对应着face++人脸关键点的索引
     * 如人脸的0号点对应这个face++
     * 脸颊扩张点索引0到20号是在人脸关键点landmarks内的
     * 比如
     * 0-->64
     * 20->60
     * 21->61
     **/
    private static int[] checkIndexInLandMarkIndex = {
            64,
            67,68,69,70,71,72,
            75,76,77,78,79,80,
            40,41,
            44,45,
            55,
            58,59,
            60,61
    };

    /***
     * 脸颊变换索引和强度
     * 每三个数一组，（起点索引，终点索引，变形强度）
     */
    private static final short[] checkIndexAndIntensityForTransform = new short[]{
            2,24,1,
            3,25,1,
            4,22,1,
            5,26,1,
            6,28,1,
            8,31,1,
            9,32,1,
            10,33,1,
            11,34,1,
            12,35,1
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
                    "    gl_PointSize = 10.0;\n" +
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

    public EyeLipCheckRender() {
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
    private float ageIntensity = 0;
    private int mAge ;
    private float mEyeRotation;
    private float mMouthLip ;
    public void onOutputSizeChanged(final int width, final int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }
    public void setLandmarks(final float[] landmarks , int age , int eye , int mouth) {
        mLandmarks = landmarks;
        mAge = age ;
        if(age == 50){
            eye = 0 ;
            mouth = 0 ;
        }else if(age == 70){
            eye = 40 ;
            mouth = 8 ;
        }else if(age == 90) {
            eye = 80 ;
            mouth = 16 ;
        }
        float eyes = eye/100f *20 ;
        float mouths = mouth/100f * 2 ;
        mEyeRotation = eyes ;
        mMouthLip = mouths ;
    }

    private static int[] LEFTEYEPOINT = {  36 ,37  ,38 , 39 , 40 ,} ;
    private static int[] RIGHTEYEPOINT = {24 , 25 , 26 , 27 , 28 } ;

    private float[] processEyeRotationWarp(float[] landmarks, float angle) {

        float point1[] =  StasmFaceDetectionSdk.getPointByIndex(landmarks, 37);
        float point2[] =  StasmFaceDetectionSdk.getPointByIndex(landmarks, 45);
        float point10[] =  StasmFaceDetectionSdk.getPointByIndex(landmarks, 35);
        float point11[] =  StasmFaceDetectionSdk.getPointByIndex(landmarks, 27);

        float[] centerleft = {(point1[0] + point2[0]) / 2, (point1[1] + point2[1]) / 2};
        float[] centerright = {(point10[0] + point11[0]) / 2, (point10[1] + point11[1]) / 2};

        for (int i = 0; i < LEFTEYEPOINT.length; i++) {
            float point[] = StasmFaceDetectionSdk.getPointByIndex(landmarks, LEFTEYEPOINT[i]);
            float result[] = TransformUtils.rotate(point , centerleft , -angle);
            StasmFaceDetectionSdk.resetPointByIndex(landmarks , LEFTEYEPOINT[i] , result);
        }
        for (int i = 0; i < RIGHTEYEPOINT.length; i++) {
            float point[] = StasmFaceDetectionSdk.getPointByIndex(landmarks, RIGHTEYEPOINT[i]);
            float result[] = TransformUtils.rotate(point , centerright , angle);
            StasmFaceDetectionSdk.resetPointByIndex(landmarks ,RIGHTEYEPOINT[i] , result);
        }
        return landmarks;
    }

    private static final int[] TARGET_POINT_INDEXES_ARRAY = new int[]{
            68, 69, 70, 71, 72,
            76, 77, 78, 79, 80,
    };
    public static final float[] SRC_CONTROL_POINTS_ARRAY = new float[]
            {
                    363, 970, 402, 1041, 448, 1107, 502, 1168, 567, 1219,
                    962, 966, 923, 1039, 874, 1106, 815, 1167, 747, 1219,

            };
    public static final float[] DIS_CONTROL_POINTS_ARRAY = new float[]
            {
                    364, 975, 394, 1052, 431, 1137, 495, 1199, 567, 1227,
                    962, 971, 932, 1050, 890, 1136, 822, 1198, 745, 1227,
            };
    private float[] processFaceShapeWarp(float[] landmarks) {
        for (int k = 0; k < TARGET_POINT_INDEXES_ARRAY.length; k++) {
            int index = TARGET_POINT_INDEXES_ARRAY[k];
            float[] pointData = StasmFaceDetectionSdk.getPointByIndex(landmarks, index);
            float[] demosrcPoints = {SRC_CONTROL_POINTS_ARRAY[k*2] , SRC_CONTROL_POINTS_ARRAY[k*2+1]};
            float[] demodisPoints = {DIS_CONTROL_POINTS_ARRAY[k*2] , DIS_CONTROL_POINTS_ARRAY[k*2+1]};
            float result[] = new float[2];
            result[0] =   (demodisPoints[0] - demosrcPoints[0]) + pointData[0];
            result[1] =     (demodisPoints[1] - demosrcPoints[1] ) +pointData[1];
            StasmFaceDetectionSdk.resetPointByIndex(landmarks , index , result);
        }
        return landmarks;
    }

    private void resetLipWarp(float[] landmarks  , float[] startf , float[] endf , float intensity  , int index) {
//        float lenght = TransformUtils.getPointLenght(start  , end) ;
        double[] start = new double[]{startf[0],startf[1]};
        double[] end = new double[]{endf[0] , endf[1]};
        float result[] = TransformUtils.transform(start , end , intensity);
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , index , result);
    }

    private void resetLipWarp(float[] landmarks  , float[] startf , float[] endf , float[] startf1 , float intensity  , int index) {
//        float lenght = TransformUtils.getPointLenght(start  , end) ;
        double[] start = new double[]{startf[0],startf[1]};
        double[] end = new double[]{endf[0] , endf[1]};
        double[] start1 = new double[]{startf1[0],startf1[1]};
        float result[] = TransformUtils.transform(start , end , start1 , intensity);
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , index , result);
    }


    private float[] processMouthCornerWarp(float[] landmarks  , float angle){
        float[] point47 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 47 );
        float[] point54 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 54 );
        float[] centerleft = {(point47[0] + point54[0]) / 2, (point47[1] + point54[1]) / 2};

        float[] point44 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 44 );
        float[] point45 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 45 );

        float result[] = TransformUtils.rotate(point44 , centerleft , angle);
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 44, result);

        result = TransformUtils.rotate(point45 , centerleft , -angle);
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 45, result);
        return landmarks;
    }

    // 嘴唇厚度
    private float[] processLipWarp(float[] landmarks  , float intensity) {
        float[] point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 58 );
        float[] point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 68 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 58);

         point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 59 );
         point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 69 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 59);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 60 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 70 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 60);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 61 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 71 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 61);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 62 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 72 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 62);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 63 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 73 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 63);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 64 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 74 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 64);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 65 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 75 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 65);

        point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 66 );
        point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 76 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 66);

        float[] point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 77 );
        float[] point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 86 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 86);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 78 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 87 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 87);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 79 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 88 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 88);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 80 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 89 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 89);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 81 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 90 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 90);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 82 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 91 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 91);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 83 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 92 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 92);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 84 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 93 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 93);

        point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 85 );
        point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 94 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 94);


       /* float[] point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 50 );
        float[] point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 52 );
        resetLipWarp(landmarks , point50 , point52 , intensity , 50);

        float[] point51 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 51 );
        float[] point53 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 53 );

        resetLipWarp(landmarks , point51 , point53 , intensity , 51);


        float[] point46 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 46 );
        float[] point47 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 47 );
        resetLipWarp(landmarks , point46 , point47 , intensity , 46);

        float[] point48 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 48 );
        float[] point49 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 49 );
        resetLipWarp(landmarks , point46 , point47 , point48 ,  intensity , 48);
        resetLipWarp(landmarks , point46 , point47 , point49 , intensity , 49);

        float[] point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 56 );
        float[] point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 58 );
        resetLipWarp(landmarks , point58 , point56 , intensity , 58);

        float[] point57 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 57 );
        float[] point61 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 61 );
        resetLipWarp(landmarks , point61 , point57 , intensity , 61);
        float[] point54 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 54 );
        float[] point55 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 55 );
        resetLipWarp(landmarks , point55 , point54 , intensity , 55);

        float[] point561 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 56 );
        float[] point541 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 54 );
        float[] point59 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 59 );
        float[]  middleFor56And54 =   {(point561[0] + point541[0]) / 2, (point561[1] + point541[1]) / 2};
        resetLipWarp(landmarks , point59 , middleFor56And54 , intensity , 59);
        float[] point571 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 57 );
        float[] point60 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 60 );
        float[]  middleFor57And54 =   {(point571[0] + point541[0]) / 2, (point571[1] + point541[1]) / 2};
        resetLipWarp(landmarks , point60 , middleFor57And54 , intensity , 60);*/

        return landmarks;
    }

    /*  // 嘴唇厚度
    private float[] processLipWarp(float[] landmarks  , float intensity) {
        float[] point50 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 50 );
        float[] point52 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 52 );
        float lenght = TransformUtils.getPointLenght(point50  , point52) ;
        float result[] = new float[2];
        result[0] = point50[0] - intensity * lenght ;
        result[1] = point50[1] - intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 50 , result);

        float[] point51 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 51 );
        float[] point53 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 53 );
        lenght = TransformUtils.getPointLenght(point51  , point53) ;
        result[0] = point51[0] + intensity * lenght ;
        result[1] = point51[1] - intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 51 , result);



        float[] point46 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 46 );
        float[] point47 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 47 );
        lenght = TransformUtils.getPointLenght(point46  , point47) ;
        result[0] = point46[0] ;
        result[1] = point46[1] - intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 46 , result);

        float[] point48 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 48 );
        result[0] = point48[0] - intensity * lenght ;
        result[1] = point48[1] - intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 48 , result);

        float[] point49 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 49 );
        result[0] = point49[0] - intensity * lenght ;
        result[1] = point49[1] - intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 49 , result);



        float[] point56 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 56 );
        float[] point58 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 58 );
        lenght = TransformUtils.getPointLenght(point56  , point58) ;
        result[0] = point58[0] - intensity * lenght ;
        result[1] = point58[1] + intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 58 , result);


        float[] point57 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 57 );
        float[] point61 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 61 );
        lenght = TransformUtils.getPointLenght(point57  , point61) ;
        result[0] = point61[0] + intensity * lenght ;
        result[1] = point61[1] + intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 61 , result);

        float[] point54 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 54 );
        float[] point55 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 55 );
        lenght = TransformUtils.getPointLenght(point54  , point55) ;
        result[0] = point55[0]  ;
        result[1] = point55[1] + intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 55 , result);


        float[] point561 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 56 );
        float[] point541 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 54 );
        float[] point59 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 59 );
        float[]  middleFor56And54 =   {(point561[0] + point541[0]) / 2, (point561[1] + point541[1]) / 2};
        lenght = TransformUtils.getPointLenght(point59  , middleFor56And54) ;
        result[0] = point59[0] - intensity * lenght ;
        result[1] = point59[1] + intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 59 , result);

        float[] point571 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 57 );
        float[] point60 = StasmFaceDetectionSdk.getPointByIndex(landmarks , 60 );
        float[]  middleFor57And54 =   {(point571[0] + point541[0]) / 2, (point571[1] + point541[1]) / 2};
        lenght = TransformUtils.getPointLenght(point60  , middleFor57And54) ;
        result[0] = point60[0] + intensity * lenght ;
        result[1] = point60[1] + intensity * lenght ;
        StasmFaceDetectionSdk.resetPointByIndex(landmarks , 60 , result);


        return landmarks;
    }*/




    /****
     * 对单张脸进行脸颊扩展
     * @param singleLandMark
     * @return 扩展后的脸颊checkLandMark  这个扩展顺序好像有点问题
     */
    private float[] extendLandMarkWithCheck(float[] singleLandMark ){
        float[] faceCenter = StasmFaceDetectionSdk.getFaceCenter(singleLandMark);
        float[] faceCenterOri = new float[]{faceCenter[0],faceCenter[1]};
        float[] landMarks = new float[singleLandMark.length];

        android.graphics.Matrix matrix = new android.graphics.Matrix();

        float rollDegree = (float)Math.toDegrees(singleLandMark[2]);//左右倾斜
        Log.i(TAG, "extendLandMarkWithCheck: rollDegree =" + rollDegree);

        float pitchDegree = (float)Math.toDegrees(singleLandMark[0]);//前后点头
        Log.i(TAG, "extendLandMarkWithCheck: pitchDegree =" + pitchDegree);

        matrix.setRotate(rollDegree,faceCenterOri[0],faceCenterOri[1]);//转正
        matrix.mapPoints(landMarks,singleLandMark);//转正之后的人脸关键点landMarks

        /****转正之后的人脸关键点landMarks****/
        singleLandMark = landMarks;


        float[] checkLandMark = new float[36 * 2];

        float[] p0 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, checkIndexInLandMarkIndex[0]);/**扩展人脸关键点数组 （x,y）**/
        float[] p1= StasmFaceDetectionSdk.getPointByIndex(singleLandMark, checkIndexInLandMarkIndex[1]);/**扩展人脸关键点数组 （x,y）**/
        float[] p7 = StasmFaceDetectionSdk.getPointByIndex(singleLandMark, checkIndexInLandMarkIndex[7]);/**扩展人脸关键点数组 （x,y）**/

        /**求过p0垂直p0p7的垂足**/
        float[] perpendicularOfLineP0 = TransformUtils.perpendicular(p1,p7,p0) ;
        float[] p22 = TransformUtils.parallelAndEquilongEndPoint(perpendicularOfLineP0,p0,p1);
        float[] p29 = TransformUtils.parallelAndEquilongEndPoint(perpendicularOfLineP0,p0,p7);

        float detalX = (p22[0] - p1[0])/4.0f;
        float detalY = (p22[1] - p1[1])/4.0f;
        float[] p23 = new float[] {p1[0] + detalX, p1[1] + detalY};
        float[] p24 = new float[] {p23[0] + detalX, p23[1] + detalY};
        float[] p25 = new float[] {p24[0] + detalX, p24[1] + detalY};

        float[] p30 = new float[] {p7[0] + detalX, p7[1] + detalY};
        float[] p31 = new float[] {p30[0] + detalX, p30[1] + detalY};
        float[] p32 = new float[] {p31[0] + detalX, p31[1] + detalY};

        detalX = (p29[0] - p22[0])/8.0f;
        detalY = (p29[1] - p22[1])/8.0f;

        float[] p26 = new float[] {p22[0] + detalX, p22[1] + detalY};
        float[] p27 = new float[] {p26[0] + detalX, p26[1] + detalY};
        float[] p28 = new float[] {p27[0] + detalX, p27[1] + detalY};

        float[] p33 = new float[] {p29[0] - detalX, p29[1] - detalY};
        float[] p34 = new float[] {p33[0] - detalX, p33[1] - detalY};
        float[] p35 = new float[] {p34[0] - detalX, p34[1] - detalY};
        int j=0;
        for(; j< checkIndexInLandMarkIndex.length; j++){
            float pp[]  =  StasmFaceDetectionSdk.getPointByIndex(singleLandMark, checkIndexInLandMarkIndex[j]);
            checkLandMark[j * 2]  = pp[0];
            checkLandMark[j * 2 +1 ]  = pp[1];

        }

        checkLandMark[j * 2] = p22[0];
        checkLandMark[j * 2 + 1] =   p22[1];
        j++;

        checkLandMark[j * 2] = p23[0];
        checkLandMark[j * 2 + 1] =   p23[1];
        j++;

        checkLandMark[j * 2] = p24[0];
        checkLandMark[j * 2 + 1] =   p24[1];
        j++;

        checkLandMark[j * 2] = p25[0];
        checkLandMark[j * 2 + 1] =   p25[1];
        j++;

        checkLandMark[j * 2] = p26[0];
        checkLandMark[j * 2 + 1] =   p26[1];
        j++;

        checkLandMark[j * 2] = p27[0];
        checkLandMark[j * 2 + 1] =   p27[1];
        j++;

        checkLandMark[j * 2] = p28[0];
        checkLandMark[j * 2 + 1] =   p28[1];
        j++;

        checkLandMark[j * 2] = p29[0];
        checkLandMark[j * 2 + 1] =   p29[1];
        j++;

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

        matrix.setRotate(-rollDegree, faceCenterOri[0], faceCenterOri[1]);
        matrix.mapPoints(checkLandMark, checkLandMark);

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

        int faceCount = 1;//**人脸个数**//*
        float[] singleLandMark = new float[landMarks.length];//****//*
        float[] extendLandMark;
        for (int i = 0; i < faceCount; i++) {
            System.arraycopy(landMarks, 0, singleLandMark, 0, landMarks.length);
            extendLandMark =  processEyeRotationWarp(singleLandMark , mEyeRotation);
            extendLandMark = processLipWarp(extendLandMark , mMouthLip);
//            extendLandMark = processMouthCornerWarp(extendLandMark , 5f);
//            extendLandMark = processFaceShapeWarp(extendLandMark);
            float[] vertexCoor = landMarks;
            float[] textureCoor = new float[extendLandMark.length ];
            for (int j = 0; j < vertexCoor.length / 2; j++) {
                //***用户人脸纹理坐标是根据顶点坐标计算得到***//*
                textureCoor[j * 2] = vertexCoor[j*2] / width;
                textureCoor[j * 2 + 1] = vertexCoor[j*2 +1] / height;
            }
            //**人脸微变形 只对关键点顶点操作即可**//*
//            transformCheckExtentLandMark(extendLandMark);
            drawMesh(srcTexture, width, height, textureCoor, extendLandMark);
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
            float intensity = checkIndexAndIntensityForTransform[i+2]/4.0f * ageIntensity;
            double[] start = new double[]{checkExtentLandMark[startIndex*2],checkExtentLandMark[startIndex*2 +1]};
            double[] end = new double[]{checkExtentLandMark[endIndex*2],checkExtentLandMark[endIndex*2 +1]};
            float result[] = TransformUtils.transform(start ,end ,intensity);
            checkExtentLandMark[startIndex*2] = result[0];
            checkExtentLandMark[startIndex*2 +1]= result[1];
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

        int pointSize = vertexCoor.length/2; /**纹理坐标点数量**/

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
//        FloatBuffer vertexBuffer1 = createVertexBuffer1(width, height, pointSize, textureCoor, vertexCoor);
//        drawTriangle(vertexBuffer1 ,pointSize);

//        FloatBuffer vertexBuffer2 = createVertexBuffer2(width, height, pointSize, vertexCoor);/***创建人脸顶点坐标 + 变老素材纹理坐标  Buffer**/
//        ShortBuffer indexesBuffer2 =  OpenGlUtils.createShortBuffer(extensionCheekDrawingLineIndices);/**创建关键点索引ShortBuffer**/
//        drawTriangle( vertexBuffer2,  indexesBuffer2);
    }


    private void drawTriangle(FloatBuffer vertexBuffer, int mPointSize){


        GLES20.glUseProgram(mLineProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mLineMatrixHandle, 1, false, mMVPMatrix, 0);/***向顶点Shader传入变换矩阵**/


        GLES20.glLineWidth(1.0f);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);/***绑定顶点缓冲区mVertexDataBufferId对象**/
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW );/***向顶点缓冲区对象mVertexDataBufferId 传入数据vertexBuffer，方式为写入一次数据，然后绘制若干次**/

//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);/***绑定索引数组**/
//        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);/**静态绘制模式**/

        GLES20.glVertexAttribPointer(mLinePositionHandle, 3, GLES20.GL_FLOAT, false, 0, 0); /**将传递顶点坐标Buffer**/// 顶点数据(X, Y, Z)
        GLES20.glEnableVertexAttribArray(mLinePositionHandle);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0 , mPointSize * 3);/***使能好纹理坐标后，通过索引的方式进行绘制****/

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mLinePositionHandle);
        GLES20.glUseProgram(0);

    }

//    private void drawTriangle(FloatBuffer vertexBuffer, ShortBuffer indexesBuffer){
//
//
//        GLES20.glUseProgram(mLineProgramId);
//        android.opengl.Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
//        GLES20.glUniformMatrix4fv(mLineMatrixHandle, 1, false, mMVPMatrix, 0);/***向顶点Shader传入变换矩阵**/
//
//
//        GLES20.glLineWidth(3.0f);
//
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);/***绑定顶点缓冲区mVertexDataBufferId对象**/
//        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW );/***向顶点缓冲区对象mVertexDataBufferId 传入数据vertexBuffer，方式为写入一次数据，然后绘制若干次**/
//
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);/***绑定索引数组**/
//        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);/**静态绘制模式**/
//
//        GLES20.glVertexAttribPointer(mLinePositionHandle, 3, GLES20.GL_FLOAT, false, 0, 0); /**将传递顶点坐标Buffer**/// 顶点数据(X, Y, Z)
//        GLES20.glEnableVertexAttribArray(mLinePositionHandle);
//        GLES20.glDrawElements(GLES20.GL_LINES, extensionCheekDrawingLineIndices.length, GLES20.GL_UNSIGNED_SHORT, 0);/***使能好纹理坐标后，通过索引的方式进行绘制****/
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
//
//        GLES20.glDisable(GLES20.GL_BLEND);
//        GLES20.glDisableVertexAttribArray(mLinePositionHandle);
//        GLES20.glUseProgram(0);
//
//    }










    public void release() {
        if (mProgramId > 0) {
            GLES20.glDeleteProgram(mProgramId);
            mProgramId = 0;
        }
    }

    private FloatBuffer createVertexBuffer1(int width, int height, int pointSize, float[] textureCoor, float[] vertexCoor) {
        FloatBuffer vertexBuffer1 = OpenGlUtils.createFloatBuffer(pointSize * 3);;
        for (int i = 0; i < 1; i++) {

            float[] landMarksBuffer = vertexCoor;
            float[] texPositions = textureCoor;
            for (int i1 = 0; i1 < pointSize; i1++) { // 关键点转化为GL顶点坐标
                float x = 2.0F * landMarksBuffer[ (2 * i1)] / width - 1.0f;
                float y = 2.0F * landMarksBuffer[ 2 * i1 + 1] / height - 1.0F;

                vertexBuffer1.put(x);
                vertexBuffer1.put(-y);
                vertexBuffer1.put(1.0f);

            }
            vertexBuffer1.position(0);

        }

        return vertexBuffer1;
    }

    private FloatBuffer mBuffer;
    private float[] mLandMarksBuffer;
    private float[] mFaceLandmarkBuffer;
    /***创建变老人脸素材遮罩 + 人脸顶点坐标Buffer   1、将用户人脸关键点坐标转换为GL坐标，2、将人脸关键点坐标和纹理坐标转化为Buffer**/
    private FloatBuffer createVertexBuffer(int width, int height, int pointSize, float[] textureCoor, float[] vertexCoor) {
        mBuffer = FloatBuffer.allocate(vertexCoor.length);
        mFaceLandmarkBuffer = new float[vertexCoor.length];
        mLandMarksBuffer = new float[mFaceLandmarkBuffer.length /*+ mExtraFaceData.length*/];
        int faceCount = 1;
        FloatBuffer vertexBuffer = OpenGlUtils.createFloatBuffer(pointSize * 5);
        for (int i = 0; i < faceCount; i++) {
            mBuffer.position(0);
            mBuffer.put(vertexCoor, 0, vertexCoor.length);
            mBuffer.position(0);
            mBuffer.get(mFaceLandmarkBuffer);

            System.arraycopy(mFaceLandmarkBuffer, 0, mLandMarksBuffer, 0, mFaceLandmarkBuffer.length);

            float[] landMarksBuffer = mLandMarksBuffer;
            float[] texPositions = textureCoor;
            FloatBuffer vertexBuffer1 = OpenGlUtils.createFloatBuffer(pointSize * 3);;
            vertexBuffer.clear();
            for (int i1 = 0; i1 < pointSize; i1++) { // 关键点转化为GL顶点坐标
                float x = 2.0F * landMarksBuffer[ (2 * i1)] / width - 1.0f;
                float y = 2.0F * landMarksBuffer[ 2 * i1 + 1] / height - 1.0F;
                vertexBuffer.put(x);
                vertexBuffer.put(-y);
                vertexBuffer.put(1.0F);

                vertexBuffer1.put(x);
                vertexBuffer1.put(-y);
                vertexBuffer1.put(1.0f);

                vertexBuffer.put(texPositions[ (2 * i1)]);
                vertexBuffer.put(texPositions[ 2 * i1 + 1]);
            }
            vertexBuffer.position(0);
            vertexBuffer1.position(0);

        }


//        float widthRatio = 1.0f / width;
//        float heightRatio = 1.0f / height;
//        FloatBuffer vertexBuffer = OpenGlUtils.createFloatBuffer(pointSize * 5);/** 5 = 纹理(x,y)为2  + 顶点 (x,y,0)为3**/
//        vertexBuffer.clear();
//
//
//        int index = 0;
//        for (int i = 0; i < pointSize; i++) {
//            index = 2 * i;
//            float x = vertexCoor[4 + (2 * i)] * 2 * widthRatio - 1.0f;
//            float y = vertexCoor[4 + 2 * i + 1] * 2 * heightRatio - 1.0f;
//
//            vertexBuffer.put(x);
//            vertexBuffer.put(-y);
//            vertexBuffer.put(1.0f);
//
//            /**纹理坐标**/
//            vertexBuffer.put(textureCoor[index]);
//            vertexBuffer.put(textureCoor[index + 1]);
//
//        }
//
//        vertexBuffer.position(0);
        return vertexBuffer;
    }




}
