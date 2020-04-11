//package com.palmapp.master.module_face.activity.takephoto.activity;
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.graphics.Color
//import android.graphics.drawable.AnimationDrawable
//import android.graphics.drawable.BitmapDrawable
//import android.net.Uri
//import android.os.Bundle
//import android.os.Environment
//import android.text.SpannableString
//import android.text.SpannableStringBuilder
//import android.text.Spanned
//import android.text.TextUtils
//import android.text.style.ForegroundColorSpan
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.TextView
//import com.alibaba.android.arouter.launcher.ARouter
//import com.cs.bd.utils.ToastUtils
//import com.palmapp.master.baselib.BaseFragment
//import com.palmapp.master.baselib.BaseMultipleMVPActivity
//import com.palmapp.master.baselib.GoCommonEnv
//import com.palmapp.master.baselib.bean.user.getCntCover
//import com.palmapp.master.baselib.bean.user.getConstellationById
//import com.palmapp.master.baselib.constants.PreConstants
//import com.palmapp.master.baselib.constants.RouterConstants
//import com.palmapp.master.baselib.manager.*
//import com.palmapp.master.baselib.manager.config.ConfigManager
//import com.palmapp.master.baselib.manager.config.TimeConfig
//import com.palmapp.master.baselib.proxy.BillingServiceProxy
//import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
//import com.palmapp.master.baselib.utils.AppUtil
//import com.palmapp.master.baselib.utils.FileUtils
//import com.palmapp.master.baselib.utils.PermissionUtil
//import com.palmapp.master.baselib.view.LikeDialog
//import com.palmapp.master.module_ad.HotRatingPresenter
//import com.palmapp.master.module_ad.RewardVideoPresenter
//import com.palmapp.master.module_ad.manager.ExitAdManager
//import com.palmapp.master.module_ad.manager.RewardAdManager
//import com.palmapp.master.module_face.R
//import com.palmapp.master.module_face.activity.takephoto.fragment.DataObject.FragmentInteractionListener
//import com.palmapp.master.module_face.activity.takephoto.fragment.old.OldPresenter
//import com.palmapp.master.module_face.activity.takephoto.fragment.old.OldView
//import com.palmapp.master.module_face.activity.takephoto.fragment.old.ShowGifActivity
//import com.palmapp.master.module_imageloader.AnimatedGifEncoder
//import com.yanzhenjie.permission.AndPermission
//import io.reactivex.Observable
//import io.reactivex.ObservableOnSubscribe
//import io.reactivex.Observer
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.disposables.Disposable
//import io.reactivex.functions.Consumer
//import io.reactivex.schedulers.Schedulers
//import kotlinx.android.synthetic.main.face_fragment_old.*
//import java.io.File
//import java.io.FileOutputStream
//import java.text.SimpleDateFormat
//
//
///**
// * Create by zhangliang
// *
// * 变老页面， 返回按钮和返回键 回到首页
// *
// */
//
//class OldActivity : BaseMultipleMVPActivity(), OldView, View.OnClickListener, OnTimerListener {
//    private var payView: View? = null
//    private var mAge: String? = null
//
//    private val oldPresenter = OldPresenter()
//    private val rewardVideoPresenter = RewardVideoPresenter(this)
//    private val hotRatingPresenter = HotRatingPresenter(this, rewardVideoPresenter)
//
//    val orignBitmap_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
//        .plus("orign_bitmap")
//    val bitmap50_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
//        .plus("bitmap_50")
//    val bitmap70_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
//        .plus("bitmap_70")
//    val bitmap90_path = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
//        .plus("bitmap_90")
//
//    private val face_gif = GoCommonEnv.getApplication().cacheDir.path.plus(File.separator)
//        .plus("face_gif.gif")
//
//    private var gif = File(face_gif)
//
//    companion object {
//        val ARG_PARAM1: String = "ARG_PARAM1"
//        val ARG_PARAM2: String = "ARG_PARAM2"
//        val ARG_PARAM3: String = "ARG_PARAM3"
//    }
//
//    override fun onClick(v: View?) {
//
//        if (v is Button) {
//            thumbnails?.let {
//                for (cv in thumbnails!!) {
//                    v?.let {
//                        cv.isSelected = cv.id == it.id
//                    }
//                }
//            }
//
//            thumbnails2?.let {
//                for (cv in thumbnails2!!) {
//                    v?.let {
//                        cv.isSelected = cv.id == it.id
//                    }
//                }
//            }
//        }
//
//        when (v?.id) {
//            R.id.iv_titlebar_back ->
//                super.onBackPressed()
//            R.id.face_50s -> {
//                mAge = "50"
//                face_image?.setImageBitmap(bitmap50)
//                finalBitmapFile = bitmap50File
//            }
//            R.id.face_70s -> {
//                mAge = "70"
//                face_image?.setImageBitmap(bitmap70)
//                finalBitmapFile = bitmap70File
//            }
//            R.id.face_90s -> {
//                mAge = "90"
//                face_image?.setImageBitmap(bitmap90)
//                finalBitmapFile = bitmap90File
//            }
////            R.id.face_tovip -> {
////                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).navigation()
////            }
//        }
//    }
//
//    private fun share() {
//        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
//            BaseSeq103OperationStatistic.share_a000,
//            "",
//            "1",
//            ""
//        )
//        val view = LayoutInflater.from(this)
//            .inflate(R.layout.face_activity_old_share, null, false)
//        val bitmap = (face_image.drawable as BitmapDrawable).bitmap
//        if (TextUtils.isEmpty(GoCommonEnv.userInfo?.name ?: "")) {
//            view.findViewById<View>(R.id.share_info_layout).visibility = View.INVISIBLE
//        }
//        originBitmap.let {
//            view.findViewById<ImageView>(R.id.iv_before).setImageBitmap(originBitmap)
//        }
//        bitmap?.let {
//            view.findViewById<ImageView>(R.id.iv_after).setImageBitmap(bitmap)
//        }
//        view.findViewById<TextView>(R.id.tv_age).text = mAge ?: "50"
//        view.findViewById<ImageView>(R.id.iv_me)
//            .setImageResource(getCntCover(GoCommonEnv.userInfo?.constellation ?: 1))
//        view.findViewById<TextView>(R.id.tv_user_name).text =
//            GoCommonEnv.userInfo?.name ?: ""
//        view.findViewById<TextView>(R.id.tv_user_cnt).text =
//            getConstellationById(GoCommonEnv.userInfo?.constellation ?: 1)
//
//        val download = view.findViewById<TextView>(R.id.tv_download)
//        try {
//            val str = getString(R.string.share_download) ?: ""
//            val index = str.indexOf("Palm Secret")
//            val builder = SpannableStringBuilder(str)
//            builder.setSpan(
//                ForegroundColorSpan(Color.parseColor("#c8e8ff")),
//                index,
//                index + "Palm Secret".length,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//            download.setText(builder)
//        } catch (e: Exception) {
//
//        }
//
//        ShareManager.share(view, getString(R.string.app_app_name) ?: "")
//    }
//
//
//    private var param1: String? = null
//    private var param2: String? = null
//    private var newPlan: String? = null
//    private var thumbnails: MutableList<Button>? = null
//    private var thumbnails2: MutableList<Button>? = null
//    var bitmap50: Bitmap? = null
//    var bitmap70: Bitmap? = null
//    var bitmap90: Bitmap? = null
//    var originBitmap: Bitmap? = null
//
//    private var bitmap50File: File? = null
//    private var bitmap70File: File? = null
//    private var bitmap90File: File? = null
//    private var finalBitmapFile: File? = null
//
//    private lateinit var mIvLike: ImageView
//    private lateinit var mIvShare_2: ImageView
//    private lateinit var mIvDownload: ImageView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.face_fragment_old)
//        RewardAdManager.addFreeUse()
//        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
//            BaseSeq103OperationStatistic.face_scan_pro,
//            "",
//            "4", ""
//        )
//        bitmap50 = BitmapFactory.decodeFile(bitmap50_path)
//        bitmap70 = BitmapFactory.decodeFile(bitmap70_path)
//        bitmap90 = BitmapFactory.decodeFile(bitmap90_path)
//        originBitmap = BitmapFactory.decodeFile(orignBitmap_path)
//        param1 = intent.getStringExtra(ARG_PARAM1)
//        param2 = intent.getStringExtra(ARG_PARAM2)
//        newPlan = intent.getStringExtra(ARG_PARAM3)
//
//        initView()
//    }
//
//    private fun initView() {
////        val time = ConfigManager.getConfig(TimeConfig::class.java)?.old_delay ?: 1000L
////        TimerManager.registerTimerListener(TIMER_OLD, this)
////        GoPrefManager.getDefault().putString(PreConstants.Palm.KEY_PALM_OLD_RESULT, "1")
////        TimerManager.startTimer(TIMER_OLD, time)
//        face_old_blur.entrance = "6"
//        face_50s?.setOnClickListener(this)
//        face_70s?.setOnClickListener(this)
//        face_90s?.setOnClickListener(this)
////        face_tovip.setOnClickListener(this)
//        iv_close_ticktok.setOnClickListener {
//            ll_ticktok.visibility = View.GONE
//        }
//        findViewById<ImageView>(R.id.iv_titlebar_back).setOnClickListener(this)
//        findViewById<TextView>(R.id.tv_titlebar_title)
//            .setText(getString(R.string.face_daily_title))
//
//        mIvLike = layout_share.findViewById<ImageView>(R.id.iv_like)
//        mIvShare_2 = layout_share.findViewById<ImageView>(R.id.iv_share)
//        mIvDownload = layout_share.findViewById<ImageView>(R.id.iv_download)
//        mIvDownload.visibility = View.VISIBLE
//
//        if (thumbnails == null) {
//            thumbnails = mutableListOf(face_50s, face_70s, face_90s)
//        }
//        face_50s?.performClick()
//
//        if (!BillingServiceProxy.isVip() && !RewardAdManager.isFreeUse()) {
//            ExitAdManager.loadAd(this)
//            if (newPlan == "1") {
//                if (GoCommonEnv.isFirstRunPayGuide) {
//                    val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
//                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
//                        .withString("entrance", config.scen).withString("type", "6").withString("newPlan", "1").navigation()
//                } else {
//                    val config = PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
//                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
//                        .withString("entrance", config.scen).withString("type", "6").withString("newPlan", "1").navigation()
//                }
//            } else {
//                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "6").navigation()
//            }
//        }
//        toUpdateUIWithVip(BillingServiceProxy.isVip() || RewardAdManager.isFreeUse(), false)
//
//        //点赞
//        mIvLike.setOnClickListener {
//            //帧动画
//            val animationDrawable = mIvLike.drawable as AnimationDrawable
//            animationDrawable.start()
//
//            ThreadExecutorProxy.runOnMainThread(
//                Runnable {
//                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.thumbs_up_a000)
//                    var isLiked = GoPrefManager.getDefault().getBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, false)
//                    if (!isLiked) {
//                        BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
//                            BaseSeq103OperationStatistic.rating_f000,
//                            "",
//                            "5",
//                            ""
//                        )
//                        var dialog = LikeDialog(this, "like")
//                        dialog?.show()
//                        GoPrefManager.getDefault().putBoolean(PreConstants.ScoreGuide.KEY_IS_FIRST_LIKE, true).commit()
//                    }
//
//                }, 1000)
//        }
//
//        mIvShare_2.setOnClickListener {
//            share()
//        }
//
//        mIvDownload.setOnClickListener {
//            checkPermission()
//        }
//
//        saveBitmaps()
//
//        iv_gif.setOnClickListener {
//            isGifTimeout()
//        }
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        TimerManager.unregisterTimerListener(TIMER_OLD, this)
//        TimerManager.resetTimer(TIMER_OLD)
//    }
//
//    override fun addPresenters() {
//        addToPresenter(oldPresenter)
//    }
//
//
//    private fun isGifTimeout() {
//        if (!TimerManager.hasFinishTimer(TIMER_OLD)) {
//            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.wait_old_a000, "1")
//            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.old_toast_f000)
//            ll_ticktok.visibility = View.VISIBLE
//        } else {
//            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.wait_old_a000, "2")
//            tv_point.visibility = View.INVISIBLE
//            startActivity(Intent(this, ShowGifActivity::class.java))
//        }
//    }
//
//
//    private fun saveBitmaps() {
//        ThreadExecutorProxy.execute(Runnable {
//
//            /*originBitmap?.let {
//                var file = File(orignBitmap_path)
//                if (!file.exists()) {
//                    file.createNewFile()
//                }
//                FileUtils.writeBitmap(file.path, it)
//            }
//            bitmap50?.let {
//                bitmap50File = File(bitmap50_path)
//                if (!bitmap50File!!.exists()) {
//                    bitmap50File!!.createNewFile()
//                }
//                //默认
//                finalBitmapFile = bitmap50File
//                FileUtils.writeBitmap(bitmap50File!!.path, it)
//            }
//            bitmap70?.let {
//                bitmap70File = File(bitmap70_path)
//                if (!bitmap70File!!.exists()) {
//                    bitmap70File!!.createNewFile()
//                }
//                FileUtils.writeBitmap(bitmap70File!!.path, it)
//            }
//            bitmap90?.let {
//                bitmap90File = File(bitmap90_path)
//                if (!bitmap90File!!.exists()) {
//                    bitmap90File!!.createNewFile()
//                }
//                FileUtils.writeBitmap(bitmap90File!!.path, it)
//            }*/
//            val bitmaps = arrayOf(originBitmap, bitmap50, bitmap70, bitmap90)
//            Observable
//                .create(ObservableOnSubscribe<File> {
//                    if (bitmaps.size == 4) {
//                        var originBitmap = AppUtil.resizeBitmap(bitmaps[0], 500, 500)
//                        var bitmap50 = AppUtil.resizeBitmap(bitmaps[1], 500, 500)
//                        var bitmap70 = AppUtil.resizeBitmap(bitmaps[2], 500, 500)
//                        var bitmap90 = AppUtil.resizeBitmap(bitmaps[3], 500, 500)
//                        if (!gif.exists()) {
//                            gif.createNewFile()
//                        } else {
//                            gif.delete()
//                            gif.createNewFile()
//                        }
//                        var outputFileName = FileOutputStream(gif)
//                        val ae = AnimatedGifEncoder()
//                        ae.setSize(originBitmap.width, originBitmap.height)
//                        ae.start(outputFileName)
//                        ae.setRepeat(0)
//                        ae.setDelay(1000)
//                        ae.addFrame(originBitmap)
//                        ae.addFrame(bitmap50)
//                        ae.addFrame(bitmap70)
//                        ae.addFrame(bitmap90)
//                        ae.finish()
//                        outputFileName.close()
//                        it.onNext(gif)
//                    }
//                })
//                .compose(bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<File> {
//                    override fun onNext(t: File) {
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Log.d("ShowGif", e.toString())
////                        ToastUtils.makeEventToast(this@ShowGifActivity, e.toString(), false)
//                    }
//
//                    override fun onComplete() {
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//
//                    }
//
//                })
//
//        })
//    }
//
//    private fun checkPermission() {
//        AndPermission.with(this)
//            .runtime()
//            .permission(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE)
//            .onGranted {
//                //已获取权限
//                saveImage()
//            }
//            .onDenied { data ->
//                //判断用户是不是选中不再显示权限弹窗了，若不再显示的话提醒进入权限设置页
//                if (AndPermission.hasAlwaysDeniedPermission(this, data)) { //提醒打开权限设置页
//                    PermissionUtil.GoToSetting(this)
//                } else {
//                    ToastUtils.makeEventToast(this, "Permisssion Denied!", false)
//                }
//            }.start()
//    }
//
//
//    @SuppressLint("CheckResult")
//    private fun saveImage() {
//        BaseSeq103OperationStatistic
//            .uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.download_func_a000, "", "1", "")
//        finalBitmapFile?.let {
//            Observable
//                .create<File> { emitter ->
//                    //保存目录的目标文件
//                    var destFile: File? = null
//                    if (it.length() > 0) {
//                        //保存到sdcard的自定义目录
//                        val pictureFolder = Environment.getExternalStorageDirectory()
//                        //第二个参数为你想要保存的目录名称
//                        val appDir = File(pictureFolder, "PalmSecret/old")
//                        if (!appDir.exists()) {
//                            //创建目标文件目录
//                            appDir.mkdirs()
//                        }
//                        val fileName = System.currentTimeMillis().toString() + "_old.jpg"
//                        //创建目标文件实例
//                        destFile = File(appDir, fileName)
//                        //把得到图片复制到目标文件中
//                        FileUtils.copyFile(it, destFile)
//                    }
//                    emitter.onNext(destFile!!)
//                }
//                .compose(bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(Consumer<File> { destFile ->
//                    if (destFile.length() > 0) {
//                        ToastUtils.makeEventToast(this, getString(R.string.download_successful), false)
//                        // 最后通知图库更新
//                        this?.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                            Uri.fromFile(File(destFile.path))))
//                    } else {
//                        ToastUtils.makeEventToast(this, getString(R.string.download_failed), false)
//                    }
//                })
//        }
//    }
//
//
//    override fun toUpdateUIWithVip(isVip: Boolean, isUnlock: Boolean) {
//        if (!isVip) {
//            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.show_result_a000, "", "1", "")
//        }
//        /*if (isVip) {
//            listener?.onFragmentInteraction(
//                Uri.Builder().appendPath("goto.SkipVip").build(),
//                null
//            )
//        }
//        if (isUnlock) {
//            listener?.onFragmentInteraction(
//                Uri.Builder().appendPath("goto.UnlockVip").build(),
//                null
//            )
//        }*/
//        payView?.visibility = if (isVip) View.GONE else View.VISIBLE
//        face_old_blur?.visibility = if (isVip) View.INVISIBLE else View.VISIBLE
//        face_old_blur?.findViewById<View>(R.id.btn_pay)?.setOnClickListener {
//            ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY).withString("entrance", "6").navigation()
//        }
//        face_old_blur?.setWatchClickListener(View.OnClickListener {
//            oldPresenter?.onClickWatchAd()
//        })
//        layout_share?.visibility = if (isVip) View.VISIBLE else View.INVISIBLE
//
//        if (isVip) {
//            val time = ConfigManager.getConfig(TimeConfig::class.java)?.old_delay ?: 1000L
//            TimerManager.registerTimerListener(TIMER_OLD, this)
//            GoPrefManager.getDefault().putString(PreConstants.Palm.KEY_PALM_OLD_RESULT, "1")
//            TimerManager.startTimer(TIMER_OLD, time)
//        }
//        iv_gif.visibility = if (isVip) View.VISIBLE else View.INVISIBLE
//        tv_point.visibility = if (isVip && TimerManager.hasFinishTimer(TIMER_OLD)) View.VISIBLE else View.INVISIBLE
//        if (isVip) {
//            saveBitmaps()
//        }
//    }
//
//    override fun onFinish() {
//        tv_point.visibility = View.VISIBLE
//        ll_ticktok.visibility = View.GONE
//    }
//
//    private val formatter = SimpleDateFormat("HH:mm:ss") //初始化Formatter的转换格式。
//
//    init {
//        formatter.timeZone = java.util.TimeZone.getTimeZone("GMT+00:00")
//    }
//
//    val foreground = ForegroundColorSpan(Color.parseColor("#FFBB59"))
//    private var startIndex = 0
//    private var endIndex = 0
//    override fun onTick(remainTimeMillis: Long) {
//        val time = formatter.format(remainTimeMillis)
//        val text = getString(R.string.dialog_oldgif_content, time)
//        val sp = SpannableString(text)
//        if (startIndex == 0 && endIndex == 0) {
//            startIndex = text.indexOf(time)
//            endIndex = startIndex + time.length
//        }
//        sp.setSpan(foreground, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        tv_time.text = sp
//    }
//
//    override fun onTimerCreate() {
//        if (ll_ticktok != null)
//            ll_ticktok.visibility = if (BillingServiceProxy.isVip() && !TimerManager.hasFinishTimer(TIMER_OLD)) View.VISIBLE else View.INVISIBLE
//    }
//
//    override fun onTimerDestroy() {
//    }
//
//}