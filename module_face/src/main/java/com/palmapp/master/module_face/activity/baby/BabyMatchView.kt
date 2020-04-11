package com.palmapp.master.module_face.activity.baby;

import android.graphics.Bitmap
import com.palmapp.master.baselib.IView
import com.palmapp.master.baselib.bean.face.BabyGenerateRequest
import com.palmapp.master.baselib.bean.face.BabyGenerateResponse

interface BabyMatchView : IView {
    //图片异常
    fun showPicError()
    //脸部识别失败
    fun showFaceError(status:String?)

    fun showNetError()

    fun showLoadingDialog()

    fun dismissLoadingDialog()
    //跳转到摄像机
    fun showCamera(isFather:Boolean)

    //跳转到结果页
    fun showResult(request: BabyGenerateRequest,response: BabyGenerateResponse?)

    //展示爸爸的图片
    fun showFatherPic(bitmap: Bitmap)

    //展示妈妈的图片
    fun showMotherPic(bitmap: Bitmap)

    //展示loading页面
    fun showLoadingView()

    //展示默认页面
    fun showDefaultView()
}