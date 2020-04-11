package com.palmapp.master.module_face.activity.animal;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES30;


import java.nio.FloatBuffer;
import java.util.LinkedList;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/23
 * @tips 这个类是Object的子类
 * @fuction
 */
public class AbsFrameBuffer {

    protected String TAG = getClass().getSimpleName();

    protected Context mContext;

    private final LinkedList<Runnable> mRunOnDraw;

    // 纹理字符串
    protected String mVertexShader;
    protected String mFragmentShader;

    // 是否初始化成功
    protected boolean mIsInitialized;

    // 句柄
    protected int mProgramHandle;
    protected int mPositionHandle;
    protected int mMatrix = 0;
    protected int mTextureCoordinateHandle;
    protected int mInputTextureHandle;

    // 渲染的Image的宽高
    protected int mImageWidth;
    protected int mImageHeight;

    // 显示输出的宽高
    protected int mDisplayWidth;
    protected int mDisplayHeight;

    // FBO的宽高，可能跟输入的纹理大小不一致
    protected int mFrameWidth = -1;
    protected int mFrameHeight = -1;

    // 每个顶点坐标有几个参数
    protected int mCoordsPerVertex = TextureRotationUtils.CoordsPerVertex;
    // 顶点坐标数量
    protected int mVertexCount = TextureRotationUtils.CubeVertices.length / mCoordsPerVertex;


    // FBO
    protected int[] mFrameBuffers;
    protected int[] mFrameBufferTextures;

    //用于绘制到屏幕上的变换矩阵
    private float[] SM = new float[16];

    protected FloatBuffer mVertexBuffer;
    protected FloatBuffer mTextureBuffer;

    protected boolean mIsMatrix = true;

    protected float[] mClearColor = new float[]{1f, 1f, 1f, 1f};

    public AbsFrameBuffer(Context context, String vertexShader, String fragmentShader) {
        mContext = context;
        mRunOnDraw = new LinkedList<>();
        // 记录shader数据
        mVertexShader = vertexShader;
        mFragmentShader = fragmentShader;
        // 只有在shader都不为空的情况下才初始化程序句柄
        mProgramHandle = OpenGLUtils.createProgram(mVertexShader, mFragmentShader);
        // 初始化程序句柄
        initProgramHandle(mProgramHandle);
        mIsInitialized = true;
    }

    /**
     * 初始化程序句柄
     *
     * @param programHandle ProgramId
     */
    protected void initProgramHandle(int programHandle) {
        mPositionHandle = GLES30.glGetAttribLocation(programHandle, "aPosition");
        mMatrix = GLES30.glGetUniformLocation(programHandle, "vMatrix");
        mTextureCoordinateHandle = GLES30.glGetAttribLocation(programHandle, "aTextureCoordinate");
        mInputTextureHandle = GLES30.glGetUniformLocation(programHandle, "uTexture");
    }

    /**
     * 创建FBO
     *
     * @param width
     * @param height
     */
    public void initFrameBuffer(int width, int height) {
        if (!mIsInitialized) {
            return;
        }
        if (mFrameBuffers != null && (mFrameWidth != width || mFrameHeight != height)) {
            destroyFrameBuffer();
        }
        if (mFrameBuffers == null) {
            mFrameWidth = width;
            mFrameHeight = height;
            mFrameBuffers = new int[1];
            mFrameBufferTextures = new int[1];
            OpenGLUtils.createFrameBuffer(mFrameBuffers, mFrameBufferTextures, width, height);
        }
    }

    public void initMatrix(int imgWidth, int imgHeight, int width, int height) {
        if (mIsMatrix) {
            MatrixUtils.getMatrix(SM, MatrixUtils.TYPE_CENTERCROP, imgWidth, imgHeight, width, height);
        }
    }


    /**
     * 绘制到FBO
     *
     * @param textureId
     * @param vertexBuffer
     * @param textureBuffer
     * @return FBO绑定的Texture
     */
    public int drawFrameBuffer(int textureId, int textureIndex, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        // 没有FBO、没初始化、输入纹理不合法、滤镜不可用时，直接返回
        if (textureId == OpenGLUtils.GL_NOT_TEXTURE || mFrameBuffers == null
                || !mIsInitialized) {
            return textureId;
        }

        // 绑定FBO
        GLES30.glViewport(0, 0, mFrameWidth, mFrameHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFrameBuffers[0]);
        // 使用当前的program
        GLES30.glUseProgram(mProgramHandle);
        // 设置clear color颜色RGBA(这里仅仅是设置清屏时GLES30.glClear()用的颜色值而不是执行清屏)
        GLES30.glClearColor(mClearColor[0], mClearColor[1], mClearColor[2], mClearColor[3]);
        // 清除深度缓冲与颜色缓冲(清屏,否则会出现绘制之外的区域花屏)
        GLES30.glClear(GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        // 运行延时任务，这个要放在glUseProgram之后，要不然某些设置项会不生效
        runPendingOnDrawTasks();

        if (mIsMatrix) {
            GLES30.glUniformMatrix4fv(mMatrix, 1, false, SM, 0);
        }

        // 绘制纹理
        onDrawTexture(textureId, textureIndex, vertexBuffer, textureBuffer);

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    /**
     * 绘制
     *
     * @param textureId
     * @param vertexBuffer
     * @param textureBuffer
     */
    protected void onDrawTexture(int textureId, int index, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        // 绑定顶点坐标缓冲
        vertexBuffer.position(0);
        GLES30.glVertexAttribPointer(mPositionHandle, mCoordsPerVertex,
                GLES30.GL_FLOAT, false, 0, vertexBuffer);
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        // 绑定纹理坐标缓冲
        textureBuffer.position(0);
        GLES30.glVertexAttribPointer(mTextureCoordinateHandle, 2,
                GLES30.GL_FLOAT, false, 0, textureBuffer);
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandle);
        // 绑定纹理
        OpenGLUtils.bindTexture(mInputTextureHandle, textureId, index);
//        OpenGLUtils.bindTexture(mTextureCoordinateHandle, mInputTextureHandle, index);
        onDrawFrame();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, mVertexCount);
        // 解绑
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTextureCoordinateHandle);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        GLES30.glUseProgram(0);
    }


    /**
     * 绘制动态设置参数等
     */
    protected void onDrawFrame() {

    }

    public void setVertexCount(int vertexCount) {
        this.mVertexCount = vertexCount;
    }

    public int getCoordsPerVertex() {
        return mCoordsPerVertex;
    }

    /**
     * 添加延时任务
     *
     * @param runnable
     */
    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.addLast(runnable);
        }
    }

    /**
     * 运行延时任务
     */
    protected void runPendingOnDrawTasks() {
        while (!mRunOnDraw.isEmpty()) {
            mRunOnDraw.removeFirst().run();
        }
    }

    /**
     * 释放资源
     * 建议：在Surface Destory的时候调用
     */
    public void release() {
        if (mIsInitialized) {
            GLES30.glDeleteProgram(mProgramHandle);
            mProgramHandle = OpenGLUtils.GL_NOT_INIT;
        }
        destroyFrameBuffer();
    }

    /**
     * 销毁纹理
     */
    public void destroyFrameBuffer() {
        if (!mIsInitialized) {
            return;
        }
        if (mFrameBufferTextures != null) {
            GLES30.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }

        if (mFrameBuffers != null) {
            GLES30.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
        mFrameWidth = -1;
        mFrameHeight = -1;
    }

    ///------------------ 统一变量(uniform)设置 ------------------------///
    protected void setInteger(final int location, final int intValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES30.glUniform1i(location, intValue);
            }
        });
    }

    protected void setFloat(final int location, final float floatValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES30.glUniform1f(location, floatValue);
            }
        });
    }

    protected void setFloatVec2(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES30.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec3(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES30.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatVec4(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES30.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setFloatArray(final int location, final float[] arrayValue) {
        runOnDraw(new Runnable() {
            @Override
            public void run() {
                GLES30.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES30.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES30.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }

    public void setImageWidth(int imageWidth) {
        mImageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        mImageHeight = imageHeight;
    }

    public int getImageWidth() {
        return mImageWidth;
    }

    public int getImageHeight() {
        return mImageHeight;
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return mTextureBuffer;
    }

    public void setVertexBuffer(FloatBuffer vertexBuffer) {
        this.mVertexBuffer = vertexBuffer;
    }

    public void setTextureBuffer(FloatBuffer textureBuffer) {
        this.mTextureBuffer = textureBuffer;
    }
}
