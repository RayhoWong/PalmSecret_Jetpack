package com.palmapp.master.module_face.activity.animal;

import javax.microedition.khronos.egl.EGLSurface;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/19
 * @tips 这个类是Object的子类
 * @fuction
 */
public interface IEGLEnvironment {

    public void initialize();

    void initCurrent(EGLSurface surface);

    public int swap();

    public void release();

    void removeSurace(GLSurface surface);

    void addSurface(GLSurface surface);

    void render(GLRenderer renderer);

}
