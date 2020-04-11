package com.palmapp.master.module_face.activity.takephoto.fragment.takephotoed;

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.net.Uri
import com.cs.bd.commerce.util.io.BitmapUtility
import com.example.oldlib.old.FaceDetect
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.Exception

class TakephotoedPresenter : BasePresenter<TakephotoedView>() {
    override fun onAttach() {

    }

    override fun onDetach() {

    }


    fun faceDetectToContourPoint(bitmap: Bitmap) {
        if (GoPrefManager.getDefault().getBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, true)) {
            getView()?.getContext()?.let {
                val dialog = PermissionDialog(it)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                    override fun onPositiveClick() {
                        GoPrefManager.getDefault().putBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, false)
                            .commit()
                        faceDetectToContourPoint(bitmap)
                    }

                    override fun onNegativeClick() {

                    }
                })
                dialog.show()
                getView()?.enableClick()
                return
            }
        }
        try {
            if (bitmap != null) {
                getView()?.let {
                    it.startScanAnima()
                }
                bitmap?.let {
                    FaceDetect.faceDetectToContourPoint(bitmap, object : FaceDetect.IFacePointListener {
                        /*override fun success(contourPoints: FloatArray?) {
                            LogUtil.d("人脸识别成功")
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_scan,"1","1","")
                            getView()?.let {
                                it.faceDetectSuccess(contourPoints,bitmap)
                            }
                        }*/

                        override fun onSuccess(contourPoints: MutableList<MutableList<PointF>>?) {
                            LogUtil.d("人脸识别成功")
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                                BaseSeq103OperationStatistic.face_scan,"1","1","")
                            getView()?.let {
                                it.faceDetectSuccess(contourPoints, bitmap)
                            }
                        }

                        override fun onFailure() {
                            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.face_scan,"1","2","")
                            getView()?.let {
                                it.stopScanAnima()
                                it.fragmentInteraction(Uri.Builder().appendPath("goto.TakephotoActivity").build(), null)
                                it.showToast(R.string.face_daily_error)
                            }
                        }
                    })
                }
            } else {
                getView()?.let {
                    it.fragmentInteraction(Uri.Builder().appendPath("goto.TakephotoActivity").build(), null)
                    it.showToast(R.string.face_daily_error)
                }
            }

        } catch (oom: OutOfMemoryError) {

        } catch (ex: Exception) {

        }
    }

    fun faceDetectToOld(application: Application?, poinsts: FloatArray?, bitmap: Bitmap) {
        try {
//            val options = BitmapFactory.Options()
//            options.inSampleSize = 2
            val path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("oldImage")
            FileUtils.writeBitmap(path, bitmap)
            bitmap?.let {
                FaceDetect.faceDetectToOld(
                    application,
                    poinsts,
                    bitmap,
                    listOf(50, 70, 90),
                    object : FaceDetect.IAgeingListener {

                        override fun success(bitmaps: MutableList<Bitmap>?) {
                            ThreadExecutorProxy.runOnMainThread(object : Runnable {
                                override fun run() {
                                    getView()?.let {
                                        it.stopScanAnima()
                                        it.fragmentInteraction(
                                            Uri.Builder().appendPath("goto.OldFragment").build(),
                                            DataObject(bitmaps,bitmap)
                                        )
                                    }
                                }
                            })
                        }

                        override fun onFailure() {
                            ThreadExecutorProxy.runOnMainThread(object : Runnable {
                                override fun run() {
                                    getView()?.let {
                                        it.stopScanAnima()
                                        it.fragmentInteraction(
                                            Uri.Builder().appendPath("goto.TakephotoActivity").build(),
                                            null
                                        )
                                        it.showToast(R.string.face_daily_error)
                                    }
                                }
                            })
                        }
                    })
            }
        } catch (oom: OutOfMemoryError) {
            getView()?.let {
                it.stopScanAnima()
                it.fragmentInteraction(Uri.Builder().appendPath("goto.TakephotoActivity").build(), null)
                it.showToast(R.string.face_daily_error)
            }
        }
    }

}