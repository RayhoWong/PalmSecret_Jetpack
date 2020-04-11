package com.palmapp.master.module_transform.util

import android.content.Context
import android.graphics.*
import android.os.Build
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

/**
 * Created by huangweihao on 2019/12/9.
 */
object TransformManager {

    /**
     * 得到bitmap的大小
     */
    fun getBitmapSize(bitmap: Bitmap): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.allocationByteCount
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.byteCount
        }
        // 在低版本中用一行的字节x高度
        return bitmap.rowBytes * bitmap.height               //earlier version
    }


//    private val MODEL_FILE = "palm_recog_224_1_1.pb"
//
//    private val INPUT_NAME = "input_1"
//    private val OUTPUT_NAME = "output_0"
//
//    val INPUT_SIZE = 224
//
//    val MIN_LINE_LENGTH = 5
//
//    private var sTFInterface: TensorFlowInferenceInterface? = null
//
//    fun initialize(context: Context): Boolean {
//        OpenCVLoader.initDebug()
//        var input: InputStream?
//        try {
//            input = context.assets.open(MODEL_FILE)
//        } catch (e: IOException) {
//            input = null
//            e.printStackTrace()
//        }
//        if (input == null) {
//            return false
//        }
//        sTFInterface = TensorFlowInferenceInterface(input)
//        input.close()
//        if (sTFInterface == null) {
//            return false
//        }
//        return true
//    }
//
//    fun isInitialize(): Boolean {
//        return sTFInterface != null
//    }
//
//    @Synchronized
//    fun start(bitmap: Bitmap): Boolean {
//        if (!isInitialize()) {
//            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.palm_scan,"2","2")
//            return false
//        }
//        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
//        val w = scaleBitmap.width
//        val h = scaleBitmap.height
//        val mIntValues = IntArray(w * h)
//        val mFlatIntValues = FloatArray(w * h * 3)
//        val resultValues = FloatArray(w * h * 4)
//        scaleBitmap.getPixels(mIntValues, 0, w, 0, 0, w, h)
//        for (i in mIntValues.indices) {
//            val value = mIntValues[i]
//            mFlatIntValues[i * 3] = (value shr 16 and 0xFF) / 127.5f - 1  //r
//            mFlatIntValues[i * 3 + 1] = (value shr 8 and 0xFF) / 127.5f - 1 //g
//            mFlatIntValues[i * 3 + 2] = (value and 0xFF) / 127.5f - 1 //b
//        }
//        scaleBitmap.recycle()
//        sTFInterface?.feed(INPUT_NAME, mFlatIntValues, 1L, h.toLong(), w.toLong(), 3L)
//        try {
//            sTFInterface?.run(arrayOf(OUTPUT_NAME))
//        } catch (e: Exception) {
//        }
//
//        sTFInterface?.fetch(OUTPUT_NAME, resultValues)
//
//        val love = ArrayList<Point>()
//        val business = ArrayList<Point>()
//        val life = ArrayList<Point>()
//        for (i in 0 until INPUT_SIZE) {
//            for (j in 0 until INPUT_SIZE) {
//                val index = i * INPUT_SIZE * 4 + j * 4
//                love.addPoint(resultValues[index + 1], j, i)
//                business.addPoint(resultValues[index + 2], j, i)
//                life.addPoint(resultValues[index + 3], j, i)
//            }
//        }
//        if (love.isEmpty() || life.isEmpty() || business.isEmpty()) {
//            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.palm_scan,"3","2")
//            return false
//        }
//
//        return realStartToBitmap(ScanResult(love, business, life, bitmap.width.toDouble(), bitmap.height.toDouble()))
//    }
//
//    //判断是否为手掌
//    @Synchronized
//    fun isPalm(bitmap: Bitmap): Boolean {
//        if (!isInitialize())
//            return false
//        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false)
//        val w = scaleBitmap.width
//        val h = scaleBitmap.height
//        val mIntValues = IntArray(w * h)
//        val mFlatIntValues = FloatArray(w * h * 3)
//        val resultValues = FloatArray(w * h * 4)
//        scaleBitmap.getPixels(mIntValues, 0, w, 0, 0, w, h)
//        for (i in mIntValues.indices) {
//            val value = mIntValues[i]
//            mFlatIntValues[i * 3] = (value shr 16 and 0xFF) / 127.5f - 1  //r
//            mFlatIntValues[i * 3 + 1] = (value shr 8 and 0xFF) / 127.5f - 1 //g
//            mFlatIntValues[i * 3 + 2] = (value and 0xFF) / 127.5f - 1 //b
//        }
//        scaleBitmap.recycle()
//        sTFInterface?.feed(INPUT_NAME, mFlatIntValues, 1L, h.toLong(), w.toLong(), 3L)
//        try {
//            sTFInterface?.run(arrayOf(OUTPUT_NAME))
//        } catch (e: Exception) {
//        }
//
//        sTFInterface?.fetch(OUTPUT_NAME, resultValues)
//
//        val love = ArrayList<Point>()
//        val business = ArrayList<Point>()
//        val life = ArrayList<Point>()
//        for (i in 0 until INPUT_SIZE) {
//            for (j in 0 until INPUT_SIZE) {
//                val index = i * INPUT_SIZE * 4 + j * 4
//                life.addPoint(resultValues[index + 1], j, i)
//                business.addPoint(resultValues[index + 2], j, i)
//                love.addPoint(resultValues[index + 3], j, i)
//            }
//        }
//        LogUtils.d("PalmImageManager","Love:${love.size} Life:${life.size} Business:${business.size}")
//
//        if (love.isEmpty() || life.isEmpty() || business.isEmpty()) {
//            return false
//        }
//
//
//        if (love.size < 10 || life.size < 10 || business.size < 10) {
//            return false
//        }
//
//        return true
//    }
//
//    private fun ArrayList<Point>.addPoint(value: Float, x: Int, y: Int) {
//        if (value >= 0.25f) {
//            this.add(Point(x, y))
//        }
//    }

//    private fun realStartToBitmap(result: ScanResult): Boolean {
//        try {
//            val lifeBitmap = PalmImageManager.convertToBitmap(
//                result.life,
//                Scalar(0.toDouble(), 171.toDouble(), 137.toDouble(), 255.toDouble()),
//                result.width,
//                result.height
//            )
//            val businessBitmap = PalmImageManager.convertToBitmap(
//                result.business,
//                Scalar(0.toDouble(), 125.toDouble(), 196.toDouble(), 255.toDouble()),
//                result.width,
//                result.height
//            )
//            val loveBitmap = PalmImageManager.convertToBitmap(
//                result.love,
//                Scalar(255.toDouble(), 67.toDouble(), 98.toDouble(), 255.toDouble()),
//                result.width,
//                result.height
//            )
//            FileUtils.writeBitmap(
//                GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("life.png"),
//                lifeBitmap
//            )
//            FileUtils.writeBitmap(
//                GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("business.png"),
//                businessBitmap
//            )
//            FileUtils.writeBitmap(
//                GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("love.png"),
//                loveBitmap
//            )
//            return true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            BaseSeq101OperationStatistic.uploadOperationStatisticData(BaseSeq101OperationStatistic.palm_scan,"4","2")
//            return false
//        }
//    }
//
//    private fun convertToBitmap(points: ArrayList<Point>, color: Scalar, width: Double, height: Double): Bitmap {
//        val findContours = PalmImageManager.findContours(points, width, height)
//
//        //画最大轮廓
//        val result = Mat(
//            findContours.srcImageMat.size(),
//            CvType.CV_8UC4,
//            Scalar(0.toDouble(), 0.toDouble(), 0.toDouble(), 0.toDouble())
//        )
//        Imgproc.drawContours(
//            result, findContours.contours, findContours.maxIndex, color,
//            7, Imgproc.LINE_AA, Mat(), 0, org.opencv.core.Point()
//        )
//
//        val resultBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(result, resultBitmap)
//
//        return resultBitmap
//    }
//
//    private fun findContours(
//        points: ArrayList<Point>,
//        width: Double,
//        height: Double
//    ): PalmImageManager.FindContoursResult {
//        val createBitmap =
//            Bitmap.createBitmap(PalmImageManager.INPUT_SIZE, PalmImageManager.INPUT_SIZE, Bitmap.Config.ARGB_8888)
//
////        createBitmap.eraseColor(Color.TRANSPARENT);//填充颜色
//
//        val canvas = Canvas(createBitmap)
//        val paint = Paint()
//        paint.color = Color.parseColor("#ffffff")
//        points.forEach {
//            canvas.drawPoint(it.x.toFloat(), it.y.toFloat(), paint)
//        }
//
//
//        val srcImageMat = Mat()
//        Utils.bitmapToMat(createBitmap, srcImageMat, true)
//
//        Imgproc.resize(srcImageMat, srcImageMat, Size(width, height))
//
//        Imgproc.cvtColor(srcImageMat, srcImageMat, Imgproc.COLOR_RGBA2GRAY)
//
//        //膨化
//        val erosionSize = 4
//        val element = Imgproc.getStructuringElement(
//            Imgproc.MORPH_ELLIPSE,
//            Size((2 * erosionSize + 1).toDouble(), (2 * erosionSize + 1).toDouble())
//        )
//        Imgproc.dilate(srcImageMat, srcImageMat, element)
//
//        //二值化
//        Imgproc.threshold(srcImageMat, srcImageMat, 80.toDouble(), 255.toDouble(), Imgproc.THRESH_BINARY)
//
//        //细化获得骨架
//        Ximgproc.thinning(srcImageMat, srcImageMat, Ximgproc.THINNING_ZHANGSUEN)
//
//        //        val resultBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888)
//        //        Utils.matToBitmap(srcImageMat, resultBitmap)
//        //        return resultBitmap
//
//        //检索外部轮廓
//        val contours = ArrayList<MatOfPoint>()
//        Imgproc.findContours(srcImageMat, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE)
//
//        var maxIndex = -1
//        var maxArea = -1.0
//        LogUtils.e("yxq", "contours size = " + contours.size)
//        for (i in 0 until contours.size) {
//            val contour = contours[i]
//            val area = Imgproc.contourArea(contour)
//            LogUtils.e("yxq", "area = " + area)
//            if (area > maxArea) {
//                maxIndex = i
//                maxArea = area
//            }
//        }
//
//        return PalmImageManager.FindContoursResult(contours, maxIndex, srcImageMat)
//
//    }
//
//    class FindContoursResult(val contours: ArrayList<MatOfPoint>, val maxIndex: Int, val srcImageMat: Mat)
//    class ScanResult(
//        val love: ArrayList<Point>,
//        val business: ArrayList<Point>,
//        val life: ArrayList<Point>,
//        val width: Double,
//        val height: Double
//    ) {
//        fun isPalm(): Boolean {
//            return life.size > 10 && business.size > 10 && love.size > 10
//        }
//    }
}