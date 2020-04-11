package com.palmapp.master.module_face.activity.cartoon;

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.widget.ImageView
import com.palmapp.master.InterpreterHelper
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_face.R
import org.tensorflow.lite.Interpreter
import java.io.File

class CartoonResultPresenter : BaseMultiplePresenter<CartoonResultView>() {
    private val parentPath =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("cartoon")
            .plus(File.separator)
    private val ddims = intArrayOf(1, 512, 512, 3)
    private var mInterpreter: Interpreter? = null
    @Volatile
    private var lastPos = -1
    private var originalBitmap: Bitmap? = null
    private val mBitmapOptions: BitmapFactory.Options = BitmapFactory.Options()
    private val mMainHandler = HandlerThread("Cartoon")
    private val mHandler: Handler

    init {
        mBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565

        mMainHandler.start()
        mHandler = Handler(mMainHandler.looper)
    }

    override fun onAttach(pView: CartoonResultView, context: Context) {
        super.onAttach(pView, context)
        val file = File(parentPath)
        if (!file.exists()) {
            file.mkdir()
        }
        file.listFiles().forEach {
            it.delete()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mMainHandler.quit()
    }

    //分享 生成图片
    fun share(bitmap: Bitmap) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "4",
            ""
        )
        val view =
            LayoutInflater.from(getView()?.getContext())
                .inflate(R.layout.face_activity_cartoon_share, null, false)
        view.findViewById<ImageView>(R.id.iv_share_result).setImageBitmap(bitmap)
        ShareManager.share(
            view,
            getView()?.getContext()?.getString(R.string.app_app_name) ?: "",
            bitmap.width,
            bitmap.height
        )

    }

    fun start(bitmap: Bitmap) {
        originalBitmap = bitmap
        selectFilter(0, "", "")
    }

    fun selectFilter(pos: Int, path: String, tfFileName: String) {
        if (lastPos == pos) {
            return
        }
        getView()?.showLoading()
        lastPos = pos
        if (pos == 0) {
            //第一张用原图
            getView()?.showOriginalBitmap(originalBitmap)
            return
        }
        val file = File(parentPath, path)
        if (file.exists()) {
            //已经有过滤好的图
            getView()?.showResult(
                BitmapFactory.decodeFile(file.path, mBitmapOptions)
            )
            return
        }
        startFilter(pos, file.path, tfFileName)
    }

    private fun startFilter(pos: Int, path: String, tfFileName: String) {
        mHandler.removeCallbacksAndMessages(null)
        mHandler.postAtFrontOfQueue(Runnable {
            originalBitmap?.let { src ->
                LogUtil.d("Cartoon", "开始过滤图片 $pos")
                ThreadExecutorProxy.runOnMainThread(Runnable {
                    getView()?.showLoading()
                })
                val activity = getView()?.getContext() as Activity
                mInterpreter?.close()
                mInterpreter = InterpreterHelper.decryptModel(activity, tfFileName)
                val input = AppUtil.getScaledMatrix(src, ddims)
                val output = Array(1) { Array(ddims[1]) { Array(ddims[2]) { FloatArray(3) } } }
                mInterpreter?.run(input, output)
                var bitmap = AppUtil.getBitmap(output, ddims)
                bitmap = Bitmap.createScaledBitmap(bitmap, src.width, src.height, false)
                FileUtils.writeBitmap(path, bitmap)
                ThreadExecutorProxy.runOnMainThread(Runnable {
                    LogUtil.d("Cartoon", "结束过滤图片 $pos lastPos $lastPos")
                    if (lastPos == pos) {
                        getView()?.showResult(bitmap)
                    }
                })
            }
        })
    }
}