package com.palmapp.master.module_palm.scan

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.OkDialog
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.module_palm.R
import com.palmapp.master.module_palm.view.HandAnimatorView
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.opencv.core.Scalar
import java.io.IOException

object PalmAnimActivityInject {

    fun injectShowBitmap(activity: RxAppCompatActivity?, viewGroup: ViewGroup, bitmap: Bitmap, errorRunnable: Runnable? = null, listener: HandAnimatorView.OnAnimUpdateListener? = null) {
//        if (GoPrefManager.getDefault().getBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, true)) {
//            activity?.let {
//                val dialog = PermissionDialog(activity)
//                dialog.setCancelable(false)
//                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
//                    override fun onPositiveClick() {
//                        GoPrefManager.getDefault().putBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, false)
//                            .commit()
//                        realShowBitmap(activity, viewGroup, bitmap, errorRunnable, listener)
//                    }
//
//                    override fun onNegativeClick() {
//                    }
//                })
//                dialog.show()
//                return
//            }
//        } else {
            realShowBitmap(activity, viewGroup, bitmap, errorRunnable, listener)
//        }
    }
    fun injectShowScan(activity: RxAppCompatActivity?, viewGroup: ViewGroup, bitmap: Bitmap) {
        showScan(activity, viewGroup, bitmap)
    }

    private fun showScan(activity: RxAppCompatActivity?, viewGroup: ViewGroup, bitmap: Bitmap) {
        viewGroup.visibility = View.VISIBLE
        val handView = viewGroup.findViewById<HandAnimatorView>(R.id.hand_view)
        viewGroup.findViewById<View>(R.id.iv_takephoto_back).setOnClickListener {
            viewGroup.visibility = View.GONE
            handView.stopAnim()
            activity?.onBackPressed()
        }

        val mSource = Bitmap.createScaledBitmap(bitmap, PalmImageManager.HAVEHAND_INPUT_SIZE * 5, PalmImageManager.HAVEHAND_INPUT_SIZE  * 5, false)
        handView.setData(mSource)
        handView.visibility = View.VISIBLE
    }

    private fun realShowBitmap(activity: RxAppCompatActivity?, viewGroup: ViewGroup, bitmap: Bitmap, errorRunnable: Runnable? = null, listener: HandAnimatorView.OnAnimUpdateListener? = null) {
        viewGroup.visibility = View.VISIBLE
        val handView = viewGroup.findViewById<HandAnimatorView>(R.id.hand_view)
        viewGroup.findViewById<View>(R.id.iv_takephoto_back).setOnClickListener {
            viewGroup.visibility = View.GONE
            handView.stopAnim()
            activity?.onBackPressed()
        }
        // 缩放比例，让点一致
        val mSource = Bitmap.createScaledBitmap(bitmap, PalmImageManager.HAVEHAND_INPUT_SIZE * 5, PalmImageManager.HAVEHAND_INPUT_SIZE  * 5, false)
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
        handView.setData(mSource)
        handView.visibility = View.VISIBLE


        val pbPalmShape = viewGroup.findViewById<ProgressBar>(R.id.pb_palm_shape)
        val pbFinger = viewGroup.findViewById<ProgressBar>(R.id.pb_finger)
        val pbPalmPrint = viewGroup.findViewById<ProgressBar>(R.id.pb_palm_print)
        pbPalmShape.max = 3500
        pbFinger.max = 3500
        pbPalmPrint.max = 3500

        val loading = viewGroup.findViewById<View>(R.id.loading)

        val disposable =Observable.fromCallable {
            val point = PalmImageManager.getPoint(mSource) ?: throw IOException("")
            val bitmaps = ArrayList<Bitmap?>()
            for ((index, item) in point.withIndex()) {
                when (index) {
                    0 -> {
                        bitmaps.add(
                            PalmImageManager.realStartToBitmap(
                                mSource.width.toDouble(), mSource.height.toDouble(),
                                item, Scalar(0.toDouble(), 171.toDouble(), 137.toDouble(), 255.toDouble())
                            )
                        )
                    }
                    2 -> {
                        bitmaps.add(
                            PalmImageManager.realStartToBitmap(
                                mSource.width.toDouble(), mSource.height.toDouble(),
                                item, Scalar(255.toDouble(), 67.toDouble(), 98.toDouble(), 255.toDouble())
                            )
                        )
                    }
                    3 -> {
                        bitmaps.add(
                            PalmImageManager.realStartToBitmap(
                                mSource.width.toDouble(), mSource.height.toDouble(),
                                item, Scalar(0.toDouble(), 125.toDouble(), 196.toDouble(), 255.toDouble())
                            )
                        )
                    }
                }
            }
            handView.setData(mSource, point, bitmaps, object : HandAnimatorView.OnAnimUpdateListener {
                override fun onAnimEnd() {
                    loading.visibility = View.VISIBLE
                    if (listener == null) {
                    } else {
                        listener.onAnimEnd()
                    }
                }

                override fun onUpdate(value: Int) {
                    if (listener == null) {
                    } else {
                        listener.onUpdate(value)
                    }
                    when {
                        value <= 3500 -> {
                            pbPalmShape.progress = value
                        }
                        value <= 7000 -> {
                            pbPalmShape.progress = 3500
                            pbFinger.progress = value - 3500
                        }
                        value <= 10500 -> {
                            pbFinger.progress = 3500
                            pbPalmPrint.progress = value - 7000
                        }
                    }
                }

            })
            true
        }.compose(activity?.bindToLifecycle())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({}, {
                viewGroup.visibility = View.GONE
                handView?.stopAnim()
                activity?.runOnUiThread(errorRunnable)
            })
    }


    fun injectDetach(activity: Activity?, viewGroup: ViewGroup) {
        val handView = viewGroup.findViewById<HandAnimatorView>(R.id.hand_view)
        viewGroup.visibility = View.GONE
        handView?.stopAnim()
    }

}
