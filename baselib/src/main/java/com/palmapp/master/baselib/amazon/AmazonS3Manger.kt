package com.palmapp.master.module_palm

import android.content.Context
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.S3ClientOptions
import com.amazonaws.services.s3.model.ObjectMetadata
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.amazon.KeyCreator
import com.palmapp.master.baselib.bean.S3ImageInfo
import com.palmapp.master.baselib.bean.UploadImageInfo
import com.palmapp.master.baselib.manager.DebugProxy
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.MachineUtil
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.math.BigInteger
import java.nio.channels.FileChannel
import java.security.MessageDigest

const val AMAZON_UPLOAD_FAIL_NETWORK_ERROR = 400
const val AMAZON_UPLOAD_FAIL_FILE_NO_EXISTS = 404
const val AMAZON_UPLOAD_FAIL_AMAZON = 401

object AmazonS3Manger {

    private val S3_BUCKET = "secret-s3-data"
    private val S3_REGION = "us-west-2"
    private val IDENTITY_POOL_ID =
        "us-west-2:c18b1910-2abb-4632-886b-bd06cfe9a0e6"
    private val IDENTITY_POOL_REGION = "us-west-2"
    private val BASE_SERVER_URL =
        if (DebugProxy.isDebug()) "http://faccore.3g.net.cn" else "http://faccore.palm-secret.com"

    private val keyCreator = KeyCreator()
    private var credProvider: CognitoCachingCredentialsProvider? = null
    private var s3Client: AmazonS3Client? = null
    private var transferUtility: TransferUtility

    init {
        val context: Context = GoCommonEnv.getApplication()
        transferUtility = TransferUtility.builder().context(context).s3Client(getS3Client(context)).build()
    }

    private fun getCredProvider(context: Context): CognitoCachingCredentialsProvider {
        if (credProvider == null) {
            credProvider = CognitoCachingCredentialsProvider(
                context.applicationContext,
                IDENTITY_POOL_ID,
                Regions.fromName(IDENTITY_POOL_REGION)
            )
        }
        return credProvider!!
    }

    private fun getS3Client(context: Context): AmazonS3Client {
        if (s3Client == null) {
            s3Client = AmazonS3Client(
                getCredProvider(context.applicationContext),
                Region.getRegion(Regions.fromName(S3_REGION))
            )
            s3Client?.setS3ClientOptions(
                S3ClientOptions.builder().build()
            )
        }
        return s3Client!!
    }

    fun uploadImage(uploadImageInfo: UploadImageInfo, listener: UploadListener?) {
        if (!MachineUtil.isNetworkOK(GoCommonEnv.getApplication())) {
            listener?.onUploadError(AMAZON_UPLOAD_FAIL_NETWORK_ERROR)
            return
        }
        if (uploadImageInfo.file == null || uploadImageInfo.file.isDirectory || !uploadImageInfo.file.exists()) {
            listener?.onUploadError(AMAZON_UPLOAD_FAIL_FILE_NO_EXISTS)
            return
        }
        val amazonKey = createFaceAmazonKey(uploadImageInfo.type)
        val eTag = getMd5ByFile(uploadImageInfo.file)
        val metadata = ObjectMetadata()
        metadata.contentType = "image/jpeg"
        LogUtil.startMoniter()
        val uploadObserver = transferUtility.upload(S3_BUCKET, amazonKey, uploadImageInfo.file, metadata)
        uploadObserver.setTransferListener(object : TransferListener {

            private var hasHandleError = false

            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED === state) {
                    LogUtil.i("GsonRequest", "upload image completed: " + LogUtil.recodingTime("upload"))
                    val imageInfo = S3ImageInfo()
                    imageInfo.key = amazonKey
                    imageInfo.etag = eTag
                    imageInfo.imageWidth = uploadImageInfo.width
                    imageInfo.imageHeight = uploadImageInfo.height
                    listener?.onUploadCompleted(imageInfo, BASE_SERVER_URL + amazonKey)
                } else if (TransferState.FAILED == state) {
                    if (!hasHandleError) {
                        LogUtil.i("GsonRequest", "upload failed: " + LogUtil.recodingTime("upload"))
                        listener?.onUploadError(AMAZON_UPLOAD_FAIL_AMAZON)
                        hasHandleError = true
                    }
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                val percentDoneF = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDoneF.toInt()
                listener?.onUploadProgress(percentDone)
            }

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
                if (!hasHandleError) {
                    LogUtil.i("Test", "upload failed: " + LogUtil.recodingTime("upload"))
                    listener?.onUploadError(AMAZON_UPLOAD_FAIL_AMAZON)
                    hasHandleError = true
                }
            }

        })
    }

    /**
     * 获取文件的Md5值
     *
     * @param file
     * @return
     */
    private fun getMd5ByFile(file: File): String {
        var value = ""
        var inputStream: FileInputStream? = null
        try {
            inputStream = FileInputStream(file)
            val byteBuffer = inputStream.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(byteBuffer)
            val bi = BigInteger(1, md5.digest())
            value = bi.toString(16)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return value
    }

    /**
     * 生成Key
     * @param type
     * @return
     */
    private fun createFaceAmazonKey(type: Int): String {
        return keyCreator.createAmazonKey(type)
    }

    interface UploadListener {
        fun onUploadCompleted(imageInfo: S3ImageInfo, imageUrl: String)
        fun onUploadProgress(percent: Int)
        fun onUploadError(errorCode: Int)
    }
}
