package com.palmapp.master.module_palm.match;

import android.content.Intent
import android.graphics.Bitmap
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.bean.palm.PalmMatchResponse
import com.palmapp.master.baselib.constants.PreConstants
import com.palmapp.master.baselib.manager.GoPrefManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.view.CommonDialog
import com.palmapp.master.baselib.view.PermissionDialog
import com.palmapp.master.module_network.CustomException
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_palm.scan.PalmImageManager
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Scheduler
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.*

class PalmMatchLoadingPresenter : BasePresenter<PalmMatchLoadingView>() {
    override fun onAttach() {
    }

    override fun onDetach() {
    }

    fun startRequest(bitmap1: Bitmap, bitmap2: Bitmap) {
        if (GoPrefManager.getDefault().getBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, true)) {
            getView()?.getContext()?.let {
                val dialog = PermissionDialog(it)
                dialog.setOnClickBottomListener(object :CommonDialog.OnClickBottomListener{
                    override fun onPositiveClick() {
                        GoPrefManager.getDefault().putBoolean(PreConstants.First.KEY_FIRST_UPLOAD_PERMISSION, false)
                            .commit()
                        startRequest(bitmap1, bitmap2)
                    }

                    override fun onNegativeClick() {
                        getView()?.finishActivity()
                    }
                })
                dialog.show()
                return
            }
        }
        Observable.create<Boolean> {
            if (!PalmImageManager.isPalm(bitmap1) || !PalmImageManager.isPalm(bitmap2)) {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.palm_succ,
                    "2","1", "2"
                )
                it.onError(CustomException())
            } else {
                BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                    BaseSeq103OperationStatistic.palm_succ,
                    "2","1", "1"
                )
                it.onNext(true)
            }
            it.onComplete()
        }.flatMap { HttpClient.getOldCntRequest().getPalmMatch(Random().nextInt(12) + 1, Random().nextInt(12) + 1) }
            .compose(activityProvider?.bindToLifecycle())
            .compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<PalmMatchResponse>() {
                override fun onNext(t: PalmMatchResponse) {
                    getView()?.showResult(t)
                }

                override fun onServiceError() {
                    getView()?.showNetError()
                }

                override fun onNetworkError() {
                    getView()?.showNetError()
                }

                override fun onCustomError() {
                    getView()?.showPicError()
                }
            })

    }
}