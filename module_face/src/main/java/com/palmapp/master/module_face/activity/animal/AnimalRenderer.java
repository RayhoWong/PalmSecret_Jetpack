package com.palmapp.master.module_face.activity.animal;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;

import com.picstudio.photoeditorplus.stasm.MlsUtils;

import java.nio.FloatBuffer;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/24
 * @tips 这个类是Object的子类
 * @fuction
 */
public class AnimalRenderer extends GLRenderer {

    private int programId;

    private Context mContext;
    private int areaCount = 30;

    private static int VERTEX_COMPONENT_COUNT = 2;
    private float verticesData[] = {
            -1f, -1f,
            -1f, 1f,
            1f, 1f,
            -1f, -1f,
            1f, 1f,
            1f, -1f
    };


    private FloatBuffer verticesBuffer;
    private int aPosition = 0;
    private FloatBuffer texturesBuffer1;
    private int aTextureCoordinateLocation1 = 0;
    private int uTexture1 = 0;

    private FloatBuffer texturesBuffer2;
    private int aTextureCoordinateLocation2 = 0;
    private int uTexture2 = 0;

    private int mInputTexture1;
    private int mInputTexture2;

    private float mProgress = 0.5f;
    private int uProgress = 0;

    private int vMatrix = 0;
    //用于绘制到屏幕上的变换矩阵
    private float[] SM = new float[16];

    private TransFrameBuffer srcTransFrameBuffer;

    private TransFrameBuffer targetTransFrameBuffer;

    private final GLWaterMask mGLWaterMask;

    public AnimalRenderer(Context context) {
        super();
        mContext = context;
        mGLWaterMask = new GLWaterMask(mContext);
        mGLWaterMask.init(0.25f, 540f / 205f, 0.04f);
    }

    @Override
    public void onCreated() {
        programId = OpenGLUtils.createProgram(
                OpenGLUtils.getShaderFromAssets(mContext, "shader/animal_render.vert"),
                OpenGLUtils.getShaderFromAssets(mContext, "shader/animal_render.frag"));
        verticesBuffer = OpenGLUtils.createFloatBuffer(verticesData);

        texturesBuffer1 = OpenGLUtils.createFloatBuffer(
                OpenGLUtils.converVerticesDataToTextureData(verticesData, false)
        );
        texturesBuffer2 = OpenGLUtils.createFloatBuffer(
                OpenGLUtils.converVerticesDataToTextureData(verticesData, false)
        );

        /**
         * 初始化获取着色器中的属性
         */
        // 获取着色器中的属性引用id(传入的字符串就是我们着色器脚本中的属性名)
        aPosition = GLES30.glGetAttribLocation(programId, "aPosition");
        vMatrix = GLES30.glGetUniformLocation(programId, "vMatrix");

        // 获取字段a_textureCoordinate在shader中的位置
        aTextureCoordinateLocation1 = GLES30.glGetAttribLocation(programId, "aTextureCoordinate1");
        uTexture1 = GLES30.glGetUniformLocation(programId, "uTexture1");

        aTextureCoordinateLocation2 = GLES30.glGetAttribLocation(programId, "aTextureCoordinate2");
        uTexture2 = GLES30.glGetUniformLocation(programId, "uTexture2");

        uProgress = GLES30.glGetUniformLocation(programId, "uProgress");

        mGLWaterMask.onCreate();
    }

    public void initSrcFrameBuffer(Bitmap src, float[] srcControlPoints) {
        if (src != null && mWidth != 0 && mHeight != 0) {
            mInputTexture1 = OpenGLUtils.createTexture(src);
            srcTransFrameBuffer = new TransFrameBuffer(mContext);
            float[] srcMeshPoints = OpenGLUtils.createMeshPoints(src.getWidth(), src.getHeight(), areaCount);
            srcTransFrameBuffer.setAreaCount(areaCount);
            srcTransFrameBuffer.setSrcControlPoints(srcControlPoints);
            srcTransFrameBuffer.setSrcMesh(srcMeshPoints, src.getWidth(), src.getHeight());
            onSizeChangeFrameBuffer(srcTransFrameBuffer);
        }
    }

    public void initTargetFrameBuffer(Bitmap target, float[] targetControlPoints) {
        if (target != null && mWidth != 0 && mHeight != 0) {
            mInputTexture2 = OpenGLUtils.createTexture(target);
            targetTransFrameBuffer = new TransFrameBuffer(mContext);
            float[] targetMeshPoints = OpenGLUtils.createMeshPoints(target.getWidth(), target.getHeight(), areaCount);
            targetTransFrameBuffer.setAreaCount(areaCount);
            targetTransFrameBuffer.setSrcControlPoints(targetControlPoints);
            targetTransFrameBuffer.setSrcMesh(targetMeshPoints, target.getWidth(), target.getHeight());
            onSizeChangeFrameBuffer(targetTransFrameBuffer);

            initTargetMesh();
        }
    }

    private void initTargetMesh() {
        if (srcTransFrameBuffer.getTargetMeshPoints() == null) {
            float[] srcTargetMeshPoints = MlsUtils.mlsWithRigid(mInputTexture1,
                    srcTransFrameBuffer.getSrcMeshPoints(),
                    srcTransFrameBuffer.getSrcControlPoints(), targetTransFrameBuffer.getSrcControlPoints(), false);
            srcTransFrameBuffer.setTargetMeshPoints(srcTargetMeshPoints);
        }

        if (targetTransFrameBuffer.getTargetMeshPoints() == null) {
            float[] targetTargetMeshPoints = MlsUtils.mlsWithRigid(mInputTexture2,
                    targetTransFrameBuffer.getSrcMeshPoints(),
                    targetTransFrameBuffer.getSrcControlPoints(), srcTransFrameBuffer.getSrcControlPoints(), false);
            targetTransFrameBuffer.setTargetMeshPoints(targetTargetMeshPoints);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width, height);
        MatrixUtils.getMatrix(SM, MatrixUtils.TYPE_CENTERCROP, mWidth, mHeight, mWidth, mHeight);
        GLES30.glViewport(0, 0, width, height);

        onSizeChangeFrameBuffer(srcTransFrameBuffer);
        onSizeChangeFrameBuffer(targetTransFrameBuffer);
    }

    private void onSizeChangeFrameBuffer(AbsFrameBuffer absFrameBuffer) {
        if (mWidth != 0 && mHeight != 0) {
            if (absFrameBuffer != null) {
                absFrameBuffer.initFrameBuffer(mWidth, mHeight);
                absFrameBuffer.initMatrix(absFrameBuffer.getImageWidth(),
                        absFrameBuffer.getImageHeight(),
                        mWidth, mHeight);
            }
        }
    }

    @Override
    public void onDrawFrame(GLSurface glSurface) {
        if (mWidth == 0 || mHeight == 0) {
            clearBg();
            GLES30.glUseProgram(programId);
            // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES30.glClear()用的颜色值而不是执行清屏)
            GLES30.glClearColor(1f, 1, 1, 1.0f);
            // 清除深度缓冲与颜色缓冲(清屏,否则会出现绘制之外的区域花屏)
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            // 允许顶点位置数据数组
            GLES30.glEnableVertexAttribArray(aPosition);
            // 为画笔指定顶点位置数据(vPosition)
            GLES30.glVertexAttribPointer(aPosition, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, verticesBuffer);

            // 绘制
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, verticesData.length / VERTEX_COMPONENT_COUNT);
            return;
        }
        if (srcTransFrameBuffer == null || targetTransFrameBuffer == null) {
            clearBg();
            GLES30.glUseProgram(programId);
            // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES30.glClear()用的颜色值而不是执行清屏)
            GLES30.glClearColor(1f, 1, 1, 1.0f);
            // 清除深度缓冲与颜色缓冲(清屏,否则会出现绘制之外的区域花屏)
            GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
            // 允许顶点位置数据数组
            GLES30.glEnableVertexAttribArray(aPosition);
            // 为画笔指定顶点位置数据(vPosition)
            GLES30.glVertexAttribPointer(aPosition, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, verticesBuffer);

            // 绘制
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, verticesData.length / VERTEX_COMPONENT_COUNT);
            return;
        }

        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES30.glClear()用的颜色值而不是执行清屏)
        clearBg();
        int srcFrameBufferTexture = -1;
        int targetFrameBufferTexture = -1;

        if (srcTransFrameBuffer.getCurrentMeshPoints() == null) {
            srcTransFrameBuffer.setCurrentMeshPoints(new float[srcTransFrameBuffer.getSrcMeshPoints().length]);
        }

        if (targetTransFrameBuffer.getCurrentMeshPoints() == null) {
            targetTransFrameBuffer.setCurrentMeshPoints(new float[targetTransFrameBuffer.getSrcMeshPoints().length]);
        }
        initTargetMesh();
        for (int i = 0; i < srcTransFrameBuffer.getSrcMeshPoints().length; i++) {
            float srcTargetMeshPoint = srcTransFrameBuffer.getTargetMeshPoints()[i];
            float srcSrcMeshPoint = srcTransFrameBuffer.getSrcMeshPoints()[i];
            float srcDiff = srcTargetMeshPoint - srcSrcMeshPoint;
            float targetTargetMeshPoint = targetTransFrameBuffer.getTargetMeshPoints()[i];
            float targetSrcMeshPoint = targetTransFrameBuffer.getSrcMeshPoints()[i];
            float targetDiff = targetTargetMeshPoint - targetSrcMeshPoint;

            srcTransFrameBuffer.getCurrentMeshPoints()[i] = srcSrcMeshPoint + srcDiff * mProgress;
            targetTransFrameBuffer.getCurrentMeshPoints()[i] = targetSrcMeshPoint + targetDiff * (1 - mProgress);
        }

        srcTransFrameBuffer.setVertex();
        targetTransFrameBuffer.setVertex();

        /**
         * 动态计算网格信息会需要太长时间，造成卡顿的现象
         */
//            float[] currentControlPoints = calculateCurrentControlPoints(
//                    srcTransFrameBuffer.getSrcControlPoints(),
//                    targetTransFrameBuffer.getSrcControlPoints());
//
//            Duration.setStart("MlsUtils");
//            float[] srcCurrentMeshPoints = MlsUtils.mlsWithRigid(mInputTexture1,
//                    srcTransFrameBuffer.getSrcMeshPoints(),
//                    srcTransFrameBuffer.getSrcControlPoints(), currentControlPoints, false);
//            srcTransFrameBuffer.setTargetMeshPoints(srcCurrentMeshPoints);
//
//            float[] targetCurrentMeshPoints = MlsUtils.mlsWithRigid(mInputTexture2,
//                    targetTransFrameBuffer.getSrcMeshPoints(),
//                    targetTransFrameBuffer.getSrcControlPoints(), currentControlPoints, false);
//            targetTransFrameBuffer.setTargetMeshPoints(targetCurrentMeshPoints);
//            Duration.logDuration("MlsUtils");
//            Duration.clear("MlsUtils");

        srcFrameBufferTexture = srcTransFrameBuffer.drawFrameBuffer(mInputTexture1, 0,
                srcTransFrameBuffer.getVertexBuffer(),
                srcTransFrameBuffer.getTextureBuffer());
        targetFrameBufferTexture = targetTransFrameBuffer.drawFrameBuffer(mInputTexture2, 1,
                targetTransFrameBuffer.getVertexBuffer(),
                targetTransFrameBuffer.getTextureBuffer());

        GLES30.glUseProgram(programId);
        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES30.glClear()用的颜色值而不是执行清屏)
        GLES30.glClearColor(1f, 1, 1, 1.0f);
        // 清除深度缓冲与颜色缓冲(清屏,否则会出现绘制之外的区域花屏)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

//        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
////        GLES30.glDepthMask(false);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        GLES30.glUniformMatrix4fv(vMatrix, 1, false, SM, 0);

        // 允许顶点位置数据数组
        GLES30.glEnableVertexAttribArray(aPosition);
        // 为画笔指定顶点位置数据(vPosition)
        GLES30.glVertexAttribPointer(aPosition, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, verticesBuffer);

        // 启动对应位置的参数
        GLES30.glEnableVertexAttribArray(aTextureCoordinateLocation1);
        // 指定a_textureCoordinate所使用的顶点数据
        GLES30.glVertexAttribPointer(aTextureCoordinateLocation1, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, texturesBuffer1);

        // 启动对应位置的参数
        GLES30.glEnableVertexAttribArray(aTextureCoordinateLocation2);
        // 指定a_textureCoordinate所使用的顶点数据
        GLES30.glVertexAttribPointer(aTextureCoordinateLocation2, VERTEX_COMPONENT_COUNT, GLES30.GL_FLOAT, false, 0, texturesBuffer2);

        if (srcFrameBufferTexture != 0) {
            OpenGLUtils.bindTexture(uTexture1, srcFrameBufferTexture, 0);
        }
        if (targetFrameBufferTexture != 0) {
            OpenGLUtils.bindTexture(uTexture2, targetFrameBufferTexture, 1);
        } else {
            OpenGLUtils.bindTexture(uTexture2, srcFrameBufferTexture, 1);
        }

        GLES30.glUniform1f(uProgress, mProgress);
        // 绘制
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, verticesData.length / VERTEX_COMPONENT_COUNT);

        // 解绑
        GLES30.glDisableVertexAttribArray(aPosition);
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation1);
        GLES30.glDisableVertexAttribArray(aTextureCoordinateLocation2);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 1);
        GLES30.glUseProgram(0);

        mGLWaterMask.onDraw();
    }

    private void clearBg() {
        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES30.glClear()用的颜色值而不是执行清屏)
        GLES30.glClearColor(1f, 1, 1, 1.0f);
        // 清除深度缓冲与颜色缓冲(清屏,否则会出现绘制之外的区域花屏)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

    }

    private float[] calculateCurrentControlPoints(float[] srcControlPoints, float[] targetControlPoints) {
        float[] result = new float[srcControlPoints.length];
        for (int i = 0; i < srcControlPoints.length; i++) {
            float diff = targetControlPoints[i] - srcControlPoints[i];
            result[i] = srcControlPoints[i] + diff * mProgress;
        }
        return result;
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }
}
