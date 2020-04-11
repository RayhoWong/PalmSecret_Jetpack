package com.palmapp.master.baselib.manager

import android.graphics.Bitmap
import android.view.View
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.utils.FileUtils
import java.io.File
import android.content.Intent
import android.content.Intent.*
import android.graphics.Canvas
import android.widget.ScrollView
import androidx.core.content.FileProvider


/**
 *
 * @author :     xiemingrui
 * @since :      2019/8/27
 */
object ShareManager {
    private val IMAGE_WIDTH = 1080
    private val IMAGE_HEIGHT = 1920
    private val IMG_PATH =
        GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("shareImage.jpeg")

    fun share(v: View, text: String, width: Int = IMAGE_WIDTH, height: Int = IMAGE_HEIGHT, path: String = IMG_PATH, needToStart: Boolean = true) {
//        BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.share_a000,"",pos.toString())
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        v.measure(widthMeasureSpec, heightMeasureSpec)
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        var h = v.measuredHeight
        var w = v.measuredWidth
        if (v is ScrollView) {
            h = v.getChildAt(0).measuredHeight
            w = v.getChildAt(0).measuredWidth
        }
        val bitmap = Bitmap.createBitmap(
            w, h,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        if (v is ScrollView) {
            v.getChildAt(0).draw(canvas)
        } else {
            v.draw(canvas)
        }
        FileUtils.writeBitmap(path, bitmap)
        if (!needToStart) {
            return
        }
        val file = File(path)
        val uri =
            FileProvider.getUriForFile(GoCommonEnv.getApplication(), "${GoCommonEnv.applicationId}.fileprovider", file)
        var share_intent = Intent()
        share_intent.action = ACTION_SEND//设置分享行为
        share_intent.type = "image/jpeg"  //设置分享内容的类型
        share_intent.putExtra(Intent.EXTRA_STREAM, uri)
        share_intent.putExtra(Intent.EXTRA_TEXT, text)
        share_intent = Intent.createChooser(share_intent, "Share")
        share_intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        //创建分享的Dialog
        GoCommonEnv.getApplication().startActivity(share_intent)
    }
}