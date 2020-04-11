package com.palmapp.master.module_face.activity.baby;

import android.graphics.Bitmap
import com.example.oldlib.old.FaceDetect
import com.example.oldlib.old.FaceDetectHelper
import com.palmapp.master.baselib.BasePresenter
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.UploadImageInfo
import com.palmapp.master.baselib.constants.AppConstants
import com.palmapp.master.module_network.HttpClient
import com.palmapp.master.module_network.NetworkSubscriber
import com.palmapp.master.module_network.NetworkTransformer
import com.palmapp.master.module_palm.AmazonS3Manger
import com.palmapp.master.baselib.amazon.KeyCreator.Companion.TYPE_FACE_DETECT
import com.palmapp.master.baselib.bean.face.*
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.File
import java.util.concurrent.TimeUnit

class BabyMatchPresenter : BasePresenter<BabyMatchView>() {
    private var fatherBitmap: Bitmap? = null
    private var motherBitmap: Bitmap? = null
    private val statusCode = hashMapOf<String, String>()

    init {
        statusCode.put("FACE_NOT_FOUND", "6")
        statusCode.put("BAD_FACE", "1")
        statusCode.put("TIME_LIMIT", "2")
        statusCode.put("TEMPLATE_NOT_FOUND", "3")
        statusCode.put("FACE_EMPTY", "4")
        statusCode.put("LOCAL_FACE_ERROR", "5")
    }

    private var babyRequest = BabyGenerateRequest()
    override fun onAttach() {
    }

    override fun onDetach() {
    }

    fun uploadPic() {
        if (!babyRequest.isReady()) {
            getView()?.showPicError()
            return
        }
        getView()?.showLoadingView()
        HttpClient.getFaceRequest().generateBaby(babyRequest)
            .compose(NetworkTransformer.toMainSchedulers())
            .subscribe(object : NetworkSubscriber<BabyGenerateResponse>() {
                override fun onNext(response: BabyGenerateResponse) {
                    if (response.isSuccess() == true) {
                        getView()?.showResult(babyRequest, response)
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.face_scan,
                            "2",
                            "1",""
                        )
                    } else {
                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                            BaseSeq103OperationStatistic.face_scan,
                            "2",
                            "2",""
                        )
                        getView()?.showFaceError(statusCode.get(response.status_result?.status_code))
                    }
                }

                override fun onError(t: Throwable) {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.face_scan,
                        "2",
                        "3",""
                    )
                    getView()?.showNetError()
                    getView()?.showDefaultView()

                }
            })
    }

    fun uploadFatherPic(bitmap: Bitmap) {
        fatherBitmap = bitmap
        getView()?.showLoadingDialog()
        val file = File(AppConstants.FACE_BABY_FATHER)
        val info = UploadImageInfo()
        info.file = file
        info.width = bitmap.width
        info.height = bitmap.height
        info.type = TYPE_FACE_DETECT
        FaceDetect.faceDetectToContourPoint(bitmap, object : FaceDetect.IContourPointListener {
            override fun onFailure() {
                getView()?.showFaceError(statusCode.get("LOCAL_FACE_ERROR"))
                getView()?.dismissLoadingDialog()
            }

            override fun success(contourPoints: FloatArray?) {
                AmazonS3Manger.uploadImage(info, object : AmazonS3Manger.UploadListener {
                    override fun onUploadCompleted(imageInfo: S3ImageInfo, imageUrl: String) {
                        val request = FaceIdentityRequest()
                        request.image = imageInfo
                        HttpClient.getFaceRequest().detectFace(request)
                            .compose(NetworkTransformer.toMainSchedulers())
                            .subscribe(object : NetworkSubscriber<FaceIdentityResponse>() {
                                override fun onNext(t: FaceIdentityResponse) {
                                    val fatherInfo = t.face_info?.getOrNull(0)

                                    if (fatherInfo != null) {
                                        babyRequest.father_face_rectangle = FaceRectangle(
                                            fatherInfo.top,
                                            fatherInfo.left,
                                            fatherInfo.width,
                                            fatherInfo.height
                                        )
                                        babyRequest.father_img = imageInfo
                                        getView()?.showFatherPic(bitmap)
                                    } else {
                                        getView()?.showFaceError(statusCode.get(t.status_result?.status_code))
                                    }
                                }

                                override fun onError(t: Throwable) {
                                    super.onError(t)
                                    getView()?.showNetError()
                                }

                                override fun onFinish() {
                                    getView()?.dismissLoadingDialog()
                                }
                            })
                    }

                    override fun onUploadProgress(percent: Int) {

                    }

                    override fun onUploadError(errorCode: Int) {
                        getView()?.showNetError()
                        getView()?.dismissLoadingDialog()
                    }
                })
            }
        })
    }

    fun uploadMotherPic(bitmap: Bitmap) {
        motherBitmap = bitmap
        getView()?.showLoadingDialog()
        val file = File(AppConstants.FACE_BABY_MOTHER)
        val info = UploadImageInfo()
        info.file = file
        info.width = bitmap.width
        info.height = bitmap.height
        info.type = TYPE_FACE_DETECT
        FaceDetect.faceDetectToContourPoint(bitmap, object : FaceDetect.IContourPointListener {
            override fun onFailure() {
                getView()?.showFaceError(statusCode.get("LOCAL_FACE_ERROR"))
                getView()?.dismissLoadingDialog()
            }

            override fun success(contourPoints: FloatArray?) {
                AmazonS3Manger.uploadImage(info, object : AmazonS3Manger.UploadListener {
                    override fun onUploadCompleted(imageInfo: S3ImageInfo, imageUrl: String) {
                        val request = FaceIdentityRequest()
                        request.image = imageInfo
                        HttpClient.getFaceRequest().detectFace(request)
                            .compose(NetworkTransformer.toMainSchedulers())
                            .subscribe(object : NetworkSubscriber<FaceIdentityResponse>() {
                                override fun onNext(t: FaceIdentityResponse) {
                                    val motherInfo = t.face_info?.getOrNull(0)

                                    if (motherInfo != null) {
                                        babyRequest.mother_face_rectangle = FaceRectangle(
                                            motherInfo.top,
                                            motherInfo.left,
                                            motherInfo.width,
                                            motherInfo.height
                                        )
                                        babyRequest.mother_img = imageInfo
                                        babyRequest.ethnicity = motherInfo.ethnicity.toString()
                                        getView()?.showMotherPic(bitmap)
                                    } else {
                                        getView()?.showFaceError(statusCode.get(t.status_result?.status_code))
                                    }
                                }

                                override fun onError(t: Throwable) {
                                    super.onError(t)
                                    getView()?.showNetError()
                                }

                                override fun onFinish() {
                                    getView()?.dismissLoadingDialog()
                                }
                            })
                    }

                    override fun onUploadProgress(percent: Int) {

                    }

                    override fun onUploadError(errorCode: Int) {
                        getView()?.showNetError()
                        getView()?.dismissLoadingDialog()

                    }
                })
            }
        })

    }
}