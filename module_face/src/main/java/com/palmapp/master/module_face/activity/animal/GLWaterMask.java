package com.palmapp.master.module_face.activity.animal;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

import com.palmapp.master.module_face.R;

import java.nio.FloatBuffer;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/30
 * @tips 这个类是Object的子类
 * @fuction
 */
public class GLWaterMask {

    // 顶点着色器的脚本
    static final String VERTEX_SHADER =
            "precision mediump float;                               \n" +
                    "attribute vec4 aPosition;                     \n" +
                    "attribute vec2 aTextureCoordinate;            \n" +
                    "varying vec2 vTextureCoordinate;              \n" +
                    "void main() {                                  \n" +
                    "    vTextureCoordinate = aTextureCoordinate; \n" +
                    "    gl_Position = aPosition;                  \n" +
                    "}";
    // 片元着色器的脚本
    static final String FRAGMENT_SHADER =
            "precision mediump float;                               \n" +
                    "varying vec2 vTextureCoordinate;              \n" +  //从vertex shader中传递过来的一个经过插值的纹理坐标值
                    "uniform sampler2D uTexture;                   \n" +//声明了一个2D的采样器用于采样纹理
                    "void main() {                                  \n" +
                    "     vec4 color=texture2D(uTexture, vTextureCoordinate);\n" +  //从纹理中采样出v_textureCoordinate坐标所对应的颜色作为fragment shader的输出
                    "                             \n" +
                    "    gl_FragColor = color;                       \n" +
                    "}";


    private int programId;

    private Context mContext;

    private static int VERTEX_COMPONENT_COUNT = 2;
    private float verticesData[] = {
            -1f, -1f,
            -1f, 1f,
            1f, 1f,
            -1f, -1f,
            1f, 1f,
            1f, -1f
    };

    private float textureData[] = {
            0f, 1f,
            0f, 0f,
            1f, 0f,
            0f, 1f,
            1f, 0f,
            1f, 1f
    };

    private FloatBuffer verticesBuffer;
    private FloatBuffer texturesBuffer;
    private int aPosition = 0;
    private int aTextureCoordinateLocation = 0;
    private int uTexture = 0;

    private int mInputTexture;
    private float mWidthPercent;
    private float mRatio;
    private float mMarginWidthPercent;

    public GLWaterMask(Context context) {
        super();
        mContext = context;
    }

    public void init(float widthPercent, float ratio, float marginWidthPercent) {
        mWidthPercent = widthPercent;
        mRatio = ratio;
        mMarginWidthPercent = marginWidthPercent;
    }

    public void onCreate() {
        programId = OpenGLUtils.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        verticesData = generateVeticesData(mWidthPercent, mRatio, mMarginWidthPercent);
        verticesBuffer = OpenGLUtils.createFloatBuffer(verticesData);

        texturesBuffer = OpenGLUtils.createFloatBuffer(
                OpenGLUtils.converVerticesDataToTextureData(verticesData, true)
        );
        texturesBuffer = OpenGLUtils.createFloatBuffer(textureData);

        /**
         * 初始化获取着色器中的属性
         */
        // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
        aPosition = GLES20.glGetAttribLocation(programId, "aPosition");

        // 获取字段a_textureCoordinate在shader中的位置
        aTextureCoordinateLocation = GLES20.glGetAttribLocation(programId, "aTextureCoordinate");
        uTexture = GLES20.glGetUniformLocation(programId, "uTexture");

        mInputTexture = OpenGLUtils.createTexture(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.face_pic_water_mask));
    }

    public void onDraw() {
        GLES20.glUseProgram(programId);
//        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES20.glClear()用的颜色值而不是执行清屏)
//        GLES20.glClearColor(1f, 1, 1, 0.0f);
//        // 清除深度缓冲与颜色缓冲(清屏,否则会出现绘制之外的区域花屏)
//        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
//
////        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//////        GLES20.glDepthMask(false);
//        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

//        GLES20.glUniformMatrix4fv(vMatrix, 1, false, SM, 0);

        // 允许顶点位置数据数组
        GLES20.glEnableVertexAttribArray(aPosition);
        // 为画笔指定顶点位置数据(vPosition)
        GLES20.glVertexAttribPointer(aPosition, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, verticesBuffer);

        // 启动对应位置的参数
        GLES20.glEnableVertexAttribArray(aTextureCoordinateLocation);
        // 指定a_textureCoordinate所使用的顶点数据
        GLES20.glVertexAttribPointer(aTextureCoordinateLocation, VERTEX_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, texturesBuffer);

        OpenGLUtils.bindTexture(uTexture, mInputTexture, 4);
        // 绘制
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, verticesData.length / VERTEX_COMPONENT_COUNT);
    }


    private float[] generateVeticesData(float widthPercent, float ratio, float marginWidthPercent) {
        float transX = (1 - widthPercent) * 2;
        float heightPercent = widthPercent / ratio;
        float transY = -((1 - heightPercent) * 2);

        float transMarginX = -2 * marginWidthPercent;
        float transMarginY = -transMarginX;

        float leftTopX = -1f + transX + transMarginX;
        float leftTopY = 1f + transY + transMarginY;
        float leftBottomX = -1f + transX + transMarginX;
        float leftBottomY = -1f + transMarginY;
        float rightTopX = 1f + transMarginX;
        float rightTopY = 1f + transY + transMarginY;
        float rightBottomX = 1f + transMarginX;
        float rightBottomY = -1f + transMarginY;

        return new float[]{
                leftBottomX, leftBottomY,
                leftTopX, leftTopY,
                rightTopX, rightTopY,
                leftBottomX, leftBottomY,
                rightTopX, rightTopY,
                rightBottomX, rightBottomY
        };
    }

}
