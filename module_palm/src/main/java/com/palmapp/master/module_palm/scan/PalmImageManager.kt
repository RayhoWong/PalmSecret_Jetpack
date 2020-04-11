package com.palmapp.master.module_palm.scan

import android.content.Context
import android.graphics.*
import android.graphics.Point
import com.cs.bd.commerce.util.LogUtils
import com.palmapp.master.InterpreterHelper
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.statistics.BaseSeq101OperationStatistic
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.module_palm.PalmCalcUtils
import com.palmapp.master.module_palm.scan.PalmResult.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.opencv.ximgproc.Ximgproc
import org.tensorflow.lite.Interpreter
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 *  掌纹识别管理类
 * @author :     xiemingrui
 * @since :      2019/8/14
 */
object PalmImageManager {


    val INPUT_SIZE = 192
    val HAVEHAND_INPUT_SIZE = 192
    private val OFFSET = 5

    private var palmRecog: Interpreter? = null
    private var haveHand: Interpreter? = null
    private var pointHand: Interpreter? = null

    fun initialize(context: Context) {
        OpenCVLoader.initDebug()
    }

    @Synchronized
    fun start(bitmap: Bitmap, width: Int, height: Int, inputValue: ByteBuffer): Array<ArrayList<PointF>> {
        val resultValues = Array(INPUT_SIZE) { Array(INPUT_SIZE) { FloatArray(6) } }
        try {
            palmRecog = InterpreterHelper.decryptModel(GoCommonEnv.getApplication(), "palm_recog_5lines_192_input_1_output_0_v1_encrypt")
            palmRecog?.run(inputValue, resultValues)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            palmRecog?.close()
            palmRecog = null
        }
        val love = ArrayList<PointF>()
        val business = ArrayList<PointF>()
        val life = ArrayList<PointF>()
        val money = ArrayList<PointF>()
        val marry = ArrayList<PointF>()
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val index = 0
                life.addPoint(resultValues[i][j][index + 1], j, i)
                money.addPoint(resultValues[i][j][index + 2], j, i)
                business.addPoint(resultValues[i][j][index + 3], j, i)
                love.addPoint(resultValues[i][j][index + 4], j, i)
                marry.addPoint(resultValues[i][j][index + 5], j, i)
            }
        }
        if (life.isEmpty() || love.isEmpty() || business.isEmpty()) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.palm_succ, "1", "2", "2")
        } else {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                BaseSeq103OperationStatistic.palm_succ, "1", "2", "2")
        }
        realStartToBitmap(bitmap,
            ScanResult(
                love,
                business,
                life,
                money, marry,
                width.toDouble(),
                height.toDouble()
            )
        )
        return arrayOf(life, marry, love, business, money)
    }

    //判断是否为手掌
    @Synchronized
    fun isPalm(bitmap: Bitmap): Boolean {
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, HAVEHAND_INPUT_SIZE, HAVEHAND_INPUT_SIZE, false)
        val w = scaleBitmap.width
        val h = scaleBitmap.height
        val mIntValues = IntArray(w * h)
        val mFlatIntValues = ByteBuffer.allocateDirect(HAVEHAND_INPUT_SIZE * HAVEHAND_INPUT_SIZE * 3 * 4)
        mFlatIntValues.order(ByteOrder.nativeOrder())
        val resultValues = FloatArray(1)
        scaleBitmap.getPixels(mIntValues, 0, w, 0, 0, w, h)
        for (i in mIntValues.indices) {
            val value = mIntValues[i]
            mFlatIntValues.putFloat((value shr 16 and 0xFF) / 127.5f - 1)//r
            mFlatIntValues.putFloat((value shr 8 and 0xFF) / 127.5f - 1) //g
            mFlatIntValues.putFloat((value and 0xFF) / 127.5f - 1) //b
        }
        scaleBitmap.recycle()
        try {
            haveHand = InterpreterHelper.decryptModel(GoCommonEnv.getApplication(), "have_hand_192_input_1_output_0_seed1236545_encrypt")
            haveHand?.run(mFlatIntValues, resultValues)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            haveHand?.close()
            haveHand = null
        }
        mFlatIntValues.clear()
        LogUtil.d("PalmImageManager", "${resultValues[0]}")
        val result = resultValues[0] > 0.5f
        return result
    }

    fun getPoint(bitmap: Bitmap): List<ArrayList<PointF>>? {
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, HAVEHAND_INPUT_SIZE, HAVEHAND_INPUT_SIZE, false)
        val w = scaleBitmap.width
        val h = scaleBitmap.height
        val mIntValues = IntArray(w * h)
        val mFlatIntValues = ByteBuffer.allocateDirect(HAVEHAND_INPUT_SIZE * HAVEHAND_INPUT_SIZE * 3 * 4)
        mFlatIntValues.order(ByteOrder.nativeOrder())
        val resultValues = FloatArray(1)
        scaleBitmap.getPixels(mIntValues, 0, w, 0, 0, w, h)
        for (i in mIntValues.indices) {
            val value = mIntValues[i]
            mFlatIntValues.putFloat((value shr 16 and 0xFF) / 127.5f - 1)//r
            mFlatIntValues.putFloat((value shr 8 and 0xFF) / 127.5f - 1) //g
            mFlatIntValues.putFloat((value and 0xFF) / 127.5f - 1) //b
        }
        try {
            haveHand = InterpreterHelper.decryptModel(GoCommonEnv.getApplication(), "have_hand_192_input_1_output_0_seed1236545_encrypt")
            haveHand?.run(mFlatIntValues, resultValues)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            haveHand?.close()
            haveHand = null
        }
        LogUtil.d("PalmImageManager", "${resultValues[0]}")
        val isPalm = resultValues[0] > 0.5f
        if (!isPalm) {
            mFlatIntValues.clear()
            return null
        }

        val pointResult = FloatArray(46)

        try {
            pointHand = InterpreterHelper.decryptModel(GoCommonEnv.getApplication(), "palm_align_192_input_1_output_0_model_1_encrypt")
            pointHand?.run(mFlatIntValues, pointResult)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pointHand?.close()
            pointHand = null
        }

        val points = start(bitmap, bitmap.width, bitmap.height, mFlatIntValues)
        val list = ArrayList<ArrayList<PointF>>()
        for(pointArray in points) {
            list.add(pointArray)
        }
        list.add(covertToPoints(pointResult))
        return  list
    }

    fun isPalmAndGetPoint(bitmap: Bitmap): PalmResult? {
        val scaleBitmap = Bitmap.createScaledBitmap(bitmap, HAVEHAND_INPUT_SIZE, HAVEHAND_INPUT_SIZE, false)
        val w = scaleBitmap.width
        val h = scaleBitmap.height
        val mIntValues = IntArray(w * h)
        val mFlatIntValues = ByteBuffer.allocateDirect(HAVEHAND_INPUT_SIZE * HAVEHAND_INPUT_SIZE * 3 * 4)
        mFlatIntValues.order(ByteOrder.nativeOrder())
        val resultValues = FloatArray(1)
        scaleBitmap.getPixels(mIntValues, 0, w, 0, 0, w, h)
        for (i in mIntValues.indices) {
            val value = mIntValues[i]
            mFlatIntValues.putFloat((value shr 16 and 0xFF) / 127.5f - 1)//r
            mFlatIntValues.putFloat((value shr 8 and 0xFF) / 127.5f - 1) //g
            mFlatIntValues.putFloat((value and 0xFF) / 127.5f - 1) //b
        }
        try {
            haveHand = InterpreterHelper.decryptModel(GoCommonEnv.getApplication(), "have_hand_192_input_1_output_0_seed1236545_encrypt")
            haveHand?.run(mFlatIntValues, resultValues)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            haveHand?.close()
            haveHand = null
        }
        LogUtil.d("PalmImageManager", "${resultValues[0]}")
        val isPalm = resultValues[0] > 0.5f
        if (!isPalm) {
            mFlatIntValues.clear()
            return null
        }

        val pointResult = FloatArray(46)

        try {
            pointHand = InterpreterHelper.decryptModel(GoCommonEnv.getApplication(), "palm_align_192_input_1_output_0_model_1_encrypt")
            pointHand?.run(mFlatIntValues, pointResult)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            pointHand?.close()
            pointHand = null
        }
        val points = start(bitmap, bitmap.width, bitmap.height, mFlatIntValues)
        points[0].forEach { it.y = INPUT_SIZE - it.y }
        points[1].forEach { it.y = INPUT_SIZE - it.y }
        points[2].forEach { it.y = INPUT_SIZE - it.y }
        points[3].forEach { it.y = INPUT_SIZE - it.y }
        points[4].forEach { it.y = INPUT_SIZE - it.y }

        val life = points[0]
        val marry = points[1]
        val love = points[2]
        val business = points[3]
        val money = points[4]
        val keyPoints = covertToPoints(pointResult)
        val result = PalmResult()

        //1、判断生命线长短
        val x2 = PalmCalcUtils.getVerticalLine(keyPoints[4], keyPoints[11])
        val x3 = PalmCalcUtils.getVerticalLine(keyPoints[11], keyPoints[12])
        val xA = PalmCalcUtils.getCenterPoint(keyPoints[4], keyPoints[11])
        val xB = PalmCalcUtils.getCenterPoint(keyPoints[11], keyPoints[12])
        var minLifeX = HAVEHAND_INPUT_SIZE.toFloat()
        var maxLifeX = 0f
        var minLifeY = HAVEHAND_INPUT_SIZE.toFloat()
        var maxLifeY = 0f
        for (i in 0 until life.size) {
            minLifeX = Math.min(life[i].x, minLifeX)
            maxLifeX = Math.max(life[i].x, maxLifeX)
            minLifeY = Math.min(life[i].y, minLifeY)
            maxLifeY = Math.max(life[i].y, maxLifeY)
        }

        var lifePoints = arrayOf(PointF(minLifeX, minLifeY), PointF(maxLifeX, maxLifeY))

        if (life.isNotEmpty()) {
            result.life_length = if (PalmCalcUtils.isIntersection(x2[0], x2[1], lifePoints[0], lifePoints[1])) KEY_LIFE_LONG else KEY_LIFE_SHORT
        } else {
            result.life_length = KEY_LIFE_SHORT
        }
        minLifeX = HAVEHAND_INPUT_SIZE.toFloat()
        maxLifeX = 0f
        minLifeY = HAVEHAND_INPUT_SIZE.toFloat()
        maxLifeY = 0f
        for (i in 0 until business.size) {
            minLifeX = Math.min(business[i].x, minLifeX)
            maxLifeX = Math.max(business[i].x, maxLifeX)
            minLifeY = Math.min(business[i].y, minLifeY)
            maxLifeY = Math.max(business[i].y, maxLifeY)
        }
        lifePoints = arrayOf(PointF(minLifeX, minLifeY), PointF(maxLifeX, maxLifeY))
        //2、判断智慧线长短
        if (business.isNotEmpty()) {
            result.business_length = if (PalmCalcUtils.isIntersection(x2[0], x2[1], lifePoints[0], lifePoints[1])) KEY_BUSINESS_LONG else KEY_BUSINESS_LONG
        } else {
            result.business_length = KEY_BUSINESS_SHORT
        }

        //3、判断感情线位置
        when {
            PalmCalcUtils.isContain(keyPoints[3], xA, love.get(0)) -> result.love_pos = KEY_LOVE_SHIZHI
            PalmCalcUtils.isContain(xA, xB, love.get(0)) -> result.love_pos = KEY_LOVE_FUCK
            PalmCalcUtils.isContain(xB, keyPoints[20], love.get(0)) -> result.love_pos = KEY_LOVE_WUMING
            else -> result.love_pos = KEY_LOVE_SHIZHI
        }
        //4、判断事业线位置
        result.money_pos = KEY_MONEY_LESS
        if (money.isNotEmpty()) {
            val lastMoneyPoint = money[0]
            //有事业线
            val temp = business.filter {
                it.x < lastMoneyPoint.x
            }
            val moneyUp = temp.filter { lastMoneyPoint.y - it.y >= OFFSET }
            val moneyEq = temp.filter { Math.abs(lastMoneyPoint.y - it.y) < OFFSET }

            when {
                moneyUp.size > OFFSET -> result.money_pos = KEY_MONEY_HIGH
                moneyEq.size > OFFSET -> result.money_pos = KEY_MONEY_EQULE
                else -> result.money_pos = KEY_MONEY_LESS
            }
        }

        //5、判断婚姻线位置
        if (marry.size <= OFFSET) {
            result.marry_type = KEY_MARRY_NONE
        } else {
            result.marry_type = KEY_MARRY_HAS
        }

        //6、判断拇指长度
        val x4 = PalmCalcUtils.getVerticalLineByO(keyPoints[2], keyPoints[0])[1]
        val c5 = PalmCalcUtils.getVerticalLineByO(keyPoints[5], keyPoints[4])
        val x5 = PalmCalcUtils.getCenterPoint(c5[0], c5[1])

        if (x4.y <= x5.y) {
            result.thumb_length = KEY_THUMB_SHORT
        } else {
            result.thumb_length = KEY_THUMB_LONG
        }

        //7、判断食指长度
        val x8 = PalmCalcUtils.getVerticalLineByO(keyPoints[7], keyPoints[4])[1]
        val x10 = keyPoints[9]
        val xD = PalmCalcUtils.getCenterPoint(keyPoints[8], keyPoints[9])
        val x16 = PalmCalcUtils.getVerticalLineByO(keyPoints[15], keyPoints[12])[1]
        val x9 = keyPoints[8]

        when {
            x8.y < x10.y -> result.shizhi_length = KEY_SHIZHI_MAX_SHORT
            x8.y < xD.y -> result.shizhi_length = KEY_SHIZHI_SHORT
            xD.y < x8.y && x8.y < x16.y -> result.shizhi_length = KEY_SHIZHI_LONG
            x16.y < x8.y && x8.y < x9.y -> result.shizhi_length = KEY_SHIZHI_MAX_LONG
            else -> result.shizhi_length = KEY_SHIZHI_LONG
        }

        //8、判断中指长度
        val a = PalmCalcUtils.getDistance(keyPoints[8], keyPoints[11])
        val b = PalmCalcUtils.getDistance(keyPoints[20], keyPoints[21]) * 0.8f
        when {
            abs(a - b) <= OFFSET -> result.fuck_length = KEY_FUCK_EQULE
            a < b -> result.fuck_length = KEY_FUCK_SHORT
            a > b -> result.fuck_length = KEY_FUCK_LONG
        }

        //9、判断无名指长度
        val xE = (keyPoints[9].y + keyPoints[8].y) * 2 / 3
        when {
            x16.y < x8.y -> result.wuming_length = KEY_WUMING_SHORT
            x8.y < x16.y && x16.y < xE -> result.wuming_length = KEY_WUMING_EQULE
            x16.y > xE -> result.wuming_length = KEY_WUMING_LONG
        }

        //10、判断尾指长度
        val x17 = PalmCalcUtils.getVerticalLineByO(keyPoints[16], keyPoints[19])[1]
        val x15 = PalmCalcUtils.getVerticalLineByO(keyPoints[14], keyPoints[12])[1]
        when {
            abs(x17.y - x15.y) <= OFFSET -> result.small_length = KEY_SMALL_EQULE
            x17.y < x15.y -> result.small_length = KEY_SMALL_SHORT
            x17.y > x15.y -> result.small_length = KEY_SMALL_LONG
        }

        //11、判断手手掌形状
        val xA1 = PalmCalcUtils.getDistance(keyPoints[8], keyPoints[11])
        val xB1 = PalmCalcUtils.getDistance(keyPoints[3], keyPoints[20])
        val xC1 = PalmCalcUtils.getDistance(keyPoints[20], keyPoints[21])

        when {
            abs(xA1 - xB1) <= OFFSET && abs(xB1 - xC1) <= OFFSET && abs(xA1 - xC1) <= OFFSET -> result.palm_type = KEY_PALM_EQULE
            xB1 > xA1 && xB1 > xC1 -> result.palm_type = KEY_PALM_BIG
            xC1 > xA1 && xC1 > xB1 -> result.palm_type = KEY_PALM_SMALL
            xA1 > xB1 && xA1 > xC1 -> result.palm_type = KEY_PALM_LONG
            xA1 < xB1 && xA1 > xC1 -> result.palm_type = KEY_PALM_SHORT
        }

        //判断食指长度、手掌宽度，手掌长度
        result.finger_length = xA1.roundToInt()
        result.palm_width = xB1.roundToInt()
        result.palm_length = xC1.roundToInt()

        LogUtil.d("PalmImageManager", result.toString())
        return result
    }

    private fun ArrayList<PointF>.addPoint(value: Float, x: Int, y: Int) {
        if (value >= 0.25f) {
            this.add(PointF(x.toFloat(), y.toFloat()))
        }
    }

    private fun covertToPoints(points: FloatArray): ArrayList<PointF> {
        val result = ArrayList<PointF>(points.size / 2)
        for (i in 0 until points.size / 2) {
            val y = points[i + 23]
            val x = points[i]
            result.add(PointF(x, y))
        }

        return result
    }

    private fun createBitmapFormPoints(bitmap: Bitmap, w: Int, h: Int, points: ArrayList<Point>): Bitmap {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.textSize = 6f
        paint.textAlign = Paint.Align.CENTER
        for (i in 0 until points.size) {
            canvas.drawText("$i", points.get(i).x.toFloat(), points.get(i).y.toFloat(), paint)
        }
        return Bitmap.createScaledBitmap(bitmap, w, h, false)
    }

    private fun realStartToBitmap(bitmap: Bitmap, result: ScanResult): Boolean {
        try {
            val colors = arrayOf(Scalar(0.toDouble(), 171.toDouble(), 137.toDouble(), 255.toDouble()), Scalar(0.toDouble(), 125.toDouble(), 196.toDouble(), 255.toDouble())
                , Scalar(255.toDouble(), 67.toDouble(), 98.toDouble(), 255.toDouble()), Scalar(255.toDouble(), 180.toDouble(), 0.toDouble(), 255.toDouble()), Scalar(0.toDouble(), 225.toDouble(), 255.toDouble(), 255.toDouble()))
            val points = arrayOf(result.life, result.business, result.love, result.marry, result.money)
            val resultBitmap = convertToBitmap(bitmap, points, colors, result.width, result.height)
            FileUtils.writeBitmap(
                GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("palmResult.png"),
                resultBitmap
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    public fun realStartToBitmap(width: Double, height: Double,array: ArrayList<PointF>, scalar: Scalar): Bitmap? {
        try {
            val colors = arrayOf(scalar)
            val points = arrayOf(array)
            return convertToBitmap(null, points, colors, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun convertToBitmap(
        bitmap: Bitmap?,
        points: Array<ArrayList<PointF>>,
        color: Array<Scalar>,
        width: Double,
        height: Double
    ): Bitmap {
        val resultBitmap =
            Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        for (i in 0 until points.size) {
            val findContours = findContours(points.get(i), width, height)
            //画最大轮廓
            val result = Mat(
                findContours.srcImageMat.size(),
                CvType.CV_8UC4,
                Scalar(0.toDouble(), 0.toDouble(), 0.toDouble(), 0.toDouble())
            )
            Imgproc.drawContours(
                result, findContours.contours, findContours.maxIndex, color.get(i),
                7, Imgproc.LINE_AA, Mat(), 0, org.opencv.core.Point()
            )
            val temp = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(result, temp)
            canvas.drawBitmap(temp, 0f, 0f, null)
        }
        return resultBitmap
    }

    private fun findContours(
        points: ArrayList<PointF>,
        width: Double,
        height: Double
    ): PalmImageManager.FindContoursResult {
        val createBitmap =
            Bitmap.createBitmap(
                PalmImageManager.INPUT_SIZE,
                PalmImageManager.INPUT_SIZE,
                Bitmap.Config.ARGB_8888
            )

//        createBitmap.eraseColor(Color.TRANSPARENT);//填充颜色

        val canvas = Canvas(createBitmap)
        val paint = Paint()
        paint.color = Color.parseColor("#ffffff")
        points.forEach {
            canvas.drawPoint(it.x.toFloat(), it.y.toFloat(), paint)
        }


        val srcImageMat = Mat()
        Utils.bitmapToMat(createBitmap, srcImageMat, true)

        Imgproc.resize(srcImageMat, srcImageMat, Size(width, height))

        Imgproc.cvtColor(srcImageMat, srcImageMat, Imgproc.COLOR_RGBA2GRAY)

        //膨化
        val erosionSize = 4
        val element = Imgproc.getStructuringElement(
            Imgproc.MORPH_ELLIPSE,
            Size((2 * erosionSize + 1).toDouble(), (2 * erosionSize + 1).toDouble())
        )
        Imgproc.dilate(srcImageMat, srcImageMat, element)

        //二值化
        Imgproc.threshold(
            srcImageMat,
            srcImageMat,
            80.toDouble(),
            255.toDouble(),
            Imgproc.THRESH_BINARY
        )

        //细化获得骨架
        Ximgproc.thinning(srcImageMat, srcImageMat, Ximgproc.THINNING_ZHANGSUEN)

        //        val resultBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888)
        //        Utils.matToBitmap(srcImageMat, resultBitmap)
        //        return resultBitmap

        //检索外部轮廓
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(
            srcImageMat,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_NONE
        )

        var maxIndex = -1
        var maxArea = -1.0
        LogUtils.e("yxq", "contours size = " + contours.size)
        for (i in 0 until contours.size) {
            val contour = contours[i]
            val area = Imgproc.contourArea(contour)
            LogUtils.e("yxq", "area = " + area)
            if (area > maxArea) {
                maxIndex = i
                maxArea = area
            }
        }

        return PalmImageManager.FindContoursResult(contours, maxIndex, srcImageMat)

    }

    class FindContoursResult(
        val contours: ArrayList<MatOfPoint>,
        val maxIndex: Int,
        val srcImageMat: Mat
    )

    class ScanResult(
        val love: ArrayList<PointF>,
        val business: ArrayList<PointF>,
        val life: ArrayList<PointF>,
        val money: ArrayList<PointF>,
        val marry: ArrayList<PointF>,
        val width: Double,
        val height: Double
    ) {
        fun isPalm(): Boolean {
            return life.size > 10 && business.size > 10 && love.size > 10
        }
    }
}