package com.palmapp.master.module_face.activity.animal;


public abstract class GLRenderer {
    private static final String TAG = "GLThread";

    /**
     * 单位矩阵
     */
    public static final float[] OM= MatrixUtils.getOriginalMatrix();

    protected int mHeight = 0;
    protected int mWidth = 0;

    public GLRenderer() {
    }

    public void onSurfaceChanged(int width, int height) {
        mWidth=width;
        mHeight=height;
    }

    /**
     * 当创建完基本的OpenGL环境后调用此方法，可以在这里初始化纹理之类的东西
     */
    public abstract void onCreated();

    /**
     * 在渲染之前调用，用于更新纹理数据。渲染一帧调用一次
     */
    protected void onUpdate() {
    }

    /**
     * 绘制渲染，每次绘制都会调用，一帧数据可能调用多次(不同是输出缓存)
     * @param glSurface
     */
    public abstract void onDrawFrame(GLSurface glSurface);

    /**
     * 当渲染器销毁前调用，用户回收释放资源
     */
    public void onDestroy() {
    }
}