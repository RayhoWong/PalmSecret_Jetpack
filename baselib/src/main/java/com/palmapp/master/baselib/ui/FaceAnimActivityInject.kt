package com.palmapp.master.baselib.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.PointF
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.palmapp.master.baselib.R
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.event.FaceAnimEvent
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.FaceAnimatorView
import com.palmapp.master.baselib.view.PermissionDialog
import org.greenrobot.eventbus.EventBus

object FaceAnimActivityInject {

    fun toFloat(contourPoints: MutableList<MutableList<PointF>>?) : FloatArray {
        var size = 0
        if (contourPoints != null) {
            for(pointArray in contourPoints) {
                size+= pointArray.size * 2
            }
        }
        var index = 0
        val points = FloatArray(size)
        if (contourPoints != null) {
            for(pointArray in contourPoints) {
                for(point in pointArray) {
                    val px = point.x
                    points[index] = px
                    index++
                    val py = point.y
                    points[index] = py
                    index++
                }
            }
        }
        return points
    }

    /**
     * step 2
     */
    fun injectSuccess(activity: Activity?, viewGroup: ViewGroup, mSource: Bitmap, contourPoints: MutableList<MutableList<PointF>>?, listener: FaceAnimatorView.OnAnimUpdateListener? = null) {
        viewGroup.visibility = View.VISIBLE
        if (contourPoints != null && contourPoints.size <= 12) {
            injectFail(viewGroup)
            return
        }

        LogUtil.d(this.javaClass.simpleName, "contourPoints:$contourPoints")
        val list = mutableListOf<MutableList<PointF>>()
        if (contourPoints != null && contourPoints.size > 12) {
            LogUtil.d("contourPoints:${contourPoints.size}")
            list.add(contourPoints[12])
            var tmpArray = ArrayList<PointF>()
            tmpArray.addAll(contourPoints[4])
            tmpArray.add(PointF(-1f,-1f))
            tmpArray.addAll(contourPoints[5])
            list.add(tmpArray)

            tmpArray = ArrayList()
            tmpArray.addAll(contourPoints[8])
            tmpArray.add(PointF(-1f,-1f))
            tmpArray.addAll(contourPoints[9])
            tmpArray.add(PointF(-1f,-1f))
            tmpArray.addAll(contourPoints[10])
            tmpArray.add(PointF(-1f,-1f))
            tmpArray.addAll(contourPoints[11])
            list.add(tmpArray)

            tmpArray = ArrayList()
            tmpArray.addAll(contourPoints[6])
            tmpArray.add(PointF(-1f,-1f))
            tmpArray.addAll(contourPoints[7])
            list.add(tmpArray)
        }

        val faceAnim = viewGroup.findViewById<FaceAnimatorView>(R.id.face_view)
        viewGroup.findViewById<View>(R.id.iv_takephoto_back).setOnClickListener {
            viewGroup.visibility = View.GONE
            faceAnim.stopAnim()
            activity?.onBackPressed()
        }
        val pbFace = viewGroup.findViewById<ProgressBar>(R.id.pb_face)
        val pbEye = viewGroup.findViewById<ProgressBar>(R.id.pb_eye)
        val pbMouth = viewGroup.findViewById<ProgressBar>(R.id.pb_mouth)
        val pbNose = viewGroup.findViewById<ProgressBar>(R.id.pb_nose)
        pbFace.max = 1000
        pbEye.max = 1000
        pbMouth.max = 1000
        pbNose.max = 1000

        val loading = viewGroup.findViewById<View>(R.id.loading)


        faceAnim.setData(mSource, list, object :FaceAnimatorView.OnAnimUpdateListener{
            override fun onAnimEnd() {
                loading.visibility = View.VISIBLE
                if(listener == null) {
                    EventBus.getDefault().post(FaceAnimEvent(true))
                } else {
                    listener.onAnimEnd()
                }
            }

            override fun onUpdate(value: Int) {
                if(listener == null) {
                } else {
                    listener.onUpdate(value)
                }
                when {
                    value <= 2000 -> {
                        pbFace.progress = value - 1000
                    }
                    value <= 3000 -> {
                        pbFace.progress = 1000
                        pbEye.progress = value - 2000
                    }
                    value <= 4000 -> {
                        pbEye.progress = 1000
                        pbMouth.progress = value - 3000
                    }
                    value <= 5000 -> {
                        pbMouth.progress = 1000
                        pbNose.progress = value - 4000
                    }
                }
            }

        })
        faceAnim.visibility = View.VISIBLE
    }

    private fun realInjectShowBitmap(activity: Activity?,viewGroup: ViewGroup, mSource: Bitmap) {
        viewGroup.visibility = View.VISIBLE
        val faceAnim = viewGroup.findViewById<FaceAnimatorView>(R.id.face_view)
        viewGroup.findViewById<View>(R.id.iv_takephoto_back).setOnClickListener {
            viewGroup.visibility = View.GONE
            faceAnim.stopAnim()
            activity?.onBackPressed()
        }

        faceAnim.setData(mSource)
        faceAnim.visibility = View.VISIBLE
    }
    /**
     * step 1
     */
    fun injectShowBitmap(activity: Activity?,viewGroup: ViewGroup, mSource: Bitmap, runnable: Runnable? = null) {
        if (GoPrefManager.getDefault().getBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, true)) {
            activity?.let {
                val dialog = PermissionDialog(it)
                dialog.setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {
                    override fun onPositiveClick() {
                        GoPrefManager.getDefault().putBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, false)
                            .commit()
                        realInjectShowBitmap(activity,viewGroup, mSource)
                        runnable?.run()
                    }

                    override fun onNegativeClick() {

                    }
                })
                dialog.show()
            }
        } else {
            realInjectShowBitmap(activity,viewGroup, mSource)
            runnable?.run()
        }
    }
    /**
     * step 3
     */
    fun injectFail(viewGroup: ViewGroup) {
        viewGroup.visibility = View.GONE
        val faceAnim = viewGroup.findViewById<FaceAnimatorView>(R.id.face_view)
        faceAnim?.stopAnim()
    }
    /**
     * step 4
     */
    fun injectDetach(activity: Activity?, viewGroup: ViewGroup) {
        val faceAnim = viewGroup.findViewById<FaceAnimatorView>(R.id.face_view)
        faceAnim?.stopAnim()
    }

}
