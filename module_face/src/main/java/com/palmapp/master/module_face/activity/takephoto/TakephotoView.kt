package com.palmapp.master.module_face.activity.takephoto;

import com.palmapp.master.baselib.IView

/**
 * Created by zhangliang on 15-8-19.
 * 变老拍照页面
 */
interface TakephotoView : IView {

        var isStroagePermissionGranted: Boolean
        var isCameraPermissionGranted: Boolean

        fun startCamera()

        fun showFragmentDialog(permission:String)

        fun startTakephotoedFragment()

        fun startPicture()

        fun stopCamera()

        fun permissionDenide()
}