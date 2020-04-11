package com.palmapp.master.module_face.activity.animal;

import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author rayhahah
 * @blog http://rayhahah.com
 * @time 2019/9/26
 * @tips 这个类是Object的子类
 * @fuction
 */
public abstract class AbsGLThread extends Thread {

    protected static final String TAG = "GLThread";

    protected ArrayBlockingQueue<Event> eventQueue;

    protected boolean rendering;
    protected boolean isRelease;

    public AbsGLThread() {
        setName("GLRenderer-" + getId());
        rendering = false;
        isRelease = false;
        eventQueue = new ArrayBlockingQueue<>(100);
    }

    /**
     * 开始渲染
     * 启动线程并等待初始化完毕
     */
    public void startRender() {
        if (!eventQueue.offer(new Event(Event.START_RENDER))) {
            Log.e(TAG, "queue full");
        }
        if (getState() == State.NEW) {
            super.start(); // 启动渲染线程
        }
    }

    public void stopRender() {
        if (!eventQueue.offer(new Event(Event.STOP_RENDER))) {
            Log.e(TAG, "queue full");
        }
    }

    public boolean postRunnable(Runnable runnable) {
        Event event = new Event(Event.RUNNABLE);
        event.param = runnable;
        if (!eventQueue.offer(event)) {
            Log.e(TAG, "queue full");
            return false;
        }

        return true;
    }

    public boolean postRenderRunnable(Runnable runnable) {
        Event event = new Event(Event.RUNNABLE_RENDER);
        event.param = runnable;
        if (!eventQueue.offer(event)) {
            Log.e(TAG, "queue full");
            return false;
        }

        return true;
    }

    public boolean postRenderCallback(Callback callback) {
        Event event = new Event(Event.CALLBACK_RENDER);
        event.param = callback;
        if (!eventQueue.offer(event)) {
            Log.e(TAG, "queue full");
            return false;
        }

        return true;
    }

    public void addSurface(final GLSurface surface) {
        Event event = new Event(Event.ADD_SURFACE);
        event.param = surface;
        if (!eventQueue.offer(event)) {
            Log.e(TAG, "queue full");
        }
    }

    public void removeSurface(final GLSurface surface) {
        Event event = new Event(Event.REMOVE_SURFACE);
        event.param = surface;
        if (!eventQueue.offer(event)) {
            Log.e(TAG, "queue full");
        }
    }

    /**
     * 不要直接调用
     */
    @Override
    public void start() {
        Log.w(TAG, "Don't call this function");
    }

    /**
     * 请求渲染
     */
    public void requestRender() {
        eventQueue.offer(new Event(Event.REQ_RENDER));
    }

    /**
     * 退出OpenGL渲染并释放资源
     * 这里先将渲染器释放(renderer)再退出looper，因为renderer里面可能持有这个looper的handler，
     * 先退出looper再释放renderer可能会报一些警告信息(sending message to a Handler on a dead thread)
     */
    public void release() {
        if (eventQueue.offer(new Event(Event.RELEASE))) {
            // 等待线程结束，如果不等待，在快速开关的时候可能会导致资源竞争(如竞争摄像头)
            // 但这样操作可能会引起界面卡顿，择优取舍
            while (isAlive()) {
                try {
                    this.join(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void run() {
        Event event;
        Log.d(TAG, getName() + ": render create");
        onCreate();
        // 渲染
        while (!isRelease) {
            try {
                event = eventQueue.take();
                switch (event.event) {
                    case Event.ADD_SURFACE: {
                        // 创建eglSurface
                        GLSurface surface = (GLSurface) event.param;
                        Log.d(TAG, "add:" + surface);
                        onAddSurface(surface);
                        break;
                    }
                    case Event.REMOVE_SURFACE: {
                        GLSurface surface = (GLSurface) event.param;
                        Log.d(TAG, "remove:" + surface);
                        onRemoveSurface(surface);

                        break;
                    }
                    case Event.START_RENDER:
                        rendering = true;
                        break;
                    case Event.REQ_RENDER: // 渲染
                        if (rendering) {
                            onRender();
                        }
                        break;
                    case Event.STOP_RENDER:
                        rendering = false;
                        onStop();
                        break;
                    case Event.RUNNABLE:
                        ((Runnable) event.param).run();
                        break;
                    case Event.RELEASE:
                        isRelease = true;
                        break;
                    case Event.RUNNABLE_RENDER:
                        ((Runnable) event.param).run();
                        if (rendering) {
                            onRender();
                        }
                        break;
                    case Event.CALLBACK_RENDER:
                        ((Callback) event.param).renderBefore();
                        if (rendering) {
                            onRender();
                            if (!isRelease) {
                                ((Callback) event.param).renderAfter();
                            }
                        }
                        break;
                    default:
                        Log.e(TAG, "event error: " + event);
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        onRelease();
        eventQueue.clear();
        Log.d(TAG, getName() + ": render release");
    }

    /**
     * 移除Surface
     *
     * @param surface
     */
    protected void onRemoveSurface(GLSurface surface) {

    }

    /**
     * 添加新的Surface
     */
    protected void onAddSurface(GLSurface surface) {

    }

    /**
     * 停止行为
     */
    protected void onStop() {

    }

    /**
     * 每次执行刷新
     */
    protected abstract void onRender();

    /**
     * 初始化设置
     */
    protected abstract void onCreate();

    /**
     * 释放资源
     */
    protected abstract void onRelease();


    protected static class Event {
        // 添加输出的surface
        static final int ADD_SURFACE = 1;
        // 移除输出的surface
        static final int REMOVE_SURFACE = 2;
        // 开始渲染
        static final int START_RENDER = 3;
        // 请求渲染
        static final int REQ_RENDER = 4;
        // 结束渲染
        static final int STOP_RENDER = 5;
        //
        static final int RUNNABLE = 6;
        // 释放渲染器
        static final int RELEASE = 7;
        //执行完毕以后立即渲染一次
        static final int RUNNABLE_RENDER = 8;
        //执行完毕以后立即渲染一次,且回调
        static final int CALLBACK_RENDER = 9;

        final int event;
        Object param;

        Event(int event) {
            this.event = event;
        }
    }

    public interface Callback {
        void renderBefore();

        void renderAfter();
    }
}
