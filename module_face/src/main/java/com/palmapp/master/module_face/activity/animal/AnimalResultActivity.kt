package com.palmapp.master.module_face.activity.animal

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.FileProvider
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.cs.bd.utils.ToastUtils
import com.palmapp.master.baselib.BaseMultipleMVPActivity
import com.palmapp.master.baselib.GoCommonEnv
import com.palmapp.master.baselib.constants.RouterConstants
import com.palmapp.master.baselib.manager.PayGuideManager
import com.palmapp.master.baselib.manager.RouterServiceManager.goHomeAndClearTop
import com.palmapp.master.baselib.manager.ThreadExecutorProxy
import com.palmapp.master.baselib.permission.DownloadPresenter
import com.palmapp.master.baselib.permission.IDownloadView
import com.palmapp.master.baselib.permission.IPermissionView
import com.palmapp.master.baselib.permission.PermissionPresenter
import com.palmapp.master.baselib.proxy.BillingServiceProxy
import com.palmapp.master.baselib.statistics.BaseSeq103OperationStatistic
import com.palmapp.master.baselib.utils.AppUtil
import com.palmapp.master.baselib.utils.LogUtil
import com.palmapp.master.baselib.view.LoadingDialog
import com.palmapp.master.module_ad.HotRatingPresenter
import com.palmapp.master.module_ad.IHotRatingView
import com.palmapp.master.module_ad.IRewardVideoView
import com.palmapp.master.module_ad.RewardVideoPresenter
import com.palmapp.master.module_ad.manager.ExitAdManager
import com.palmapp.master.module_face.R
import com.palmapp.master.module_face.activity.takephoto.fragment.old.ShowGifActivity
import com.palmapp.master.module_imageloader.AnimatedGifEncoder
import kotlinx.android.synthetic.main.face_activity_animal_result.*
import java.io.File

/**
 *
 * @author :     xiemingrui
 * @since :      2020/4/7
 */
@Route(path = RouterConstants.ACTIVITY_ANIMAL_RESULT)
class AnimalResultActivity : BaseMultipleMVPActivity(), IAnimalView, IRewardVideoView, IHotRatingView, IPermissionView, IDownloadView {
    @JvmField
    @Autowired(name = "newPlan")
    var newPlan = ""
    private val rewardVideoPresenter = RewardVideoPresenter(this)
    private val hotRatingPresenter = HotRatingPresenter(this, rewardVideoPresenter)
    private val animalResultPresenter = AnimalResultPresenter(this)
    private val permissionPresenter = PermissionPresenter(this)
    private val downloadPresenter = DownloadPresenter(permissionPresenter, this)
    private var loadingDialog: LoadingDialog? = null
    override fun addPresenters() {
        addToPresenter(animalResultPresenter)
        addToPresenter(rewardVideoPresenter)
        addToPresenter(hotRatingPresenter)
        addToPresenter(permissionPresenter)
        addToPresenter(downloadPresenter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.face_activity_animal_result)
        findViewById<View>(R.id.iv_titlebar_back).setOnClickListener { onBackPressed() }
        findViewById<TextView>(R.id.tv_titlebar_title).text =
            getString(R.string.home_animal_title)
        val gif = findViewById<ImageView>(R.id.iv_titlebar_icon)
        val download = findViewById<View>(R.id.iv_download)
        val hotRating = findViewById<ImageView>(R.id.iv_like)
        val share = findViewById<View>(R.id.iv_share)
        download.visibility = View.VISIBLE
        download.setOnClickListener {
            if (rewardVideoPresenter.isCanUseFun)
                animalResultPresenter.download()
        }
        share.setOnClickListener {
            if (rewardVideoPresenter.isCanUseFun)
                animalResultPresenter.share()
        }
        hotRating.setOnClickListener {
            if (!rewardVideoPresenter.isCanUseFun)
                return@setOnClickListener
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.like_a000,"","5","")
            val animationDrawable = hotRating.drawable as AnimationDrawable
            animationDrawable.start()

            ThreadExecutorProxy.runOnMainThread(
                Runnable {
                    BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(
                        BaseSeq103OperationStatistic.thumbs_up_a000
                    )
                    hotRatingPresenter.showLikeDialog()
                }, 1000
            )
        }
        gif.setImageResource(R.mipmap.old_result_ic_gif)
        gif.setOnClickListener {
            if (!rewardVideoPresenter.isCanUseFun)
                return@setOnClickListener
            animalResultPresenter.generateGIF { success, path ->
                if (success) {
                    val intent = Intent(AnimalResultActivity@ this, ShowGifActivity::class.java)
                    intent.putExtra("from", 1)
                    startActivity(intent)
                    loadingDialog?.dismiss()
                }
            }
        }
        if (!rewardVideoPresenter.isCanUseFun) {
            layout_vip.visibility = View.VISIBLE
            btn_pay.setOnClickListener {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "17").navigation()
            }
            btn_pay_watch.setOnClickListener {
                rewardVideoPresenter.tryToWatchAd()
            }
        }
        val w = AppUtil.getScreenW(this)
        val result = w - resources.getDimensionPixelOffset(R.dimen.change_48px) * 2
        val lp = layout_content.layoutParams
        lp.width = result
        lp.height = result
        layout_content.layoutParams = lp
        loadingDialog = LoadingDialog(this)
        sv_animal.post {
            animalResultPresenter.loadAnimal(sv_animal.width.toFloat(), sv_animal.height.toFloat())
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                animalResultPresenter.setProgress(progress / 2)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        if (!rewardVideoPresenter.isCanUseFun) {
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_page_f000, if (newPlan == "1") "1" else "2","5","")
            if (newPlan == "1") {
                if (GoCommonEnv.isFirstRunPayGuide) {
                    val config =
                        PayGuideManager.payGuideConfig.get(PayGuideManager.FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "17")
                        .withString("newPlan", "1").navigation()
                } else {
                    val config =
                        PayGuideManager.payGuideConfig.get(PayGuideManager.NOT_FIRST_LAUNCHER)!!
                    ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                        .withString("entrance", config.scen).withString("type", "17")
                        .withString("newPlan", "1").navigation()
                }
            } else {
                ARouter.getInstance().build(RouterConstants.ACTIVITY_PAY)
                    .withString("entrance", "17").navigation()
            }
        }else{
            BaseSeq103OperationStatistic.uploadOperationStatisticDataFor103(BaseSeq103OperationStatistic.animal_page_f000, if (newPlan == "1") "1" else "2","4","")
        }
    }


    override fun onStart() {
        super.onStart()
        if (!mIsReady)
            return
        if (mIsStop) {
            animalResultPresenter.start(sv_animal)
            animalResultPresenter.setProgress(seekbar.progress)
            mIsStop = false
        }
    }

    private var mIsStop: Boolean = false
    private var mIsReady = false

    override fun onStop() {
        super.onStop()
        mIsStop = true
        if (!mIsReady)
            return
        animalResultPresenter.stop()
        sv_animal?.visibility = View.INVISIBLE
    }

    override fun onAnimalLoadCompleted() {
        LogUtil.d("AnimalResultActivity", "onAnimalLoadCompleted")
        animalResultPresenter.start(sv_animal)
    }

    override fun onAnimalLoadFailed() {
    }

    override fun onReadyToStart() {
        mIsReady = true
        loadingDialog?.dismiss()
    }

    override fun showLoading() {
        loadingDialog?.show()
    }

    override fun showRewardVipView() {
        layout_vip.visibility = View.GONE
    }

    override fun withoutHotRating() {
        if (BillingServiceProxy.isVip()) {
            ExitAdManager.showAd()
        }
        finish()
    }

    override fun onPermissionGranted(code: Int) {
    }

    override fun onPermissionDenied(code: Int) {
    }

    override fun onPermissionEveryDenied(code: Int) {
    }

    override fun downloadSuccess() {
    }

    override fun downloadFail() {
    }

    override fun finish() {
        goHomeAndClearTop() {
            super.finish()
        }
    }
}