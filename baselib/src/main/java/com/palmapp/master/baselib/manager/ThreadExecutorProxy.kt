package com.palmapp.master.baselib.manager

import android.os.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**线程管理类
 *
 * @author :     xiemingrui
 * @since :      2019/7/30
 */
object ThreadExecutorProxy {
    private const val ASYNC_THREAD_NAME = "plam-async-thread"
    private val mAsyncHandler: Handler  //异步线程handler
    private val mMainHandler: Handler   //主线程
    private val mMessageQueue: MessageQueue
    private val mExecutor: ScheduledExecutorService

    init {
        val handlerThread = HandlerThread(ASYNC_THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND)
        handlerThread.start()
        mAsyncHandler = Handler(handlerThread.looper)
        mMainHandler = Handler(Looper.getMainLooper())
        mMessageQueue = Looper.myQueue()

        var sCorePoolSize = Runtime.getRuntime().availableProcessors() * 2 - 1
        sCorePoolSize = Math.max(Math.min(sCorePoolSize, 6), 2)
        mExecutor = Executors.newScheduledThreadPool(sCorePoolSize) {
            val t = Thread(it)
            if (it is GoTask) {
                t.name = it.name
                t.priority = it.priority
            }
            t
        }
    }

    /**
     * 通过线程池执行task，适合数量多的task同时执行
     */
    fun execute(task: Runnable, name: String = "", priority: Int = 5) {
        mExecutor.execute(GoTask(task, name, priority))
    }

    fun execute(task:Runnable, name: String = "", priority: Int = 5,timer:Long){
        mExecutor.schedule(GoTask(task, name, priority),timer,TimeUnit.MILLISECONDS)
    }

    /**
     * 通过异步线程handler执行task，适合数量少的task执行，支持延时
     */
    fun runOnAsyncThread(r: Runnable, delay: Long = 0) {
        mAsyncHandler.postDelayed(r, delay)
    }

    fun runOnAsyncThreadAtFront(r: Runnable) {
        mAsyncHandler.postAtFrontOfQueue(r)
    }

    fun clearAsyncThread() {
        mAsyncHandler.removeCallbacksAndMessages(null)
    }

    /**
     * 主线程执行task
     */
    fun runOnMainThread(r: Runnable, delay: Long = 0) {
        mMainHandler.postDelayed(r, delay)
    }

    /**
     *  主线程空闲时执行task
     */
    fun runOnIdleThread(r: Runnable) {
        mMessageQueue.addIdleHandler {
            r.run()
            false
        }
    }

    class GoTask(val task: Runnable, val name: String = "", val priority: Int = 5) : Runnable {

        override fun run() {
            task.run()
        }
    }

}