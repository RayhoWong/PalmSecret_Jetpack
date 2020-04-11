package com.palmapp.master.module_palm.scan;

import android.graphics.*
import com.alibaba.android.arouter.launcher.ARouter
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.palm.IdentityRequestV2
import com.palmapp.master.baselib.bean.palm.IdentityResponseV2
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.*
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.*
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_palm.R
import java.io.File


class PalmScanPresenter : BaseMultiplePresenter<PalmScanView>() {
    val TAG = "PalmScanPresenter"
    val scale = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("scale")
    var bitmap: Bitmap? = null

    //开始上传手相和分析掌纹
    fun uploadPalm(bitmap: Bitmap?) {
        this.bitmap = bitmap

        bitmap?.let { src ->
            getView()?.showScan()
            ThreadExecutorProxy.execute(Runnable {
                //step1->裁剪图片并分析
                LogUtil.d(TAG, "分析图片")
                val result = PalmImageManager.isPalmAndGetPoint(bitmap)
                if (result != null) {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.palm_succ, "1", "1", "1")
                    val request = IdentityRequestV2()
                    request.type_content = ArrayList()
                    request.type_content?.add(result.life_length)
                    request.type_content?.add(result.business_length)
                    request.type_content?.add(result.love_pos)
                    request.type_content?.add(result.money_pos)
                    request.type_content?.add(result.marry_type)
                    request.type_content?.add(result.thumb_length)
                    request.type_content?.add(result.shizhi_length)
                    request.type_content?.add(result.fuck_length)
                    request.type_content?.add(result.wuming_length)
                    request.type_content?.add(result.small_length)
                    request.type_content?.add(result.palm_type)
                    HttpClient.getFaceRequest().predictPalm(request).compose(NetworkTransformer.toMainSchedulers())
                        .subscribe(object : NetworkSubscriber<IdentityResponseV2>() {
                            override fun onNext(t: IdentityResponseV2) {
                                val cache = PalmResultCache()
                                cache.finger_length = result.finger_length
                                cache.palm_length = result.palm_length
                                cache.palm_width = result.palm_width
                                val temp = HashMap<String, String>()
                                t.data?.let {
                                    for (data in it) {
                                        temp.put(data.code, data.content)
                                    }
                                }
                                cache.life_length = temp.get(result.life_length) ?: ""
                                cache.business_length = temp.get(result.business_length) ?: ""
                                cache.love_pos = temp.get(result.love_pos) ?: ""
                                cache.money_pos = temp.get(result.money_pos) ?: ""
                                cache.marry_type = temp.get(result.marry_type) ?: ""
                                cache.thumb_length = temp.get(result.thumb_length) ?: ""
                                cache.shizhi_length = temp.get(result.shizhi_length) ?: ""
                                cache.fuck_length = temp.get(result.fuck_length) ?: ""
                                cache.wuming_length = temp.get(result.wuming_length) ?: ""
                                cache.small_length = temp.get(result.small_length) ?: ""
                                var palmType = ""
                                when (result.palm_type) {
                                    "2000" -> palmType = GoCommonEnv.getApplication().getString(R.string.palm_detail_2000)
                                    "2001" -> palmType = GoCommonEnv.getApplication().getString(R.string.palm_detail_2001)
                                    "2002" -> palmType = GoCommonEnv.getApplication().getString(R.string.palm_detail_2002)
                                    "2003" -> palmType = GoCommonEnv.getApplication().getString(R.string.palm_detail_2003)
                                    "2004" -> palmType = GoCommonEnv.getApplication().getString(R.string.palm_detail_2004)
                                }
                                cache.palm_style = palmType
                                cache.palm_type = temp.get(result.palm_type)
                                cache.randomName()
                                GoPrefManager.getDefault().putString(PreConstants.Palm.KEY_PALM_RESULT, GoGson.toJson(cache))
                                getView()?.showResult(bitmap,cache)

                            }

                            override fun onError(t: Throwable) {
                                super.onError(t)
                                getView()?.showNetWorkError()
                            }
                        })
                } else {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.palm_succ, "1", "1", "2")
                    getView()?.showPicError()
                }
            })
        }
    }

}