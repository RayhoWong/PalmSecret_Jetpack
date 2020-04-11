package com.palmapp.master.module_face.activity.album;

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.face.CartoonFaceRequest
import com.palmapp.master.baselib.bean.face.CartoonFaceResponse
import com.palmapp.master.baselib.bean.face.CartoonParam
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_face.R
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import java.io.File

class AlbumResultPresenter : BaseMultiplePresenter<AlbumResultView>() {
    private val parentPath = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("album2").plus(File.separator)

    @Volatile
    private var lastPos = -1
    private var originalBitmap: Bitmap? = null
    private val mBitmapOptions: BitmapFactory.Options = BitmapFactory.Options()
    private val mMainHandler = HandlerThread("Album")
    private val mHandler: Handler

    init {
        mBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565

        mMainHandler.start()
        mHandler = Handler(mMainHandler.looper)
    }

    override fun onAttach(pView: AlbumResultView, context: Context) {
        super.onAttach(pView, context)
        val file = File(parentPath)
        if (!file.exists()) {
            file.mkdir()
        }
        if (!file.listFiles().isNullOrEmpty()) {
            file.listFiles().forEach {
                it.delete()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mMainHandler.quit()
    }

    //分享 生成图片
    fun share(bitmap: Bitmap) {
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000, "", "13", "")
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
        selectFilter(false, 0, "", S3ImageInfo(), CartoonParam())
    }


    fun selectFilter(isVip: Boolean, pos: Int, path: String, s3ImageInfo: S3ImageInfo, cartoonParam: CartoonParam) {
        if (pos == 0 || !isVip) {
            //非vip用户或者第一张用原图
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
        getView()?.showLoading()
        lastPos = pos
        startFilter(pos, file.path, s3ImageInfo, cartoonParam)
    }


    /**
     * 卡通人像效果
     * 只有vip用户才能调用这个接口（接口次数有限制）
     */
    private fun startFilter(pos: Int, path: String, s3ImageInfo: S3ImageInfo, cartoonParam: CartoonParam) {
        originalBitmap?.let { src ->
            LogUtil.d("Album", "开始过滤图片 $pos")
            ThreadExecutorProxy.runOnMainThread(Runnable {
                getView()?.showLoading()
            })
            val activity = getView()?.getContext() as AlbumResultActivity

            val request = CartoonFaceRequest()
            request.cartoon_param = cartoonParam
            request.image = s3ImageInfo
            request.time_limit = false
            HttpClient.getFaceRequest().cartoonFace(request)
                .compose(activity.bindToLifecycle())
                .compose(NetworkTransformer.toMainSchedulers())
                .subscribe(object : NetworkSubscriber<CartoonFaceResponse>() {
                    override fun onNext(t: CartoonFaceResponse) {
                        if (!t.status_result?.isSuccess()!!) {
                            LogUtil.d("Album", "错误: ${t.status_result?.status_code}")
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.comic_succ, "", "2", "")
                            getView()?.showOriginalBitmap(originalBitmap)
                            Toast.makeText(activity, activity.resources.getString(R.string.magic_comic_timeout), Toast.LENGTH_SHORT).show()
                            return
                        }

                        t.cartoon_report?.cartoon_image_url?.let {
                            ImageLoaderUtils.displayImage(getContext(), it, object : ImageLoadingListener() {
                                override fun onLoadingComplete(bitmap: Bitmap?) {
                                    super.onLoadingComplete(bitmap)
                                    bitmap?.let {
                                        FileUtils.writeBitmap(path, bitmap)
                                        ThreadExecutorProxy.runOnMainThread(Runnable {
                                            LogUtil.d("Album", "结束过滤图片 $pos lastPos $lastPos")
                                            if (lastPos == pos) {
                                                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                                    BaseSeq103OperationStatistic.comic_succ, "", "1", "")
                                                getView()?.showResult(bitmap)
                                            }
                                        })
                                    }
                                }

                                override fun onLoadingFailed(bitmap: Bitmap?) {
                                    super.onLoadingFailed(bitmap)
                                    LogUtil.d("Album", "onLoadingFailed:  Glide加载图片失败")
                                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                        BaseSeq103OperationStatistic.comic_succ, "", "2", "")
                                    getView()?.showOriginalBitmap(originalBitmap)
//                                    Toast.makeText(activity, activity.resources.getString(R.string.magic_comic_timeout), Toast.LENGTH_SHORT).show()
                                }
                            })
                        }

                    }

                    override fun onError(t: Throwable) {
                        super.onError(t)
                        LogUtil.d("Album", "获取卡通图片失败，$t")
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.comic_succ, "", "2", "")
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.comic_succ, "", "3", "")
                        netError()
                    }
                })
        }

    }


    private fun netError() {
        ThreadExecutorProxy.runOnMainThread(Runnable {
            val activity = getView()?.getContext() as AlbumResultActivity
            getView()?.showOriginalBitmap(originalBitmap)
            Toast.makeText(activity, activity.resources.getString(R.string.magic_comic_timeout), Toast.LENGTH_SHORT).show()
        })
    }

}