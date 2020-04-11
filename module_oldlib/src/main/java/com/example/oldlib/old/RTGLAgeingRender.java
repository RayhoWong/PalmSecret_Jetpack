package com.example.oldlib.old;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class RTGLAgeingRender {
    private static final String TAG = "RTGLAgeingRender";
    public static final int LANDMARK_OFFSET_EXTRA_INFO = 4;


    private static final short[] mLandMarkIndex = {
            2, 98, 99, 98, 2, 3, 8, 9, 4, 9, 8, 23, 14, 129, 130,
            129, 14, 13, 3, 2, 7, 24, 8, 25, 8, 24, 23, 16, 11, 12,
            11, 16, 15, 3, 4, 96, 4, 3, 8, 11, 15, 10, 5, 28, 27,
            28, 5, 101, 2, 99, 6, 28, 101, 102, 5, 6, 100, 6, 5, 26,
            7, 2, 6, 7, 6, 26, 8, 3, 7, 8, 7, 25, 22, 9, 23,
            9, 22, 21, 4, 9, 14, 9, 21, 20, 52, 19, 9, 19, 52, 44,
            10, 126, 11, 126, 10, 125, 16, 37, 15, 37, 16, 38, 54, 122, 50,
            122, 54, 121, 15, 37, 36, 12, 127, 128, 127, 12, 11, 12, 128, 13,
            127, 11, 126, 95, 4, 14, 4, 95, 96, 12, 13, 17, 95, 14, 130,
            14, 9, 19, 13, 14, 18, 10, 15, 125, 39, 49, 38, 49, 39, 40,
            51, 123, 36, 123, 51, 122, 12, 17, 16, 13, 18, 17, 16, 17, 39,
            14, 19, 18, 17, 18, 40, 38, 16, 39, 18, 19, 41, 19, 44, 43,
            9, 20, 52, 27, 30, 26, 30, 27, 29, 20, 21, 35, 25, 30, 31,
            30, 25, 26, 21, 22, 34, 24, 31, 32, 31, 24, 25, 22, 23, 33,
            25, 7, 26, 23, 24, 32, 26, 5, 27, 29, 27, 28, 67, 56, 105,
            56, 67, 66, 65, 56, 66, 56, 65, 64, 23, 32, 33, 29, 28, 103,
            30, 104, 31, 104, 30, 103, 30, 29, 103, 56, 31, 104, 31, 56, 32,
            22, 33, 34, 56, 53, 35, 53, 56, 55, 32, 56, 33, 21, 34, 35,
            33, 56, 34, 56, 35, 34, 20, 35, 53, 15, 36, 124, 43, 46, 42,
            46, 43, 45, 57, 120, 121, 120, 57, 119, 41, 47, 48, 47, 41, 42,
            36, 37, 51, 17, 40, 39, 37, 38, 50, 47, 42, 46, 40, 18, 41,
            41, 19, 42, 41, 48, 40, 42, 19, 43, 45, 43, 44, 20, 53, 52,
            54, 44, 53, 44, 54, 45, 53, 44, 52, 40, 48, 49, 45, 54, 46,
            38, 49, 50, 47, 46, 54, 47, 54, 48, 48, 54, 49, 37, 50, 51,
            49, 54, 50, 51, 50, 122, 121, 54, 58, 54, 55, 61, 55, 54, 53,
            57, 121, 58, 60, 54, 61, 54, 60, 59, 58, 59, 85, 59, 58, 54,
            55, 56, 63, 56, 64, 63, 119, 57, 118, 94, 117, 118, 117, 94, 93,
            57, 58, 68, 68, 58, 85, 70, 60, 61, 60, 70, 69, 59, 60, 69,
            61, 55, 62, 59, 69, 85, 62, 55, 63, 71, 62, 72, 62, 71, 61,
            62, 63, 73, 72, 62, 73, 63, 64, 74, 64, 65, 75, 28, 102, 103,
            75, 65, 77, 65, 66, 77, 101, 5, 100, 66, 67, 76, 56, 104, 105,
            106, 67, 105, 67, 106, 107, 57, 68, 94, 70, 61, 71, 84, 93, 94,
            93, 84, 83, 83, 92, 93, 92, 83, 82, 69, 70, 84, 73, 63, 74,
            72, 82, 71, 82, 72, 81, 70, 71, 83, 71, 82, 83, 74, 64, 78,
            72, 73, 80, 64, 75, 78, 74, 79, 73, 79, 74, 78, 77, 66, 76,
            75, 86, 87, 86, 75, 77, 76, 67, 86, 76, 86, 77, 75, 87, 78,
            88, 79, 78, 79, 88, 80, 78, 87, 88, 73, 79, 80, 80, 81, 72,
            81, 80, 90, 81, 91, 82, 91, 81, 90, 70, 83, 84, 90, 80, 89,
            92, 82, 91, 69, 84, 94, 68, 85, 94, 85, 69, 94, 67, 107, 86,
            80, 88, 89, 87, 86, 108, 108, 86, 107, 88, 87, 110, 87, 108, 109,
            89, 88, 110, 110, 87, 109, 90, 89, 112, 89, 110, 111, 91, 90, 114,
            90, 112, 113, 92, 91, 116, 90, 113, 114, 93, 92, 116, 91, 114, 115,
            57, 94, 118, 3, 96, 97, 3, 97, 98, 6, 99, 100, 89, 111, 112,
            110, 109, 111, 91, 115, 116, 93, 116, 117, 116, 115, 117, 36, 123, 124,
            15, 124, 125, 13, 128, 129
    };



//    public static final float[] TEXTURE_COOR_ARRAY = new float[]{
//            645,316,624,288,588,271,532,268,469,287,
//            632,328,611,305,578,292,526,290, 464,324,
//            146,320,176,293,219,278,274,278,336, 285,
//            163,331,191,311,229,300,279,301,333,322,
//            479,396,487,390,499,381,516,372,536,370,
//            553,372,567,376,575,381,584,387,575,393,
//            565,398,552,401,532,402,511,401,496,399,485,398,
//            212,392,219,385,230,378,244,371,262,368,
//            283,371,298,381,312,390,318,398,313,399,
//            301,400,282,403,262,404,247,402,232,400,220,397,
//            406,396,406,546,
//            330,559,405,381,478,562,
//            272,652,281,646,302,644,330,642,363,640,
//            399,648,435,642,472,647,493,652,510,656,517,659,
//            279,655,318,664,343,666,368,669, 397,675,
//            430,672, 459,670,487,670,512,661,
//            506,664,486,671,458,672,430,673,397,676,
//            368,671,343,668,319,666,287,657,
//            509,676,493,690,464,703,436,712,398,715,
//            361,711,336,701,312,690,285,670,
//            410,154,466,155,554,179,609,219,649,257,668,307,680,357,686,418,682,477,673,536,
//            664,601, 644,668,628,718,600,770,564,802,525,862,486,849,439,863,398,866,353,863,
//            304,844,269,823,222,798,177,768,141,715,128,655,114,589,102,524,91,462,91,403,
//            98,343,111,290,152,239,202,199,272,170,352,153
//
//};

    public static final float[] TEXTURE_COOR_ARRAY = new float[]{
            0.80625f, 0.29259259f, 0.78f, 0.26666668f, 0.735f, 0.25092593f, 0.665f, 0.24814814f, 0.58625f, 0.26574075f, 0.79f, 0.3037037f, 0.76375f, 0.2824074f, 0.7225f, 0.27037036f, 0.6575f, 0.2685185f, 0.58f, 0.3f, 0.1825f, 0.2962963f, 0.22f, 0.2712963f, 0.27375f, 0.2574074f, 0.3425f, 0.2574074f, 0.42f, 0.2638889f, 0.20375f, 0.30648148f, 0.23875f, 0.28796297f, 0.28625f, 0.2777778f, 0.34875f, 0.2787037f, 0.41625f, 0.29814816f, 0.59875f, 0.36666667f, 0.60875f, 0.3611111f, 0.62375f, 0.35277778f, 0.645f, 0.34444445f, 0.67f, 0.3425926f, 0.69125f, 0.34444445f, 0.70875f, 0.34814814f, 0.71875f, 0.35277778f, 0.73f, 0.35833332f, 0.71875f, 0.3638889f, 0.70625f, 0.36851853f, 0.69f, 0.3712963f, 0.665f, 0.37222221f, 0.63875f, 0.3712963f, 0.62f, 0.36944443f, 0.60625f, 0.36851853f, 0.265f, 0.36296296f, 0.27375f, 0.3564815f, 0.2875f, 0.35f, 0.305f, 0.34351853f, 0.3275f, 0.34074074f, 0.35375f, 0.34351853f, 0.3725f, 0.35277778f, 0.39f, 0.3611111f, 0.3975f, 0.36851853f, 0.39125f, 0.36944443f, 0.37625f, 0.37037036f, 0.3525f, 0.37314814f, 0.3275f, 0.37407407f, 0.30875f, 0.37222221f, 0.29f, 0.37037036f, 0.275f, 0.3675926f, 0.5075f, 0.36666667f, 0.5075f, 0.50555557f, 0.4125f, 0.5175926f, 0.50625f, 0.5222222f, 0.5975f, 0.52037036f, 0.34f, 0.6037037f, 0.35125f, 0.59814817f, 0.3775f, 0.5962963f, 0.4125f, 0.59444445f, 0.45375f, 0.5925926f, 0.49875f, 0.6f, 0.54375f, 0.59444445f, 0.59f, 0.59907407f, 0.61625f, 0.6037037f, 0.6375f, 0.6074074f, 0.64625f, 0.6101852f, 0.34875f, 0.6064815f, 0.3975f, 0.6148148f, 0.42875f, 0.6166667f, 0.46f, 0.61944443f, 0.49625f, 0.625f, 0.5375f, 0.62222224f, 0.57375f, 0.6203704f, 0.60875f, 0.6203704f, 0.64f, 0.61203706f, 0.6325f, 0.6148148f, 0.6075f, 0.6212963f, 0.5725f, 0.62222224f, 0.5375f, 0.62314814f, 0.49625f, 0.6259259f, 0.46f, 0.6212963f, 0.42875f, 0.61851853f, 0.39875f, 0.6166667f, 0.35875f, 0.60833335f, 0.63625f, 0.6259259f, 0.61625f, 0.6388889f, 0.58f, 0.65092593f, 0.545f, 0.65925926f, 0.4975f, 0.662037f, 0.45125f, 0.65833336f, 0.42f, 0.6490741f, 0.39f, 0.6388889f, 0.35625f, 0.6203704f, 0.5125f, 0.1425926f, 0.5825f, 0.14351852f, 0.6925f, 0.16574074f, 0.76125f, 0.20277777f, 0.81125f, 0.23796296f, 0.835f, 0.28425926f, 0.85f, 0.33055556f, 0.8575f, 0.38703704f, 0.8525f, 0.44166666f, 0.84125f, 0.4962963f, 0.83f, 0.5564815f, 0.805f, 0.61851853f, 0.785f, 0.66481483f, 0.75f, 0.712963f, 0.705f, 0.7425926f, 0.65625f, 0.76111114f, 0.6075f, 0.7861111f, 0.54875f, 0.79907405f, 0.4975f, 0.80185187f, 0.44125f, 0.79907405f, 0.38f, 0.7814815f, 0.33625f, 0.76203704f, 0.2775f, 0.73888886f, 0.22125f, 0.7111111f, 0.17625f, 0.662037f, 0.16f, 0.6064815f, 0.1425f, 0.5453704f, 0.1275f, 0.48518518f, 0.11375f, 0.42777777f, 0.11375f, 0.37314814f, 0.1225f, 0.3175926f, 0.13875f, 0.2685185f, 0.19f, 0.2212963f, 0.2525f, 0.18425927f, 0.34f, 0.1574074f, 0.44f, 0.14166667f};

    private static final String VERTEX_SHADER =
            "attribute highp vec4 position;\n" + // 顶点坐标
                    "attribute highp vec2 textureCoord;\n" + // 纹理坐标
                    "attribute highp vec2 textureCoord2;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "varying highp vec2 textureCoordinate2;\n" +
                    "uniform highp mat4 matrix; \n" +
                    "void main() {\n" +
                    "    gl_Position = matrix * position;\n" +
                    "    textureCoordinate.x = (gl_Position.x + 1.0) / 2.0;\n" + // 顶点坐标转纹理坐标
                    "    textureCoordinate.y = (-gl_Position.y + 1.0) / 2.0;\n" + // 顶点坐标转纹理坐标
                    "    textureCoordinate2 = textureCoord;\n" +
                    "}";
    // OVERLAY_BLEND_FRAGMENT_SHADER
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
                    "     \n" +
                    "     gl_FragColor = base * (overlay.a * (base / base.a) + (2.0 * overlay * (1.0 - (base / base.a)))) + overlay * (1.0 - base.a) + base * (1.0 - overlay.a);\n" +
                    "     gl_FragColor = vec4(mix(gl_FragColor, base, 1.0 - intensity).rgb, 1.0);\n" +
                    "}";

    private int mVertexDataBufferId;
    private int mPointIndicesId;
    private int mProgramId = 0;

    private int mPositionHandle; // 屏幕坐标
    private int mTextureCoordHandle; // 纹理坐标
    private int mMatrixHandle;
    private int mInputImageTextureHandle2;
    private int mIntensityHandle;
    private float mIntensity = 1f;

    public static float[] mMVPMatrix = new float[16];


    public RTGLAgeingRender() {
        init();
    }

    private int init() {
        int programId = OpenGlUtils.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        mPositionHandle = GLES20.glGetAttribLocation(programId, "position");
        mTextureCoordHandle = GLES20.glGetAttribLocation(programId, "textureCoord");
        mMatrixHandle = GLES20.glGetUniformLocation(programId, "matrix");
        mIntensityHandle = GLES20.glGetUniformLocation(programId, "intensity");
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

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glLineWidth(3.0F);
        OpenGlUtils.checkGLError("init");
        return programId;
    }

    private float[] extendLandMark(float[] rawData) {
        float[] faceCenter = getFaceCenter(rawData);
        float[] faceCenterOri = new float[]{faceCenter[0], faceCenter[1]};
        float[] landMarks = new float[rawData.length];

        android.graphics.Matrix matrix = new android.graphics.Matrix();

//         旋转至正脸
        float rollDegree = (float) Math.toDegrees(rawData[44]);
        float pitchDegree = (float) Math.toDegrees(rawData[0]);
        matrix.setRotate(rollDegree, faceCenterOri[0], faceCenterOri[1]);
        matrix.mapPoints(landMarks, rawData);

//        PointF p34 = new PointF(landMarks[4 + 34 * 2], landMarks[4 + 34 * 2 + 1]);
//
//        float[] pointScale = new float[14]; // 70 71 72 64 78 79 80
//        pointScale[0] = landMarks[4 + 70 * 2];
//        pointScale[1] = landMarks[4 + 70 * 2 + 1];
//        pointScale[2] = landMarks[4 + 71 * 2];
//        pointScale[3] = landMarks[4 + 71 * 2 + 1];
//        pointScale[4] = landMarks[4 + 72 * 2];
//        pointScale[5] = landMarks[4 + 72 * 2 + 1];
//
//        pointScale[6] = landMarks[4 + 64 * 2];
//        pointScale[7] = landMarks[4 + 64 * 2 + 1];
//
//        pointScale[8] = landMarks[4 + 78 * 2];
//        pointScale[9] = landMarks[4 + 78 * 2 + 1];
//        pointScale[10] = landMarks[4 + 79 * 2];
//        pointScale[11] = landMarks[4 + 79 * 2 + 1];
//        pointScale[12] = landMarks[4 + 80 * 2];
//        pointScale[13] = landMarks[4 + 80 * 2 + 1];
//
//        final float scaleRatio = 1.8f;
//        matrix.setScale(scaleRatio, scaleRatio, p34.x, p34.y);
//        matrix.mapPoints(pointScale);
//
//        float[] point64 = getPoint(landMarks, 64); // 下巴点
//        float[] point62 = getPoint(landMarks, 62);
//        float[] point63 = getPoint(landMarks, 63);
//        float[] point36 = getPoint(landMarks, 36);
//        float[] point37 = getPoint(landMarks, 37);
//        float[] foreheadCenter = new float[]{((point36[0] + point37[0]) / 2f), faceCenter[1] * 2 - point64[1]};
//        // 调整额头中心点高度
//        if (pitchDegree < 0) {
//            pitchDegree *= 3;
//            double foreheadCenterY = (foreheadCenter[1] - faceCenter[1]) * Math.abs(Math.cos(Math.toRadians(pitchDegree))) + faceCenter[1];
//            foreheadCenter[1] = (float) foreheadCenterY;
//        }
//
//        // 返回1 + 3 * 2个点
//        float[] p1 = getBezierPoint(point62, foreheadCenter, 0.25f);
//        float[] p2 = getBezierPoint(point62, foreheadCenter, 0.5f);
//        float[] p3 = getBezierPoint(point62, foreheadCenter, 0.75f);
//        float[] p4 = getBezierPoint(point63, foreheadCenter, 0.25f);
//        float[] p5 = getBezierPoint(point63, foreheadCenter, 0.5f);
//        float[] p6 = getBezierPoint(point63, foreheadCenter, 0.75f);
//
//        float[] foreheadExtends = new float[]{
//                p1[0], p1[1],
//                p2[0], p2[1],
//                p3[0], p3[1],
//                p4[0], p4[1],
//                p5[0], p5[1],
//                p6[0], p6[1],
//                foreheadCenter[0], foreheadCenter[1]};
//
//        // 合并扩展点
        float[] allPoint = new float[262];
        System.arraycopy(landMarks, 0, allPoint, 0, landMarks.length);
//        System.arraycopy(pointScale, 0, allPoint, landMarks.length, pointScale.length);
//        System.arraycopy(foreheadExtends, 0, allPoint, landMarks.length + pointScale.length, foreheadExtends.length);

        matrix.setRotate(-rollDegree, faceCenterOri[0], faceCenterOri[1]);
        matrix.mapPoints(allPoint, allPoint);
        return allPoint;
    }

    public void draw(int srcTexture, float[] landMarks, int width, int height,
                     int maskTextureId) {
        int originLength = 131 * 2;
        int faceCount = landMarks.length / originLength;
        float[] singleLandMark = new float[originLength];
        float[] extendLandMark;
        for (int i = 0; i < faceCount; i++) {
            System.arraycopy(landMarks, i * originLength, singleLandMark, 0, originLength);
            extendLandMark = extendLandMark(singleLandMark);
            float[] dstPoints = new float[extendLandMark.length];
            float[] srcPoint = new float[extendLandMark.length];
            for (int j = 0; j < dstPoints.length / 2; j++) {
                float[] p = getPointByIndex(extendLandMark, j);
                dstPoints[j * 2] = p[0];
                dstPoints[j * 2 + 1] = p[1];

                srcPoint[j * 2] = TEXTURE_COOR_ARRAY[j * 2];
                srcPoint[j * 2 + 1] = TEXTURE_COOR_ARRAY[j * 2 + 1];
            }
            drawMesh(srcTexture, maskTextureId, width, height, TEXTURE_COOR_ARRAY, dstPoints);
        }
//        drawMesh(srcTexture, maskTextureId, width, height, TEXTURE_COOR_ARRAY, landMarks);
    }


    public void drawMesh(int srcTextureId, int textureId, int width, int height,
                         float[] textureCoor, float[] vertexCoor) {
        if (textureId == OpenGlUtils.NO_TEXTURE || vertexCoor == null ||
                vertexCoor.length != textureCoor.length) {
            return;
        }

        GLES20.glUseProgram(mProgramId);
        Matrix.setIdentityM(mMVPMatrix, 0); // 将mMVPMatrix设置为单位矩阵
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1f(mIntensityHandle, mIntensity);
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

        int pointSize = textureCoor.length / 2;

        FloatBuffer vertexBuffer = createVertexBuffer(width, height, pointSize,
                textureCoor, vertexCoor);
        ShortBuffer indexesBuffer = OpenGlUtils.createShortBuffer(mLandMarkIndex);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, srcTextureId);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(mInputImageTextureHandle2, 5);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexDataBufferId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STREAM_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mPointIndicesId);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexesBuffer.capacity() * 2, indexesBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, 0); // 顶点数据(X, Y, Z)
        GLES20.glVertexAttribPointer(mTextureCoordHandle, 2, GLES20.GL_FLOAT, false, 20, 12); // 纹理数据
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mLandMarkIndex.length, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
        GLES20.glUseProgram(0);
    }

    private FloatBuffer createVertexBuffer(int width, int height,
                                           int pointSize, float[] textureCoor, float[] vertexCoor) {
        float widthRatio = 1.0f / width;
        float heightRatio = 1.0f / height;

        FloatBuffer vertexBuffer = OpenGlUtils.createFloatBuffer(pointSize * 5);
        vertexBuffer.clear();
        int index;
        for (int i = 0; i < pointSize; i++) {
            index = i << 1;
            float x = vertexCoor[index] * widthRatio * 2 - 1.0f;
            float y = vertexCoor[index + 1] * heightRatio * 2 - 1.0F;
            y = -y;

            vertexBuffer.put(x); // 顶点数据 (-1,1)
            vertexBuffer.put(y);
            vertexBuffer.put(1.0F);
            vertexBuffer.put(textureCoor[index]); // 纹理数据(0,1)
            vertexBuffer.put(textureCoor[index + 1]);
        }
        vertexBuffer.position(0);
        return vertexBuffer;
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

    /*辅助函数*/
    public static float[] getBezierPoint(float[] start, float[] end, float t) {
        PointF p1 = new PointF(start[0], (float) ((end[1] + start[1]) * 0.5));
        PointF p2 = new PointF((float) ((end[0] + start[0]) * 0.5), end[1]);
        PointF p0 = new PointF(start[0], start[1]);
        PointF p3 = new PointF(end[0], end[1]);
        float dt = 1 - t;
        float dt2 = dt * dt;
        float dt3 = dt2 * dt;

        float x = p0.x * dt3 + 3 * p1.x * t * dt2 + 3 * p2.x * t * t * dt + p3.x * t * t * t;
        float y = p0.y * dt3 + 3 * p1.y * t * dt2 + 3 * p2.y * t * t * dt + p3.y * t * t * t;

        return new float[]{x, y};
    }


    public float[] getPoint(float[] src, int index) {
        return new float[]{src[LANDMARK_OFFSET_EXTRA_INFO + index * 2], src[LANDMARK_OFFSET_EXTRA_INFO + index * 2 + 1]};
    }

    public float[] getFaceCenter(float[] landmark) {
        // 求人脸的最中心点
//        float[] point36 = getPoint(landmark, 36);
//        float[] point37 = getPoint(landmark, 37);
//        float[] tempCenter = new float[]{(point36[0] + point37[0]) / 2, (point36[1] + point37[1]) / 2}; // 鼻子顶端的中点
//        float[] point34 = getPoint(landmark, 34);
//        float faceCenterX = (tempCenter[0] + point34[0]) / 2;
//        float faceCenterY = (tempCenter[1] + point34[1]) / 2;
        PointF p34 = new PointF((landmark[52 * 2] + landmark[53 * 2]) / 2, (landmark[52 * 2 + 1] + landmark[53 * 2 + 1]) / 2); // 人脸的最中心点


        float[] faceCenter = new float[]{p34.x, p34.y};
        return faceCenter;
    }

    public static float[] getPointByIndex(float[] points, int index) {
        if (points == null) {
            return null;
        }
        return new float[]{points[index * 2], points[index * 2 + 1]};
    }
}
