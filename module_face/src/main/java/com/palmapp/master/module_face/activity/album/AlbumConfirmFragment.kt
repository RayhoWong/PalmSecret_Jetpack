package com.palmapp.master.module_face.activity.album

import AmazonS3Manger
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.utils.ToastUtils
import com.example.oldlib.old.FaceDetect
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.KeyCreator
import com.palmapp.master.baselib.Product
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.UploadImageInfo
import com.palmapp.master.baselib.bean.face.*
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.ui.FaceAnimActivityInject
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.FaceAnimatorView
import com.palmapp.master.baselib.view.GradientBorderDrawable
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.module_face.R
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import com.palmapp.master.module_imageloader.glide.ImageLoadingListener
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.trello.rxlifecycle2.components.support.RxFragment
import kotlinx.android.synthetic.main.face_fragment_album_confirm.*
import kotlinx.android.synthetic.main.face_fragment_album_confirm.layout_face_anim
import org.greenrobot.eventbus.EventBus
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class AlbumConfirmFragment : RxFragment(), View.OnClickListener {

    private var newPlan: String = "0"
    private val KEY_PHOTO: String = "KEY_PHOTO"
    private val KEY_NEWPLAN: String = "KEY_NEWPLAN"
    private var photo: String? = null //拍照后的图片路径
    private var photoBitmap: Bitmap? = null //拍照或者相册返回的图片
    private var finalBitmap: Bitmap? = null //最终确定的图片

    private var mActivity: AlbumTakePhotoActivity? = null
    private val TAG = "ConfirmFragment"
    //裁剪后的图片路径
    private val scale =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("album")

    private var faceInfo: FaceInfo? = null


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @return A new instance of fragment BlankFragment.
         */
        @JvmStatic //创建Fragment实例并且保存Activity传给Fragment的参数
        fun newInstance(param1: String, newPlan: String?) =
            AlbumConfirmFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_PHOTO, param1)
                    putString(KEY_NEWPLAN, newPlan)
                }
            }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AlbumTakePhotoActivity) {
            mActivity = context
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photo = it.getString(KEY_PHOTO)
            newPlan = it.getString(KEY_NEWPLAN) ?: "0"
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.face_fragment_album_confirm, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //首次进入
        var isFirstEnter = newPlan == "1"
        if (isFirstEnter) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_f000, "1", "2", "")
            GoPrefManager.getDefault().putBoolean(PreConstants.Face.KEY_FACE_IS_FIRST_ENTER_ALBUM_TAKE_PHOTO, false).commit()
        } else {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.comic_page_f000, "2", "2", "")
        }

        val drawable = GradientBorderDrawable(
            resources.getDimension(com.palmapp.master.baselib.R.dimen.change_4px),
            resources.getDimension(com.palmapp.master.baselib.R.dimen.change_75px),
            Color.parseColor("#0bc88c"),
            Color.parseColor("#187edc")
        )
        tv_cancel.background = drawable

        tv_ok.setOnClickListener(this)
        tv_cancel.setOnClickListener(this)
        album_takephoto_back.setOnClickListener(this)

        photo?.let {
            ImageLoaderUtils.displayImageNoCache(activity, photo, object : ImageLoadingListener() {
                override fun onLoadingComplete(bitmap: Bitmap?) {
                    bitmap?.let {
                        if (face_image != null) {
                            face_image.setSource(it)
                            photoBitmap = it
                        }
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_ok -> {
                isFirstUpload()
            }
            R.id.tv_cancel -> {
                finish()
            }
            R.id.album_takephoto_back -> {
                finish()
            }
        }
    }


    private fun isFirstUpload() {
        if (GoPrefManager.getDefault().getBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, true)) {
            val dialog = mActivity?.let { PermissionDialog(it) }
            dialog?.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                override fun onPositiveClick() {
                    GoPrefManager.getDefault()
                        .putBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, false)
                        .commit()
                    dialog.dismiss()
                    showAnim()
                }

                override fun onNegativeClick() {

                }
            })
            dialog?.show()
        } else {
            showAnim()
        }
    }


    private fun showAnim() {
        val bitmap = face_image.getCropBitmap(false)
        FaceAnimActivityInject.injectShowBitmap(activity, layout_face_anim as ViewGroup, bitmap, Runnable {
            FaceDetect.faceDetectToContourPoint(bitmap, object : FaceDetect.IFacePointListener {
                override fun onSuccess(contourPoints: MutableList<MutableList<PointF>>?) {
//                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
//                    BaseSeq103OperationStatistic.face_scan,"1","1","")
                    if (contourPoints!!.isEmpty()) {
                        LogUtil.d("人脸识别失败")
                        FaceAnimActivityInject.injectFail(layout_face_anim as ViewGroup)
                        ToastUtils.makeEventToast(activity, getString(R.string.face_daily_error), false)
                        finish()
                    } else {
                        FaceAnimActivityInject.injectSuccess(activity, layout_face_anim as ViewGroup, bitmap, contourPoints, object : FaceAnimatorView.OnAnimUpdateListener {
                            override fun onAnimEnd() {
                                uploadFace()
                            }

                            override fun onUpdate(value: Int) {
                            }

                        })
                    }
                }

                override fun onFailure() {
//                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan,"1","2","")
                    FaceAnimActivityInject.injectFail(layout_face_anim as ViewGroup)
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.face_scan, "3", "3", "")
                    activity?.runOnUiThread {
                        Toast.makeText(activity, R.string.face_daily_error, Toast.LENGTH_SHORT).show()
                    }
                    finish()
                }
            })
        })
    }


    private fun uploadFace() {
        face_takephoto_button_layout.visibility = View.INVISIBLE
        finalBitmap = face_image.getCropBitmap(false)
        finalBitmap?.let { src ->
            ThreadExecutorProxy.execute(Runnable {
                //裁剪图片并分析
                LogUtil.d(TAG, "分析图片")
                var bitmap = AppUtil.resizeBitmap(src, 250, 250)
                FileUtils.writeBitmap(scale, bitmap)
                LogUtil.d(
                    TAG, "文件路径：${scale} 图片大小：${PhotoUtil.getBitmapSize(bitmap)}" +
                            " 宽度：${bitmap.width}  高度：${bitmap.height} "
                )
                val file = File(scale)
                if (!file.exists()) {
                    LogUtil.d(TAG, "文件不存在")
                    return@Runnable
                }

                val info = UploadImageInfo()
                info.file = file
                info.width = bitmap.width
                info.height = bitmap.height
                info.type = KeyCreator.TYPE_ALBUM_REPORT

                //亚马逊上传
                AmazonS3Manger.uploadImage(info, object : AmazonS3Manger.UploadListener {
                    override fun onUploadCompleted(imageInfo: S3ImageInfo, imageUrl: String) {
                        LogUtil.d(TAG, "上传亚马逊成功，返回etag值：${imageInfo?.etag}  " + "\n文件大小：" + "${file.length()}")
                        EventBus.getDefault().postSticky(imageInfo)

                        val request = FaceIdentityRequest()
                        request.image = imageInfo
                        HttpClient.getFaceRequest().detectFace(request)
                            .compose(NetworkTransformer.toMainSchedulers())
                            .compose(bindToLifecycle())
                            .subscribe(object : NetworkSubscriber<FaceIdentityResponse>() {
                                override fun onNext(t: FaceIdentityResponse) {
                                    if (!t.status_result?.status_code.equals("SUCCESS")) {
                                        LogUtil.d(TAG, "人脸检测失败：${t.status_result?.status_code}")
                                        faceDetectFailed()
                                        return
                                    } else {
                                        faceInfo = t.face_info?.getOrNull(0)
                                        cartoonTemplates()
                                    }
                                }

                                override fun onError(t: Throwable) {
                                    super.onError(t)
                                    LogUtil.d(TAG, "人脸检测失败，$t")
                                    netError()
                                }
                            })
                    }

                    override fun onUploadProgress(percent: Int) {

                    }

                    override fun onUploadError(errorCode: Int) {
                        LogUtil.d(TAG, "上传亚马逊失败，状态值：$errorCode")
                        netError()
                    }
                })

            })
        }
    }


    private fun cartoonTemplates() {
        val request = CartoonTemplateRequest()
        request.module_id = Product.MAGIC_ALBUM_MODULE_ID
        HttpClient.getFaceRequest().cartoonTemplate(request)
            .compose(bindToLifecycle())
            .compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<CartoonTemplateResponse>() {

                override fun onNext(t: CartoonTemplateResponse) {
                    t.cartoon_templates?.let {
                        EventBus.getDefault().postSticky(faceInfo)
                        EventBus.getDefault().postSticky(it)
                        toResultPage()
                    }
                }

                override fun onError(t: Throwable) {
                    super.onError(t)
                    LogUtil.d(TAG, "人脸检测失败，$t")
                    netError()
                }

            })
    }


    private fun faceDetectFailed() {
//        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan, "3", "2", "")
        var dialog = activity?.let { CommonDialog(it) }
        dialog?.let {
            it.setNegativeVisibilty(false)
            it.title =
                activity?.let { activity -> activity.resources.getString(R.string.face_daily_error) }
            it.negative =
                activity?.let { activity -> activity.resources.getString(R.string.baby_title_dialog_confirm) }
            it.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                override fun onNegativeClick() {
                }

                override fun onPositiveClick() {
                    finish()
                }
            })
            it.show()
        }
    }


    private fun netError() {
//        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
//            BaseSeq103OperationStatistic.face_scan,"3","3","")
        activity?.runOnUiThread {
            Toast.makeText(
                activity, getString(R.string.net_error), Toast.LENGTH_SHORT
            ).show()
        }
        finish()
    }


    private fun toResultPage() {
        finalBitmap?.let {
            EventBus.getDefault().postSticky(it)
        }
        ARouter.getInstance().build(RouterConstants.ACTIVITY_ALBUM_RESULT).withString("newPlan", newPlan).navigation()
        activity?.finish()
    }


    private fun finish() {
        mActivity?.let {
            it.removeConfirmFragment()
        }
    }


    override fun onDestroyView() {
        if (layout_face_anim != null) {
            FaceAnimActivityInject.injectDetach(activity, layout_face_anim as ViewGroup)
        }
        super.onDestroyView()
    }
}
