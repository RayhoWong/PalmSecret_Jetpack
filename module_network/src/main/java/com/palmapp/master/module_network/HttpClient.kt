package com.palmapp.master.module_network

import com.palmapp.master.module_network.signature.DesUtil
import okhttp3.Request
import okio.Buffer
import java.io.IOException


/**
 *
 * @ClassName:      HttpClient
 * @Description:
 * @Author:         xiemingrui
 * @CreateDate:     2019/7/24
 */
object HttpClient {
    @Volatile
    private var tarotRequest: TarotRequest? = null

    @Volatile
    private var cntRequest: CntRequest? = null

    @Volatile
    private var faceRequest: FaceRequest? = null

    @Volatile
    private var faceRequest2: FaceRequest2? = null

    @Volatile
    private var confRequest: ConfRequest? = null

    @Volatile
    private var feedbackRequest: FeedbackRequest? = null

    @Volatile
    private var oldCntRequest: OldCntRequest? = null

    @Synchronized
    fun getTarotRequest(): TarotRequest {
        if (tarotRequest == null) {
            tarotRequest = RequestFactory.createRequest(TarotRequest::class.java)
        }

        return tarotRequest!!
    }

    @Synchronized
    fun getOldCntRequest():OldCntRequest{
        if (oldCntRequest == null) {
            oldCntRequest = RequestFactory.createRequest(OldCntRequest::class.java)
        }

        return oldCntRequest!!
    }

    @Synchronized
    fun getCntRequest(): CntRequest {
        if (cntRequest == null) {
            cntRequest = RequestFactory.createRequest(CntRequest::class.java)
        }

        return cntRequest!!
    }

    @Synchronized
    fun getFaceRequest(): FaceRequest {
        if (faceRequest == null) {
            faceRequest = RequestFactory.createRequest(FaceRequest::class.java)
        }

        return faceRequest!!
    }

    @Synchronized
    fun getFaceRequest2(): FaceRequest2 {
        if (faceRequest2 == null) {
            faceRequest2 = RequestFactory.createRequest(FaceRequest2::class.java)
        }

        return faceRequest2!!
    }

    @Synchronized
    fun getConfRequest(): ConfRequest {
        if (confRequest == null) {
            confRequest = RequestFactory.createRequest(ConfRequest::class.java)
        }

        return confRequest!!
    }

    @Synchronized
    fun getFeedbackRequest(): FeedbackRequest {
        if (feedbackRequest == null) {
            feedbackRequest = RequestFactory.createRequest(FeedbackRequest::class.java)
        }
        return feedbackRequest!!
    }

    fun bodyToString(request: Request, key: String): String {

        try {
            val buffer = Buffer()
            request.body()?.writeTo(buffer)
            val string = buffer.readUtf8()
            return DesUtil.decrypt(string, key)
        } catch (e: IOException) {
            return ""
        }

    }
}