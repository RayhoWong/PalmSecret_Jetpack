package com.palmapp.master.module_face.activity.animal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.opengl.GLES20
import android.os.Environment
import android.text.TextUtils
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import com.cs.bd.utils.ToastUtils
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.palmapp.master.baselib.BaseMultiplePresenter
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.bean.user.getCntCover
import com.palmapp.master.baselib.bean.user.getConstellationById
import com.palmapp.master.baselib.manager.ShareManager
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.FileUtils
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.utils.gifmaker.GifMaker
import com.palmapp.master.module_face.R
import com.palmapp.master.module_imageloader.AnimatedGifEncoder
import com.palmapp.master.module_imageloader.ImageLoader
import com.palmapp.master.module_imageloader.ImageLoaderUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.nio.IntBuffer
import java.util.*
import javax.microedition.khronos.opengles.GL10
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.roundToInt

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/7
 */
class AnimalResultPresenter(val activity: Activity) : BaseMultiplePresenter<IAnimalView>() {
    private class AnimalBean(val landmark: String, val animal: Int)

    private val ANIM_INTERVAL = 50
    private var humanSrc: Bitmap? = null
    private var humanLandmark: HashMap<String, List<Point>>? = null

    private var face: FirebaseVisionFace? = null

    private var mGlRendererController: GLRenderController? = null
    private var mWindowSurface: GLSurface? = null
    private var mPuffSurface: GLSurface? = null
    private var animalSrc: Bitmap? = null
    private var animalLandmark: HashMap<String, List<Point>>? = null
    private var animal: AnimalBean
    private val path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("animal.gif")
    private val share_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator).plus("share_animal.jpeg")
    private var mIsSavingGif = false

    init {
        val animals = SparseArray<AnimalBean>(15)
        animals.put(0, AnimalBean("animal/alpaca.landmark", R.mipmap.pic_animal_alpaca))
        animals.put(1, AnimalBean("animal/antelope.landmark", R.mipmap.pic_animal_antelope))
        animals.put(2, AnimalBean("animal/cat_ragdoll.landmark", R.mipmap.pic_animal_cat_ragdoll))
        animals.put(3, AnimalBean("animal/cat_shorthair.landmark", R.mipmap.pic_animal_cat_shorthair))
        animals.put(4, AnimalBean("animal/dog_german_shepherd_new.landmark", R.mipmap.pic_animal_demo2_german_shepherd))
        animals.put(5, AnimalBean("animal/dog_golden_retriever.landmark", R.mipmap.pic_animal_dog_golden_retriever_new))
        animals.put(6, AnimalBean("animal/dog_toy_poodle.landmark", R.mipmap.pic_animal_dog_toy_poodle))
        animals.put(7, AnimalBean("animal/face_contours_koalar.landmark", R.mipmap.pic_animal_koalar))
        animals.put(8, AnimalBean("animal/face_contours_lion.landmark", R.mipmap.pic_animal_lion))
        animals.put(9, AnimalBean("animal/face_contours_tiger.landmark", R.mipmap.pic_animal_tiger))
        animals.put(10, AnimalBean("animal/king_cobra_snake.landmark", R.mipmap.pic_animal_kingcobra_snake))
        animals.put(11, AnimalBean("animal/panda.landmark", R.mipmap.pic_animal_panda))
        animals.put(12, AnimalBean("animal/pic_animal_giraffe.landmark", R.mipmap.pic_animal_giraffe))
        animals.put(13, AnimalBean("animal/pig.landmark", R.mipmap.pic_animal_pig))
        animals.put(14, AnimalBean("animal/rabbit.landmark", R.mipmap.pic_animal_rabbit))

        animal = animals.get(Random().nextInt(15))
    }

    override fun onAttach(pView: IAnimalView, context: Context) {
        super.onAttach(pView, context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        File(path).delete()
        File(share_path).delete()
        EventBus.getDefault().unregister(this)
        mGlRendererController?.release()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onOrignEvent(orign: AnimalImageInfo) {
        humanSrc = orign.src
        face = orign.faces?.get(0)
        EventBus.getDefault().removeStickyEvent(orign)
    }

    private fun checkGLController(): Boolean {
        if (mGlRendererController?.renderer is AnimalRenderer) {
            return true
        }
        return false
    }

    private fun downloadFile(file: File) {
        file?.let {
            Observable
                .create<File> { emitter ->
                    //保存目录的目标文件
                    var destFile: File? = null
                    if (it.length() > 0) {
                        //保存到sdcard的自定义目录
                        val pictureFolder = Environment.getExternalStorageDirectory()
                        //第二个参数为你想要保存的目录名称
                        val appDir = File(pictureFolder, "PalmSecret/old")
                        if (!appDir.exists()) {
                            //创建目标文件目录
                            appDir.mkdirs()
                        }
                        val fileName = System.currentTimeMillis().toString() + "_old.jpg"
                        //创建目标文件实例
                        destFile = File(appDir, fileName)
                        //把得到图片复制到目标文件中
                        FileUtils.copyFile(it, destFile)
                    }
                    emitter.onNext(destFile!!)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer<File> { destFile ->
                    if (destFile.length() > 0) {
                        ToastUtils.makeEventToast(activity, activity.getString(R.string.download_successful), false)
                        // 最后通知图库更新
                        activity?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(File(destFile.path))))
                    } else {
                        ToastUtils.makeEventToast(activity, activity.getString(R.string.download_failed), false)
                    }
                })
        }
    }

    fun download() {
        BaseSeq103OperationStatistic
            .uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.download_func_a000, "", "5", "")
        share(false)
        downloadFile(File(share_path))
    }

    fun share(needToStart: Boolean = true) {
        animalSrc ?: return
        humanSrc ?: return
        face ?: return
        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
            BaseSeq103OperationStatistic.share_a000,
            "",
            "12",
            ""
        )
        val file = File(share_path)
        if (file.exists()) {
            if (!needToStart) {
                return
            }
            val uri =
                FileProvider.getUriForFile(GoCommonEnv.getApplication(), "${GoCommonEnv.applicationId}.fileprovider", file)
            var share_intent = Intent()
            share_intent.action = Intent.ACTION_SEND//设置分享行为
            share_intent.type = "image/jpeg"  //设置分享内容的类型
            share_intent.putExtra(Intent.EXTRA_STREAM, uri)
            share_intent.putExtra(Intent.EXTRA_TEXT, getView()?.getContext()?.getString(R.string.app_app_name))
            share_intent = Intent.createChooser(share_intent, "Share")
            share_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //创建分享的Dialog
            GoCommonEnv.getApplication().startActivity(share_intent)
            return
        }
        val animalWidth = 840
        val animalLeft = (animalSrc!!.width - animalWidth) / 2
        val animal = BitmapUtils.cropBitmap(animalSrc, animalLeft, animalLeft, animalWidth, animalWidth, false)
        val rect = face!!.boundingBox
        var human = BitmapUtils.cropBitmap(humanSrc, rect.left, rect.top, rect.width(), rect.height(), false)
        human = Bitmap.createScaledBitmap(human, animalWidth, animalWidth, false)
        val view = LayoutInflater.from(getView()?.getContext())
            .inflate(R.layout.face_activity_baby_share, null, false)
        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
            view.findViewById<View>(R.id.share_info_layout).visibility = View.INVISIBLE
        }

        val result = Bitmap.createBitmap(840, 840, Bitmap.Config.RGB_565)
        val canvas = Canvas(result)
        val paint = Paint()
        val rectF = RectF(0f, 0f, animalWidth.toFloat(), animalWidth.toFloat())
        paint.shader = BitmapShader(animal, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        canvas.drawArc(rectF, 90f, 180f, true, paint)

        paint.shader = BitmapShader(human, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        canvas.drawArc(rectF, 270f, 180f, true, paint)

        paint.shader = null
        paint.color = Color.WHITE
        paint.strokeWidth = 10f
        canvas.drawLine(rectF.centerX(), 0f, rectF.centerX(), rectF.height(), paint)
        view.findViewById<ImageView>(R.id.iv_left).setImageBitmap(animal)
        view.findViewById<ImageView>(R.id.iv_right).setImageBitmap(human)
        view.findViewById<ImageView>(R.id.iv_after).setImageBitmap(result)
        view.findViewById<ImageView>(R.id.iv_me)
            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
        view.findViewById<TextView>(R.id.tv_user_name).text = GoCommonEnv.userInfo?.name ?: ""
        view.findViewById<TextView>(R.id.tv_user_cnt).text =
            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
        ShareManager.share(view, getView()?.getContext()?.getString(R.string.app_app_name)
            ?: "", path = share_path, needToStart = needToStart)
    }

    fun generateGIF(
        onFinish: (success: Boolean, path: String) -> Unit = { s, p -> }) {
        mGlRendererController ?: return
        mWindowSurface?.apply {
            if (File(path).exists()) {
                onFinish(true, path)
                return
            }
            getView()?.showLoading()
            mIsSavingGif = true
            mPuffSurface = GLSurface(viewport.width,
                viewport.height)
            mGlRendererController?.removeSurface(mWindowSurface)
            mGlRendererController?.addSurface(mPuffSurface!!)
            ThreadExecutorProxy.execute(Runnable {
                val result: java.util.HashMap<Int, Bitmap> = java.util.HashMap()
                getAFrame(mGlRendererController!!, mPuffSurface!!, 0f) { bitmap: Bitmap?, finish: Boolean, progress: Int ->
                    if (bitmap != null) {
                        LogUtil.d("AnimalResultPresenter", "result size ： ${result.size}")
                        result.put(progress, bitmap)
                        if (finish) {
                            mIsSavingGif = false
                            mGlRendererController?.removeSurface(mPuffSurface)
                            mGlRendererController?.addSurface(mWindowSurface)
                            mPuffSurface = null
                            if (result.size >= 10) {
                                val ae = AnimatedGifEncoder()
                                ae.setDelay(150)
                                ae.setRepeat(0)
                                ae.start(path)
                                ae.setSize(bitmap.width, bitmap.height)
                                ae.addFrame(result.get(0)!!)
                                ae.addFrame(result.get(1)!!)
                                ae.addFrame(result.get(2)!!)
                                ae.addFrame(result.get(3)!!)
                                ae.addFrame(result.get(4)!!)
                                ae.addFrame(result.get(5)!!)
                                ae.addFrame(result.get(6)!!)
                                ae.addFrame(result.get(7)!!)
                                ae.addFrame(result.get(8)!!)
                                ae.addFrame(result.get(9)!!)
                                ae.addFrame(result.get(10)!!)
                                ae.addFrame(result.get(10)!!)
                                ae.addFrame(result.get(9)!!)
                                ae.addFrame(result.get(8)!!)
                                ae.addFrame(result.get(7)!!)
                                ae.addFrame(result.get(6)!!)
                                ae.addFrame(result.get(5)!!)
                                ae.addFrame(result.get(4)!!)
                                ae.addFrame(result.get(3)!!)
                                ae.addFrame(result.get(2)!!)
                                ae.addFrame(result.get(1)!!)
                                ae.finish()
                                ThreadExecutorProxy.runOnMainThread(Runnable {
                                    onFinish(true, path)
                                })
                            }
                        }
                    } else {
                        for (entry in result) {
                            result[entry.key]?.recycle()
                        }
                    }
                }
            })
        }
    }

    private fun getAFrame(glRenderController: GLRenderController, puffSurface: GLSurface, progress: Float, onFinish: (Bitmap?, Boolean, Int) -> Unit) {
        if (mIsSavingGif) {
            glRenderController.postRenderCallback(object : AbsGLThread.Callback {
                override fun renderBefore() {
                    if (mIsSavingGif) {
                        (glRenderController.renderer as AnimalRenderer).setProgress(progress)
                    }
                }

                override fun renderAfter() {
                    if (mIsSavingGif) {
                        getCurrentPuffBitmap(glRenderController, puffSurface) {
                            if (mIsSavingGif) {
                                if (progress >= 1f) {
                                    onFinish(it, true, (progress * 10).toInt())
                                } else {
                                    onFinish(it, false, (progress * 10).toInt())
                                    getAFrame(glRenderController, puffSurface, progress + 0.1f, onFinish)
                                }
                            }
                        }
                    } else {
                        onFinish(null, true, 0)
                    }
                }
            })
        }
    }

    private fun getCurrentPuffBitmap(glRenderController: GLRenderController,
                                     puffSurface: GLSurface,
                                     finish: (Bitmap?) -> Unit) {
        var bitmap: Bitmap? = null
        glRenderController.postRunnable {
            puffSurface?.let {
                val ib = IntBuffer.allocate(it.viewport.width * it.viewport.height)
                GLES20.glReadPixels(
                    0,
                    0,
                    it.viewport.width,
                    it.viewport.height,
                    GL10.GL_RGBA,
                    GL10.GL_UNSIGNED_BYTE,
                    ib
                )

                bitmap = BitmapUtils.compressByScale(
                    frameToBitmap(it.viewport.width, it.viewport.height, ib)
                    , it.viewport.width / 4, it.viewport.height / 4)
                finish(bitmap)
            }
        }
    }

    /**
     * 将数据转换成bitmap(OpenGL和Android的Bitmap色彩空间不一致，这里需要做转换)
     *
     * @param width 图像宽度
     * @param height 图像高度
     * @param ib 图像数据
     * @return bitmap
     */
    private fun frameToBitmap(width: Int, height: Int, ib: IntBuffer): Bitmap {
        val pixs = ib.array()
        // 扫描转置(OpenGl:左上->右下 Bitmap:左下->右上)
        for (y in 0 until height / 2) {
            for (x in 0 until width) {
                val pos1 = y * width + x
                val pos2 = (height - 1 - y) * width + x

                val tmp = pixs[pos1]
                pixs[pos1] = pixs[pos2] and -0xff0100 or (pixs[pos2] shr 16 and 0xff) or
                        (pixs[pos2] shl 16 and 0x00ff0000) // ABGR->ARGB
                pixs[pos2] = tmp and -0xff0100 or (tmp shr 16 and 0xff) or (tmp shl 16 and 0x00ff0000)
            }
        }
        if (height % 2 == 1) { // 中间一行
            for (x in 0 until width) {
                val pos = (height / 2 + 1) * width + x
                pixs[pos] = pixs[pos] and -0xff0100 or (pixs[pos] shr 16 and 0xff) or (pixs[pos] shl 16 and 0x00ff0000)
            }
        }

        return Bitmap.createBitmap(pixs, width, height, Bitmap.Config.ARGB_4444)
    }

    fun stop() {
        mGlRendererController?.stopRender()
    }

    fun start(sv_animal: SurfaceView) {
        mGlRendererController = GLRenderController(EGLEnvironment(), AnimalRenderer(activity))
        mGlRendererController?.startRender()
        val mSurfaceCallback = object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                LogUtil.d("AnimalResultActivity", "surfaceChanged")
                if (checkGLController()) {
                    if (mWindowSurface == null) {
                        mWindowSurface = GLSurface(holder.surface, width, height)
                    } else {
                        mWindowSurface?.set(holder.surface, width, height)
                    }

                    mWindowSurface?.let {
                        mGlRendererController?.addSurface(it)
                        initHumanRenderData()
                        initAnimalRenderData()
                        mGlRendererController?.requestRender()
                    }
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
            }
        }
        sv_animal.holder?.removeCallback(mSurfaceCallback)
        sv_animal.holder?.addCallback(mSurfaceCallback)
        sv_animal.visibility = View.VISIBLE
    }

    private fun initAnimalRenderData() {
        if (checkGLController() && animalSrc != null) {
            mGlRendererController?.postRenderCallback(object : AbsGLThread.Callback {
                override fun renderBefore() {
                    setRendererAnimalData(mGlRendererController?.renderer as AnimalRenderer)
                }

                override fun renderAfter() {
                    mGlRendererController?.render()
                    ThreadExecutorProxy.runOnMainThread(Runnable {
                        getView()?.onReadyToStart()
                    }, 100)
                }
            })
        }
    }

    private fun setRendererAnimalData(animalRenderer: AnimalRenderer) {
        animalRenderer.initTargetFrameBuffer(animalSrc,
            convertLandMarkToFloatArray(animalLandmark ?: HashMap(),
                animalSrc?.width ?: 0,
                animalSrc?.height ?: 0))
        animalRenderer.setProgress(0f)
    }

    private fun initHumanRenderData() {
        if (checkGLController() && humanSrc != null) {
            mGlRendererController?.postRenderRunnable {
                setRendererHumanData((mGlRendererController?.renderer as AnimalRenderer))
            }
        }
    }

    private fun setRendererHumanData(animalRenderer: AnimalRenderer) {
        animalRenderer.initSrcFrameBuffer(humanSrc,
            convertLandMarkToFloatArray(humanLandmark ?: HashMap(),
                humanSrc?.width ?: 0,
                humanSrc?.height ?: 0))
        animalRenderer.setProgress(0f)
    }

    private fun convertLandMarkToFloatArray(landmark: HashMap<String, List<Point>>, width: Int, height: Int): FloatArray {
        val result = java.util.ArrayList<Float>()
        for (key in AnimalImageInfo.KEY_ARRAY) {
            landmark[key]?.forEach {
                val x = width / 2f + it.x.toFloat()
                val y = height / 2f - it.y.toFloat()
                result.add(x)
                result.add(y)
            }
        }
        return result.toFloatArray()
    }

    private fun extractAllFaceLandmark(face: FirebaseVisionFace,
                                       landmark: HashMap<String, List<Point>>, xOffset: Int,
                                       yOffset: Int, scale: Float) {
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.LEFT_EYEBROW_TOP,
            AnimalImageInfo.EYE_BROW_FILTER_INDEXES,
            AnimalImageInfo.LEFT_EYEBROW_TOP)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP,
            AnimalImageInfo.EYE_BROW_FILTER_INDEXES,
            AnimalImageInfo.RIGHT_EYEBROW_TOP)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.LEFT_EYE,
            AnimalImageInfo.EYE_FILTER_INDEXES,
            AnimalImageInfo.LEFT_EYE)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.RIGHT_EYE,
            AnimalImageInfo.EYE_FILTER_INDEXES,
            AnimalImageInfo.RIGHT_EYE)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.NOSE_BRIDGE, null,
            AnimalImageInfo.NOSE_BRIDGE)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.NOSE_BOTTOM, null,
            AnimalImageInfo.NOSE_BOTTOM)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.UPPER_LIP_BOTTOM, null,
            AnimalImageInfo.UPPER_LIP_BOTTOM)
        extractFaceLandmark(face, landmark, xOffset, yOffset, scale,
            FirebaseVisionFaceContour.FACE,
            AnimalImageInfo.FACE_FILTER_INDEXES,
            AnimalImageInfo.FACE_OVAL)
    }

    private fun extractFaceLandmark(face: FirebaseVisionFace,
                                    landmark: HashMap<String, List<Point>>, xOffset: Int,
                                    yOffset: Int, scale: Float, firebaseContourType: Int,
                                    filterIndexArray: IntArray?, landmarkKey: String) {
        val contour = face.getContour(firebaseContourType)
        var points = contour.points
        filterIndexArray?.let { filterIndexes ->
            points = contour.points.filterIndexed { index, _ ->
                !filterIndexes.contains(index)
            }
        }
        val targetPoints = ArrayList<Point>(points.size)
        points.forEach {
            val x = (it.x - xOffset) * scale
            val y = -(it.y - yOffset) * scale
            val point = Point(x.toInt(), y.toInt())
            targetPoints.add(point)
        }
        landmark[landmarkKey] = targetPoints
    }

    fun loadAnimal(width: Float, height: Float) {
        getView()?.showLoading()
        humanSrc ?: return
        face ?: return
        ThreadExecutorProxy.execute(Runnable {
            var target = BitmapUtils.centerScaleBitmapForViewSize(humanSrc, width, height)
            var xOffset = (humanSrc!!.width / 2f).toInt()
            var yOffset = (humanSrc!!.height / 2f).toInt()
            var scale = 1f * target.width / humanSrc!!.width
            humanSrc = target
            humanLandmark = HashMap()
            face!!.boundingBox.set((face!!.boundingBox.left * scale).toInt(), (face!!.boundingBox.top * scale).toInt(), (face!!.boundingBox.right * scale).toInt(), (face!!.boundingBox.bottom * scale).toInt())
            extractAllFaceLandmark(face!!, humanLandmark!!, xOffset, yOffset, scale)
            val properties = Properties()
            properties.load(GoCommonEnv.getApplication().resources.assets.open(
                animal.landmark))
            val landmark = HashMap<String, List<Point>>()
            animalSrc = BitmapFactory.decodeResource(GoCommonEnv.getApplication().resources, animal.animal)
            target = BitmapUtils.centerScaleBitmapForViewSize(animalSrc, width, height)
            xOffset = (animalSrc!!.width / 2f).toInt()
            yOffset = (animalSrc!!.height / 2f).toInt()
            scale = 1f * target.width / animalSrc!!.width
            animalSrc = target
            properties.stringPropertyNames().forEach { key ->
                val points = ArrayList<Point>()
                landmark[key] = points
                val value = properties.getProperty(key)
                value.split(";").forEach { str ->
                    val pointStr = str.split("(", ",", ")").filter { it.isNotEmpty() }
                    val x = (pointStr[0].toInt() - xOffset) * scale
                    val y = -(pointStr[1].toInt() - yOffset) * scale
                    val point = Point(x.toInt(), y.toInt())
                    points.add(point)
                }
            }
            animalLandmark = landmark

            val maxWidth = max(animalSrc?.width ?: 0,
                humanSrc?.width ?: 0)
            val maxHeight = max(animalSrc?.height ?: 0,
                humanSrc?.height ?: 0)
            //保证动物和人类两张Bitmap的大小一致
            val humanRawBitmap = humanSrc!!
            if (humanRawBitmap.width < maxWidth || humanRawBitmap.height < maxHeight) {
                humanSrc = createFixedBitmap(humanRawBitmap, maxWidth, maxHeight)
            }
            val animalRawBitmap = animalSrc!!
            if (animalRawBitmap.width < maxWidth || animalRawBitmap.height < maxHeight) {
                animalSrc = createFixedBitmap(animalRawBitmap, maxWidth, maxHeight)
            }
            ThreadExecutorProxy.runOnMainThread(Runnable {
                getView()?.onAnimalLoadCompleted()
            })
        })
    }

    fun setProgress(progress: Int) {
        if (mIsSavingGif)
            return
        if (checkGLController()) {
            mGlRendererController?.postRenderRunnable {
                (mGlRendererController?.renderer as AnimalRenderer).setProgress(progress * 1f / ANIM_INTERVAL)
            }
        }
    }

    private fun createFixedBitmap(src: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val base = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(base)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.isFilterBitmap = true
        val l = (targetWidth - src.width) / 2f
        val t = (targetHeight - src.height) / 2f
        val r = l + src.width
        val b = t + src.height
        val rect = RectF(l, t, r, b)
        canvas.drawBitmap(src, null, rect, paint)
        return base
    }

}