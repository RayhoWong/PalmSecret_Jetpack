package com.palmapp.master.module_face.activity.animal;

import android.opengl.EGL14;
import android.opengl.GLUtils;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/23
 * @tips 这个类是Object的子类
 * @fuction
 */
public class GLRenderController extends AbsGLThread {

    private IEGLEnvironment mEglEnvironment;

    private GLRenderer mRenderer;

    private int mHeight = 0;
    private int mWidth = 0;


    public GLRenderer getRenderer() {
        return mRenderer;
    }

    public GLRenderController(IEGLEnvironment glEnvironment, GLRenderer renderer) {
        super();
        mEglEnvironment = glEnvironment;
        mRenderer = renderer;

    }

    /**
     * 渲染到各个eglSurface
     */
    public void render() {
        // 渲染(绘制)
        mEglEnvironment.render(mRenderer);
    }

//    /**
//     * 绑定Surface
//     *
//     * @param width
//     * @param height
//     * @param windowSurface
//     */
//    public void bindSurface(final int width, final int height, @Nullable final Surface windowSurface) {
//        postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                mWidth = width;
//                mHeight = height;
//                if (mEglEnvironment != null) {
//                    mEglEnvironment.bindSurface(width, height, windowSurface);
//                }
//                if (mRenderer != null) {
//                    mRenderer.onSurfaceChanged(width, height);
//                }
//            }
//        });
//    }

    private static String getEGLErrorString() {
        return "getEGLErrorString";
    }

    @Override
    protected void onRender() {
        if (mRenderer != null) {
            mRenderer.onUpdate();
        }

        render(); // 如果surface缓存没有释放(被消费)那么这里将卡住

    }

    @Override
    protected void onCreate() {
        if (mEglEnvironment != null) {
            mEglEnvironment.initialize();
        }
        if (mRenderer != null) {
            mRenderer.onCreated();
        }
    }

    @Override
    protected void onRelease() {
        // 回调
        if (mRenderer != null) {
            mRenderer.onDestroy();
        }
        // 销毁eglSurface
        if (mEglEnvironment != null) {
            mEglEnvironment.release();
        }
    }

    @Override
    protected void onAddSurface(GLSurface surface) {
        super.onAddSurface(surface);
        mEglEnvironment.addSurface(surface);
        if (mRenderer != null) {
            mRenderer.onSurfaceChanged(surface.viewport.width, surface.viewport.height);
        }
    }

    @Override
    protected void onRemoveSurface(GLSurface surface) {
        super.onRemoveSurface(surface);
        mEglEnvironment.removeSurace(surface);
    }
}
