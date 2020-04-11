package com.palmapp.master.module_face.activity.takephoto.fragment.takephotoed;

import android.graphics.Bitmap
import android.graphics.PointF
import android.net.Uri
import com.palmapp.master.baselib.IView
import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject

interface TakephotoedView : IView {

    fun startScanAnima()
    fun stopScanAnima()
    fun faceDetectSuccess(contourPoints: MutableList<MutableList<PointF>>??, bitmap: Bitmap)
    fun faceDetectFail()
    fun fragmentInteraction(uri: Uri, data: DataObject?)
    fun showToast(resId: Int)
    fun enableClick()
}