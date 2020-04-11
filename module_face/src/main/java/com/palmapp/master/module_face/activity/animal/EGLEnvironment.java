package com.palmapp.master.module_face.activity.animal;

import android.opengl.EGL14;
import android.opengl.GLES30;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Create by Billin on 2019/7/26
 */
public class EGLEnvironment implements IEGLEnvironment {

    private int type = 0;

    private static final String TAG = "EGLEnvironment";

    private EGL10 egl;

    private EGLDisplay eglDisplay;

    private EGLConfig eglConfig;

    private EGLContext eglContext;


    private final List<GLSurface> outputSurfaces;

    private int[] tmpInt = new int[1];

    public EGLEnvironment() {
        outputSurfaces = new ArrayList<>();
    }

    @Override
    public void initialize() {
        egl = (EGL10) EGLContext.getEGL();

        /*
         * Get to the default display.
         */
        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        /*
         * We can now initialize EGL for that display
         */
        int[] version = new int[2];
        if (!egl.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }

        eglConfig = chooseConfig(egl, eglDisplay);
        eglContext = createContext(egl, eglDisplay, eglConfig);

        if (eglContext == null || eglContext == EGL10.EGL_NO_CONTEXT) {
            eglContext = null;
            throw new RuntimeException("createContext failed");
        }

        initCurrent(createPbufferSurface(100,100));
    }

    @Override
    public void initCurrent(EGLSurface surface) {
        egl.eglMakeCurrent(eglDisplay, surface, surface, eglContext);
        for (GLSurface outputSurface : outputSurfaces) {
            outputSurface.eglSurface = null;
        }
    }

    private EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
        int[] attrList = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            attrList = new int[]{
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL10.EGL_NONE
            };
        }

        return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attrList);
    }

    private void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
        if (!egl.eglDestroyContext(display, context)) {
            throw new RuntimeException("destroyContext failed");
        }
    }

    private void destroySurface() {
        egl.eglMakeCurrent(eglDisplay,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);

        for (GLSurface outputSurface : outputSurfaces) {
            egl.eglDestroySurface(eglDisplay, outputSurface.eglSurface);
            outputSurface.eglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    private EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        int redSize = 8;
        int greenSize = 8;
        int blueSize = 8;
        int alphaSize = 8;
        int depthSize = 16;
        int stencilSize = 0;

        int[] numConfigs = new int[1];
        int[] configSpec = {
                EGL10.EGL_RED_SIZE, redSize,
                EGL10.EGL_GREEN_SIZE, greenSize,
                EGL10.EGL_BLUE_SIZE, blueSize,
                EGL10.EGL_ALPHA_SIZE, alphaSize,
                EGL10.EGL_DEPTH_SIZE, depthSize,
                EGL10.EGL_STENCIL_SIZE, stencilSize,
                EGL10.EGL_NONE
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            int len = configSpec.length;
            int[] newConfigAttributes = new int[len + 2];
            System.arraycopy(configSpec, 0, newConfigAttributes, 0, len);
            newConfigAttributes[len - 1] = EGL10.EGL_RENDERABLE_TYPE;
            newConfigAttributes[len] = EGL14.EGL_OPENGL_ES2_BIT;
            newConfigAttributes[len + 1] = EGL10.EGL_NONE;
            configSpec = newConfigAttributes;
        }

        // 获取该设备 OpenGL 支持的配置并检测返回值是否成功
        if (!egl.eglChooseConfig(display, configSpec, null, 0, numConfigs)) {
            throw new RuntimeException("eglChooseConfig failed");
        }
        // 该设备不支持设定 spec 配置
        if (numConfigs[0] < 0) {
            throw new RuntimeException("Unable to find any matching EGL config");
        }

        // 获取支持的配置列表并选择其中一个配置
        EGLConfig[] configs = new EGLConfig[numConfigs[0]];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs[0], numConfigs)) {
            throw new IllegalArgumentException("eglChooseConfig#2 failed");
        }

        for (EGLConfig config : configs) {
            int d = findConfigAttr(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttr(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
            if ((d >= depthSize) && (s >= stencilSize)) {
                int r = findConfigAttr(egl, display, config, EGL10.EGL_RED_SIZE, 0);
                int g = findConfigAttr(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
                int b = findConfigAttr(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
                int a = findConfigAttr(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);
                if ((r == redSize) && (g == greenSize) && (b == blueSize) && (a == alphaSize)) {
                    return config;
                }
            }
        }

        return null;
    }

    private int findConfigAttr(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
        if (egl.eglGetConfigAttrib(display, config, attribute, tmpInt)) {
            return tmpInt[0];
        }
        return defaultValue;
    }

    @Override
    public int swap() {
        for (GLSurface outputSurface : outputSurfaces) {
            if (!egl.eglSwapBuffers(eglDisplay, outputSurface.eglSurface)) {
                return egl.eglGetError();
            }
        }

        return EGL10.EGL_SUCCESS;
    }

    @Override
    public void release() {
        destroySurface();
        if (eglContext != null) {
            destroyContext(egl, eglDisplay, eglContext);
            eglContext = null;
        }

        if (eglDisplay != null) {
            egl.eglTerminate(eglDisplay);
            eglDisplay = null;
        }

        egl = null;
    }

    @Override
    public void removeSurace(GLSurface surface) {
        egl.eglDestroySurface(eglDisplay, surface.eglSurface);
        outputSurfaces.remove(surface);
    }

    @Override
    public void addSurface(GLSurface surface) {
        if (!outputSurfaces.contains(surface)) {
            makeOutputSurface(surface);
            outputSurfaces.add(surface);
        }
    }

    @Override
    public void render(GLRenderer renderer) {
        for (GLSurface output : outputSurfaces) {
            if (output.eglSurface == EGL10.EGL_NO_SURFACE) {
                if (!makeOutputSurface(output)) {
                    continue;
                }
            }
            // 设置当前的上下文环境和输出缓冲区
            egl.eglMakeCurrent(eglDisplay, output.eglSurface, output.eglSurface, eglContext);
            // 设置视窗大小及位置
            GLES30.glViewport(output.viewport.x, output.viewport.y, output.viewport.width, output.viewport.height);
            // 绘制
            renderer.onDrawFrame(output);
            // 交换显存(将surface显存和显示器的显存交换)
            egl.eglSwapBuffers(eglDisplay, output.eglSurface);
        }

    }

    private boolean makeOutputSurface(GLSurface surface) {
        /*
         * Check preconditions.
         */
        if (egl == null) {
            throw new RuntimeException("egl not initialized");
        }
        if (eglDisplay == null) {
            throw new RuntimeException("eglDisplay not initialized");
        }
        if (eglConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }

        // 创建Surface缓存
        try {
            switch (surface.type) {
                case GLSurface.TYPE_WINDOW_SURFACE: {
                    final int[] attributes = {EGL10.EGL_NONE};
                    // 创建失败时返回EGL14.EGL_NO_SURFACE
                    surface.eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, surface.surface, attributes);
                    break;
                }
                case GLSurface.TYPE_PBUFFER_SURFACE: {
                    // 创建失败时返回EGL14.EGL_NO_SURFACE
                    surface.eglSurface = createPbufferSurface(surface.viewport.width, surface.viewport.height);
                    break;
                }
                case GLSurface.TYPE_PIXMAP_SURFACE: {
                    Log.w(TAG, "nonsupport pixmap surface");
                    return false;
                }
                default:
                    Log.w(TAG, "surface type error " + surface.type);
                    return false;
            }
        } catch (Exception e) {
            Log.w(TAG, "can't create eglSurface");
            surface.eglSurface = EGL10.EGL_NO_SURFACE;
            return false;
        }

        return true;
    }

    private EGLSurface createPbufferSurface(int width, int height) {
        final int[] attributes = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE};
        return egl.eglCreatePbufferSurface(eglDisplay, eglConfig, attributes);
    }
}
